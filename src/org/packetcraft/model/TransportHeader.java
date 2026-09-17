package org.packetcraft.model;

public abstract class TransportHeader {
    protected int sourcePort;
    protected int destinationPort;

    public TransportHeader(int sourcePort, int destinationPort) {
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
    }

    public int getSourcePort() { return sourcePort; }
    public int getDestinationPort() { return destinationPort; }
    public abstract int getProtocolNumber();
    public abstract byte[] serialize();
    public abstract int getHeaderLength();
}
