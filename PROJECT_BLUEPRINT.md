# Master Project Blueprint: PacketCraft CLI
**Course:** Programming in Java  
**Domain:** Network Protocol Engineering, Bitwise Data Serialization & Systems Modeling  
**Target Platform:** Pure Java Standard Edition (JDK 17 or higher), Zero External Dependencies, Strict CLI Execution  

---

## 1. Project Overview & Architecture
`PacketCraft CLI` is a headless terminal utility that models, constructs, validates, and fragments Layer 3 (IPv4) and Layer 4 (TCP/UDP) network packet headers down to raw binary representation. It provides full verification of the RFC 791 16-bit One's Complement Internet Checksum, performs Maximum Transmission Unit (MTU) fragmentation handling, calculates byte offset alignments, and renders real-time Wireshark-style canonical hex dumps directly in standard terminal output.

### 1.1 Architectural Module Boundaries
The project strictly implements a separation of concerns across custom classes and a dedicated test driver:

```text
packetcraft/
├── statement.md                         # Mandatory VITyarthi submission file
├── README.md                            # Complete setup, compilation & run guide
├── docs/
│   ├── architecture.md                  # Detailed design patterns & flowcharts
│   └── report_content.md                # 15-section academic report content
├── src/
│   └── org/
│       └── packetcraft/
│           ├── Main.java                # CLI command parser, REPL interface, and driver
│           ├── model/
│           │   ├── IPv4Header.java      # Layer 3 header state (TOS, TTL, Identification, Flags, IPs)
│           │   ├── TransportHeader.java # Abstract Layer 4 contract (TCP/UDP ports, sequence, flags)
│           │   └── UDPHeader.java       # Concrete Layer 4 UDP implementation
│           ├── engine/
│           │   ├── ChecksumEngine.java  # Bitwise RFC 791 16-bit one's complement algorithm
│           │   ├── FragmentEngine.java  # MTU slicing, offset tracking (8-byte steps), MF flag manipulation
│           │   └── ReassemblyEngine.java# Re-orders received byte buffers using fragment offsets
│           ├── formatting/
│           │   └── HexDumpFormatter.java# Formats byte buffers to canonical Wireshark-style hex/ASCII tables
│           └── exception/
│               ├── CorruptPacketException.java   # Checked exception: header underflow, invalid checksum
│               └── FragmentationException.java   # Checked exception: payload exceeds maximum offset boundaries
└── test/
   └── org/
       └── packetcraft/
           └── PacketCraftTestSuite.java         # Native CLI test harness (asserts logic without external JUnit)
```

---

## 2. Core Java Concepts & Design Patterns Exhibited
* **Bitwise Arithmetic & Bit Masking:** Extensive use of bitwise operators (`&`, `|`, `^`, `<<`, `>>>`) to pack and unpack variable-length IP fields (e.g., Version + IHL in 1 byte, 3-bit Flags + 13-bit Fragment Offset into 16 bits).
* **Buffer Management:** Network byte order (Big-Endian) binary serialization.
* **Inheritance & Abstraction:** Abstract `TransportHeader` base class extended by concrete protocol representations.
* **Custom Exception Hierarchy:** Distinct checked exceptions (`CorruptPacketException`, `FragmentationException`) preserving system stability upon encountering malformed input.
* **Stream Operations & String Formatting:** Canonical hex grid formatting through `String.format("%02X")` and printable ASCII boundary validation.

---

## 3. Submission Verification Checklist
* [x] `statement.md` created at repository root.
* [x] `README.md` with complete compilation and execution commands for Linux, macOS, and Windows.
* [x] All Java sources organized under `src/org/packetcraft/`.
* [x] Test suite organized under `test/org/packetcraft/` passing 100%.
* [x] Comprehensive documentation in `docs/`.
* [x] Incremental git commits reflecting realistic software development milestones.
