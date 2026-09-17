package org.packetcraft.model;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPv4Header {
    private int version = 4;
    private int ihl = 5; // Internet Header Length (5 * 32-bit words = 20 bytes)
    private int typeOfService = 0;
    private int totalLength;
    private int identification;
    private boolean reservedFlag = false;
    private boolean dontFragment = false;
    private boolean moreFragments = false;
    private int fragmentOffset = 0; // In units of 8-byte blocks (13 bits)
    private int ttl = 64;
    private int protocol = 6; // Default to TCP (6)
    private int headerChecksum = 0;
    private InetAddress sourceAddress;
    private InetAddress destinationAddress;

    public IPv4Header(int identification, String srcIp, String dstIp) throws UnknownHostException {
        this.identification = identification;
        this.sourceAddress = InetAddress.getByName(srcIp);
        this.destinationAddress = InetAddress.getByName(dstIp);
    }

    public int getVersion() { return version; }
    public int getIhl() { return ihl; }
    public int getTypeOfService() { return typeOfService; }
    public void setTypeOfService(int tos) { this.typeOfService = tos; }
    public int getTotalLength() { return totalLength; }
    public void setTotalLength(int totalLength) { this.totalLength = totalLength; }
    public int getIdentification() { return identification; }
    public void setIdentification(int id) { this.identification = id; }
    public boolean isReservedFlag() { return reservedFlag; }
    public void setReservedFlag(boolean reservedFlag) { this.reservedFlag = reservedFlag; }
    public boolean isDontFragment() { return dontFragment; }
    public void setDontFragment(boolean df) { this.dontFragment = df; }
    public boolean isMoreFragments() { return moreFragments; }
    public void setMoreFragments(boolean mf) { this.moreFragments = mf; }
    public int getFragmentOffset() { return fragmentOffset; }
    public void setFragmentOffset(int offset) { this.fragmentOffset = offset; }
    public int getTtl() { return ttl; }
    public void setTtl(int ttl) { this.ttl = ttl; }
    public int getProtocol() { return protocol; }
    public void setProtocol(int protocol) { this.protocol = protocol; }
    public int getHeaderChecksum() { return headerChecksum; }
    public void setHeaderChecksum(int checksum) { this.headerChecksum = checksum; }
    public InetAddress getSourceAddress() { return sourceAddress; }
    public InetAddress getDestinationAddress() { return destinationAddress; }

    public byte[] serializeWithoutChecksum() {
        byte[] raw = new byte[ihl * 4];
        // Byte 0: Version (4 bits) + IHL (4 bits)
        raw[0] = (byte) (((version & 0x0F) << 4) | (ihl & 0x0F));
        // Byte 1: Type of Service
        raw[1] = (byte) (typeOfService & 0xFF);
        // Bytes 2-3: Total Length
        raw[2] = (byte) ((totalLength >>> 8) & 0xFF);
        raw[3] = (byte) (totalLength & 0xFF);
        // Bytes 4-5: Identification
        raw[4] = (byte) ((identification >>> 8) & 0xFF);
        raw[5] = (byte) (identification & 0xFF);
        
        // Bytes 6-7: Flags (3 bits) + Fragment Offset (13 bits)
        int flagsAndOffset = 0;
        if (reservedFlag) flagsAndOffset |= 0x8000;
        if (dontFragment) flagsAndOffset |= 0x4000;
        if (moreFragments) flagsAndOffset |= 0x2000;
        flagsAndOffset |= (fragmentOffset & 0x1FFF);
        raw[6] = (byte) ((flagsAndOffset >>> 8) & 0xFF);
        raw[7] = (byte) (flagsAndOffset & 0xFF);

        // Byte 8: TTL
        raw[8] = (byte) (ttl & 0xFF);
        // Byte 9: Protocol
        raw[9] = (byte) (protocol & 0xFF);
        // Bytes 10-11: Checksum zeroed during calculation
        raw[10] = 0;
        raw[11] = 0;

        // Bytes 12-15: Source IP
        byte[] src = sourceAddress.getAddress();
        System.arraycopy(src, 0, raw, 12, 4);

        // Bytes 16-19: Destination IP
        byte[] dst = destinationAddress.getAddress();
        System.arraycopy(dst, 0, raw, 16, 4);

        return raw;
    }

    public byte[] serialize() {
        byte[] packet = serializeWithoutChecksum();
        packet[10] = (byte) ((headerChecksum >>> 8) & 0xFF);
        packet[11] = (byte) (headerChecksum & 0xFF);
        return packet;
    }
}
