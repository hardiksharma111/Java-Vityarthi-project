package org.packetcraft.engine;

import org.packetcraft.exception.CorruptPacketException;

public class ChecksumEngine {

    /**
     * Calculates RFC 791 16-bit One's Complement Internet Checksum.
     */
    public static int calculateChecksum(byte[] headerBytes) {
        int length = headerBytes.length;
        int index = 0;
        long sum = 0;

        while (length > 1) {
            int high = headerBytes[index] & 0xFF;
            int low = headerBytes[index + 1] & 0xFF;
            int word16 = (high << 8) | low;
            sum += word16;
            index += 2;
            length -= 2;
        }

        // Handle odd length byte if present
        if (length > 0) {
            sum += (headerBytes[index] & 0xFF) << 8;
        }

        // Fold 32-bit sum to 16 bits
        while ((sum >>> 16) > 0) {
            sum = (sum & 0xFFFF) + (sum >>> 16);
        }

        // One's complement inversion
        return (int) (~sum & 0xFFFF);
    }

    /**
     * Validates an incoming serialized IP header including its embedded checksum field.
     * RFC 791 specifies that computing the checksum over the entire header containing
     * the valid checksum must result in 0x0000 (or inverted 0xFFFF depending on accumulator).
     */
    public static boolean verifyHeaderChecksum(byte[] headerWithChecksum) throws CorruptPacketException {
        if (headerWithChecksum == null || headerWithChecksum.length < 20) {
            throw new CorruptPacketException("Header byte array underflow: Expected >= 20 bytes.");
        }
        int calculated = calculateChecksum(headerWithChecksum);
        return calculated == 0;
    }
}
