package org.packetcraft.exception;

public class CorruptPacketException extends Exception {
    public CorruptPacketException(String message) {
        super(message);
    }

    public CorruptPacketException(String message, Throwable cause) {
        super(message, cause);
    }
}
