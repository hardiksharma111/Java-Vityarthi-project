package org.packetcraft.engine;

import org.packetcraft.exception.CorruptPacketException;
import java.util.Comparator;
import java.util.List;

public class ReassemblyEngine {

    /**
     * Verifies and reassembles a collection of fragments into one contiguous payload buffer.
     */
    public static byte[] reassemble(List<FragmentEngine.FragmentedDatagram> fragments) throws CorruptPacketException {
        if (fragments == null || fragments.isEmpty()) {
            throw new CorruptPacketException("Reassembly failed: No fragments provided.");
        }

        // Sort fragments by fragment offset ascending
        fragments.sort(Comparator.comparingInt(f -> f.getHeader().getFragmentOffset()));

        // Calculate expected total data size
        int totalPayloadBytes = 0;
        int expectedOffset = 0;

        for (int i = 0; i < fragments.size(); i++) {
            FragmentEngine.FragmentedDatagram frag = fragments.get(i);
            int offsetBytes = frag.getHeader().getFragmentOffset() * 8;

            if (offsetBytes != expectedOffset) {
                throw new CorruptPacketException("Reassembly gap detected: Expected byte offset " 
                    + expectedOffset + " but found " + offsetBytes);
            }

            expectedOffset += frag.getPayload().length;
            totalPayloadBytes += frag.getPayload().length;

            if (i == fragments.size() - 1 && frag.getHeader().isMoreFragments()) {
                throw new CorruptPacketException("Corrupted sequence: Final fragment still asserts MF=true.");
            }
        }

        byte[] reconstructed = new byte[totalPayloadBytes];
        int writeCursor = 0;
        for (FragmentEngine.FragmentedDatagram frag : fragments) {
            System.arraycopy(frag.getPayload(), 0, reconstructed, writeCursor, frag.getPayload().length);
            writeCursor += frag.getPayload().length;
        }

        return reconstructed;
    }
}
