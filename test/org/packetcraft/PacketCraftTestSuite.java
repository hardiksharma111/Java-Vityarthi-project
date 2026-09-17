package org.packetcraft;

import org.packetcraft.model.IPv4Header;
import org.packetcraft.engine.ChecksumEngine;
import org.packetcraft.engine.FragmentEngine;
import org.packetcraft.engine.ReassemblyEngine;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class PacketCraftTestSuite {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   Running PacketCraft Unit & Integration Tests  ");
        System.out.println("=================================================");

        testChecksumComputationAndVerification();
        testChecksumCorruptedBitDetection();
        testFragmentationOffsetMath();
        testReassemblyIntegrity();
        testDontFragmentExceptionEnforcement();

        System.out.println("=================================================");
        System.out.println(String.format("Tests Finished. Passed: %d | Failed: %d", testsPassed, testsFailed));
        System.out.println("=================================================");

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println("[PASS] " + testName);
            testsPassed++;
        } else {
            System.out.println("[FAIL] " + testName);
            testsFailed++;
        }
    }

    private static void testChecksumComputationAndVerification() {
        try {
            IPv4Header hdr = new IPv4Header(0x1000, "192.168.0.1", "192.168.0.2");
            hdr.setTotalLength(20);
            int chk = ChecksumEngine.calculateChecksum(hdr.serializeWithoutChecksum());
            hdr.setHeaderChecksum(chk);
            assertTrue("testChecksumComputationAndVerification", 
                ChecksumEngine.verifyHeaderChecksum(hdr.serialize()));
        } catch (Exception e) {
            assertTrue("testChecksumComputationAndVerification Exception: " + e.getMessage(), false);
        }
    }

    private static void testChecksumCorruptedBitDetection() {
        try {
            IPv4Header hdr = new IPv4Header(0x1000, "192.168.0.1", "192.168.0.2");
            hdr.setTotalLength(20);
            hdr.setHeaderChecksum(ChecksumEngine.calculateChecksum(hdr.serializeWithoutChecksum()));
            byte[] wire = hdr.serialize();
            wire[0] ^= 0x01; // Corrupt version/IHL field
            assertTrue("testChecksumCorruptedBitDetection", 
                !ChecksumEngine.verifyHeaderChecksum(wire));
        } catch (Exception e) {
            assertTrue("testChecksumCorruptedBitDetection", false);
        }
    }

    private static void testFragmentationOffsetMath() {
        try {
            IPv4Header hdr = new IPv4Header(0x2000, "10.0.0.1", "10.0.0.2");
            byte[] payload = new byte[100];
            List<FragmentEngine.FragmentedDatagram> frags = FragmentEngine.fragment(hdr, payload, 40); // 20 hdr + 20 data -> aligns to 16 data
            assertTrue("testFragmentationOffsetMath (Chunk Count)", frags.size() == 7);
            assertTrue("testFragmentationOffsetMath (MF Flag First)", frags.get(0).getHeader().isMoreFragments());
            assertTrue("testFragmentationOffsetMath (MF Flag Last)", !frags.get(frags.size() - 1).getHeader().isMoreFragments());
        } catch (Exception e) {
            assertTrue("testFragmentationOffsetMath Exception: " + e.getMessage(), false);
        }
    }

    private static void testReassemblyIntegrity() {
        try {
            IPv4Header hdr = new IPv4Header(0x3000, "172.16.1.1", "172.16.1.2");
            byte[] original = "REASSEMBLY_TEST_PAYLOAD_STRING_ABCDEFGHIJKLMN".getBytes(StandardCharsets.UTF_8);
            List<FragmentEngine.FragmentedDatagram> frags = FragmentEngine.fragment(hdr, original, 28);
            byte[] recovered = ReassemblyEngine.reassemble(frags);
            assertTrue("testReassemblyIntegrity", java.util.Arrays.equals(original, recovered));
        } catch (Exception e) {
            assertTrue("testReassemblyIntegrity Exception: " + e.getMessage(), false);
        }
    }

    private static void testDontFragmentExceptionEnforcement() {
        try {
            IPv4Header hdr = new IPv4Header(0x4000, "10.1.1.1", "10.1.1.2");
            hdr.setDontFragment(true);
            byte[] payload = new byte[500];
            FragmentEngine.fragment(hdr, payload, 100);
            assertTrue("testDontFragmentExceptionEnforcement", false); // Should not reach here
        } catch (org.packetcraft.exception.FragmentationException fe) {
            assertTrue("testDontFragmentExceptionEnforcement", true);
        } catch (Exception e) {
            assertTrue("testDontFragmentExceptionEnforcement", false);
        }
    }
}
