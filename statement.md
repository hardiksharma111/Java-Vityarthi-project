# Project Statement: PacketCraft CLI

## 1. Problem Statement
Network protocol analysis and low-level packet construction tools typically rely on heavy C/C++ native binaries (e.g., Scapy, libpcap, Wireshark). Developers and network engineering students often lack a standalone, platform-independent, zero-dependency environment to simulate the inner mechanics of protocol encapsulation, RFC 791 16-bit One's Complement Internet Checksum calculations, and Link-Layer Maximum Transmission Unit (MTU) fragmentation. 

Existing command-line tools either operate solely at the socket level without exposing raw bitwise header serialization or require platform-specific C runtime bindings. PacketCraft CLI fills this gap by delivering a pure Java, terminal-native packet synthesis and fragmentation workbench designed for offline protocol debugging and educational exploration.

## 2. Scope of the Project
The scope of PacketCraft CLI includes:
* Bitwise assembly and byte-level serialization of IPv4 headers and pseudo-transport segments.
* Mathematical calculation and automated verification of the RFC 791 16-bit One's Complement Internet Checksum over arbitrary headers.
* Dynamic payload fragmentation simulation based on user-defined Link-Layer MTUs (e.g., 576 bytes or 1500 bytes), correctly updating Identification, Total Length, More Fragments (MF) flags, and 8-byte step fragment offsets.
* Bitstream reconstruction and validation verifying that fragmented slices recombine losslessly into the original datagram.
* Structured Wireshark-style canonical hex/ASCII dump generation directly to standard terminal outputs (`stdout`).
* Out of Scope: Live promiscuous packet sniffing via raw kernel sockets and graphical network topology mapping.

## 3. Target Users
* Computer Science students studying computer networks, operating systems, and object-oriented programming.
* Systems engineers and security auditors needing an isolated, scriptable engine to model fragmented payloads without needing administrative root permissions on production interfaces.
* Software developers implementing network drivers or embedded communication protocols.

## 4. High-Level Features
* **Interactive CLI Terminal & Scripted Mode:** Execute single-line commands (`--craft`, `--fragment`, `--verify`) or utilize an interactive REPL shell.
* **Pure Bitwise Checksum Generator:** Computes and verifies 16-bit checksums, with options to deliberately inject bit-flips to observe verification failures.
* **RFC-Compliant Fragmentation Engine:** Automatically divides payloads into 8-byte aligned blocks, preserves L4 payload boundaries, and updates bitmask flags.
* **Lossless Reassembly Pipeline:** Buffers out-of-order packet fragments, checks completeness, and reconstructs the original contiguous payload.
* **Hex Engine Formatter:** Visualizes raw binary arrays as standardized offset-address hex grids with aligned printable ASCII characters.
