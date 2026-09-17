package org.packetcraft.engine;

import org.packetcraft.model.IPv4Header;
import org.packetcraft.exception.FragmentationException;
import java.util.ArrayList;
import java.util.List;

public class FragmentEngine {

    public static class FragmentedDatagram {
        private final IPv4Header header;
        private final byte[] payload;

        public FragmentedDatagram(IPv4Header header, byte[] payload) {
            this.header = header;
            this.payload = payload;
        }

        public IPv4Header getHeader() { return header; }
        public byte[] getPayload() { return payload; }

        public byte[] toByteArray() {
            byte[] hdr = header.serialize();
            byte[] out = new byte[hdr.length + payload.length];
            System.arraycopy(hdr, 0, out, 0, hdr.length);
            System.arraycopy(payload, 0, out, hdr.length, payload.length);
            return out;
        }
    }

    /**
     * Splits an existing IPv4 packet payload into valid MTU-constrained segments.
     * Enforces strict 8-byte boundary alignments for fragment offset calculations.
     */
    public static List<FragmentedDatagram> fragment(IPv4Header baseHeader, byte[] fullPayload, int mtu) 
            throws FragmentationException {
        
        int headerLen = baseHeader.getIhl() * 4;
        if (mtu < headerLen + 8) {
            throw new FragmentationException("MTU " + mtu + " is too small. Must accommodate at least header + 8 bytes.");
        }

        if (baseHeader.isDontFragment() && (headerLen + fullPayload.length) > mtu) {
            throw new FragmentationException("Cannot fragment packet: DF (Don't Fragment) flag is enabled.");
        }

        List<FragmentedDatagram> fragments = new ArrayList<>();
        int maxDataPerFragment = (mtu - headerLen) & ~0x07; // Round down to nearest multiple of 8
        int remainingBytes = fullPayload.length;
        int currentOffset = 0;

        while (remainingBytes > 0) {
            int chunk = Math.min(remainingBytes, maxDataPerFragment);
            boolean isLast = (remainingBytes - chunk) == 0;

            try {
                IPv4Header fragHeader = new IPv4Header(
                    baseHeader.getIdentification(),
                    baseHeader.getSourceAddress().getHostAddress(),
                    baseHeader.getDestinationAddress().getHostAddress()
                );
                fragHeader.setTtl(baseHeader.getTtl());
                fragHeader.setProtocol(baseHeader.getProtocol());
                fragHeader.setTypeOfService(baseHeader.getTypeOfService());
                fragHeader.setFragmentOffset(currentOffset / 8);
                fragHeader.setMoreFragments(!isLast);
                fragHeader.setTotalLength(headerLen + chunk);

                // Compute fresh checksum for the specific fragment header
                byte[] rawHeader = fragHeader.serializeWithoutChecksum();
                int chk = ChecksumEngine.calculateChecksum(rawHeader);
                fragHeader.setHeaderChecksum(chk);

                byte[] fragPayload = new byte[chunk];
                System.arraycopy(fullPayload, currentOffset, fragPayload, 0, chunk);

                fragments.add(new FragmentedDatagram(fragHeader, fragPayload));

                currentOffset += chunk;
                remainingBytes -= chunk;

            } catch (Exception e) {
                throw new FragmentationException("Error synthesizing fragment header: " + e.getMessage());
            }
        }

        return fragments;
    }
}
