package org.packetcraft.formatting;

public class HexDumpFormatter {

    /**
     * Renders standard Wireshark-style memory hex grids.
     * Example: 0000   45 00 00 3c 1c 46 40 00  40 06 b1 e6 c0 a8 01 02   |E..<.@.@........|
     */
    public static String formatHexDump(byte[] data) {
        if (data == null || data.length == 0) return "<empty buffer>\n";
        
        StringBuilder sb = new StringBuilder();
        int length = data.length;

        for (int i = 0; i < length; i += 16) {
            // Offset memory address
            sb.append(String.format("%04X   ", i));

            // Hex values
            for (int j = 0; j < 16; j++) {
                if (i + j < length) {
                    sb.append(String.format("%02X ", data[i + j]));
                } else {
                    sb.append("   ");
                }
                if (j == 7) sb.append(" ");
            }

            sb.append("  |");

            // Printable ASCII representation
            for (int j = 0; j < 16; j++) {
                if (i + j < length) {
                    byte b = data[i + j];
                    if (b >= 32 && b <= 126) {
                        sb.append((char) b);
                    } else {
                        sb.append('.');
                    }
                }
            }
            sb.append("|\n");
        }
        return sb.toString();
    }
}
