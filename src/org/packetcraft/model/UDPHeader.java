package org.packetcraft.model;

public class UDPHeader extends TransportHeader {
    private int length;
    private int checksum = 0;

    public UDPHeader(int sourcePort, int destinationPort, int payloadLength) {
        super(sourcePort, destinationPort);
        this.length = 8 + payloadLength;
    }

    @Override
    public int getProtocolNumber() {
        return 17; // UDP Protocol Number
    }

    @Override
    public int getHeaderLength() {
        return 8; // UDP Header is always 8 bytes
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getChecksum() {
        return checksum;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    @Override
    public byte[] serialize() {
        byte[] raw = new byte[8];
        // Bytes 0-1: Source Port
        raw[0] = (byte) ((sourcePort >>> 8) & 0xFF);
        raw[1] = (byte) (sourcePort & 0xFF);
        // Bytes 2-3: Destination Port
        raw[2] = (byte) ((destinationPort >>> 8) & 0xFF);
        raw[3] = (byte) (destinationPort & 0xFF);
        // Bytes 4-5: Length
        raw[4] = (byte) ((length >>> 8) & 0xFF);
        raw[5] = (byte) (length & 0xFF);
        // Bytes 6-7: Checksum
        raw[6] = (byte) ((checksum >>> 8) & 0xFF);
        raw[7] = (byte) (checksum & 0xFF);
        return raw;
    }
}
