package org.packetcraft;

import org.packetcraft.model.IPv4Header;
import org.packetcraft.engine.ChecksumEngine;
import org.packetcraft.engine.FragmentEngine;
import org.packetcraft.engine.ReassemblyEngine;
import org.packetcraft.formatting.HexDumpFormatter;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("   PacketCraft CLI - Layer 3 Protocol Engineering Tool   ");
        System.out.println("=========================================================");

        if (args.length > 0 && args[0].equals("--demo")) {
            runAutomatedDemo();
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nSelect an operational mode:");
            System.out.println(" 1. Craft IPv4 Packet & Calculate Checksum");
            System.out.println(" 2. Simulate MTU Fragmentation & Reassembly");
            System.out.println(" 3. Inject Bit-Flip Error & Test Verification Engine");
            System.out.println(" 4. Run Built-in Automated System Demonstration");
            System.out.println(" 5. Exit");
            System.out.print("packetcraft> ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleCraftPacket(scanner);
                    break;
                case "2":
                    handleFragmentation(scanner);
                    break;
                case "3":
                    handleCorruptionSimulation();
                    break;
                case "4":
                    runAutomatedDemo();
                    break;
                case "5":
                    System.out.println("Terminating PacketCraft CLI. Goodbye.");
                    return;
                default:
                    System.out.println("[!] Invalid selection. Enter 1-5.");
            }
        }
    }

    private static void handleCraftPacket(Scanner sc) {
        try {
            System.out.print("Enter Source IP (e.g. 192.168.1.100): ");
            String src = sc.nextLine().trim();
            System.out.print("Enter Dest IP (e.g. 10.0.0.1): ");
            String dst = sc.nextLine().trim();
            System.out.print("Enter Payload Text: ");
            String text = sc.nextLine();

            byte[] payload = text.getBytes(StandardCharsets.UTF_8);
            IPv4Header header = new IPv4Header(0xBEEF, src, dst);
            header.setTotalLength(20 + payload.length);

            int checksum = ChecksumEngine.calculateChecksum(header.serializeWithoutChecksum());
            header.setHeaderChecksum(checksum);

            byte[] rawHeader = header.serialize();
            byte[] fullDatagram = new byte[rawHeader.length + payload.length];
            System.arraycopy(rawHeader, 0, fullDatagram, 0, rawHeader.length);
            System.arraycopy(payload, 0, fullDatagram, rawHeader.length, payload.length);

            System.out.println("\n[+] Computed RFC 791 16-Bit Checksum: 0x" + String.format("%04X", checksum));
            System.out.println("[+] Canonical Wire Dump (" + fullDatagram.length + " bytes):");
            System.out.println(HexDumpFormatter.formatHexDump(fullDatagram));

        } catch (Exception e) {
            System.out.println("[-] Error crafting packet: " + e.getMessage());
        }
    }

    private static void handleFragmentation(Scanner sc) {
        try {
            System.out.print("Enter target MTU in bytes (e.g. 68, 576, 1500): ");
            int mtu = Integer.parseInt(sc.nextLine().trim());
            
            String longMessage = "NETWORK_ENGINEERING_DATA_STREAM: The Internet Protocol is designed for use in " +
                                 "packet-switched network communication environments. Packet fragmentation happens " +
                                 "whenever an intermediate link has a Maximum Transmission Unit lower than packet length.";
            byte[] payload = longMessage.getBytes(StandardCharsets.UTF_8);

            IPv4Header baseHeader = new IPv4Header(0x4A21, "172.16.0.4", "198.51.100.25");
            List<FragmentEngine.FragmentedDatagram> frags = FragmentEngine.fragment(baseHeader, payload, mtu);

            System.out.println("\n[+] Total Payload: " + payload.length + " bytes. Fragmented into " + frags.size() + " segments.");
            for (int i = 0; i < frags.size(); i++) {
                FragmentEngine.FragmentedDatagram f = frags.get(i);
                IPv4Header h = f.getHeader();
                System.out.println(String.format("--> Fragment #%d | Length: %d | MF: %b | Offset: %d (Byte %d)",
                    (i + 1), h.getTotalLength(), h.isMoreFragments(), h.getFragmentOffset(), (h.getFragmentOffset() * 8)));
                System.out.println(HexDumpFormatter.formatHexDump(f.toByteArray()));
            }

            System.out.println("[*] Simulating Destination Reassembly Pipeline...");
            byte[] reassembled = ReassemblyEngine.reassemble(frags);
            String recovered = new String(reassembled, StandardCharsets.UTF_8);
            System.out.println("[+] Reassembly Output Matches Original: " + recovered.equals(longMessage));

        } catch (Exception e) {
            System.out.println("[-] Fragmentation failed: " + e.getMessage());
        }
    }

    private static void handleCorruptionSimulation() {
        try {
            System.out.println("\n[*] Synthesizing valid packet with IP: 10.0.0.1 -> 10.0.0.2");
            IPv4Header header = new IPv4Header(0x1234, "10.0.0.1", "10.0.0.2");
            header.setTotalLength(20);
            int chk = ChecksumEngine.calculateChecksum(header.serializeWithoutChecksum());
            header.setHeaderChecksum(chk);

            byte[] wire = header.serialize();
            System.out.println("[+] Original Valid Wire Checksum Verification: " + 
                ChecksumEngine.verifyHeaderChecksum(wire));

            System.out.println("[!] Injecting single-bit flip corruption at index 8 (TTL byte)...");
            wire[8] ^= 0x01; // Bit flip in TTL

            boolean verified = ChecksumEngine.verifyHeaderChecksum(wire);
            System.out.println("[-] Checksum Verification After Bit Corruption: " + verified);
            if (!verified) {
                System.out.println("[+] Verification engine successfully detected data corruption!");
            }
        } catch (Exception e) {
            System.out.println("[-] Engine error: " + e.getMessage());
        }
    }

    public static void runAutomatedDemo() {
        System.out.println("\n>>> EXECUTING AUTOMATED FULL-FLOW DEMONSTRATION <<<");
        try {
            IPv4Header header = new IPv4Header(0x7777, "192.168.1.1", "192.168.1.254");
            byte[] sampleData = "SYSTEM_TEST_BUFFER_HELLO_PACKETCRAFT".getBytes(StandardCharsets.UTF_8);
            header.setTotalLength(20 + sampleData.length);
            header.setHeaderChecksum(ChecksumEngine.calculateChecksum(header.serializeWithoutChecksum()));

            System.out.println("[Step 1] Synthesized Packet:");
            System.out.println(HexDumpFormatter.formatHexDump(header.serialize()));

            System.out.println("[Step 2] Executing MTU 28 Fragmentation (Header: 20, Data: 8)...");
            List<FragmentEngine.FragmentedDatagram> frags = FragmentEngine.fragment(header, sampleData, 28);
            System.out.println("Produced " + frags.size() + " fragments.");

            System.out.println("[Step 3] Reassembling Datagram...");
            byte[] recovered = ReassemblyEngine.reassemble(frags);
            System.out.println("Integrity Check Passed: " + java.util.Arrays.equals(sampleData, recovered));
            System.out.println(">>> DEMO COMPLETE <<<\n");
        } catch (Exception e) {
            System.out.println("Demo exception: " + e.getMessage());
        }
    }
}
