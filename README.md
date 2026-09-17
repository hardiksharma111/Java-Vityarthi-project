# PacketCraft CLI
> Headless IPv4 Header Serializer, Checksum Engine & MTU Fragmentation Simulator

PacketCraft CLI is a pure Java engineering utility that models RFC 791 Internet Protocol operations. It computes 16-bit One's Complement Internet Checksums, divides payloads over link-layer MTU boundaries into 8-byte aligned offsets, tests data corruption detection, and outputs canonical hex dumps.

---

## Technical Features
* **Zero Dependencies:** Compiles with standard OpenJDK (`javac`) without external Maven/Gradle builds or GUI libraries.
* **RFC 791 Checksum Engine:** Full 16-bit one's complement folding and byte array verification.
* **Fragmentation Pipeline:** Splits payloads according to arbitrary MTUs, enforces `DF` (Don't Fragment) flags, updates `MF` (More Fragments) status, and calculates fragment offsets.
* **Lossless Reassembly:** Validates fragment continuity and verifies data reconstruction.
* **Hex Engine:** Wireshark-standard 16-byte memory dump with printable ASCII gutter columns.

---

## Directory Layout
```text
.
├── statement.md                         # Mandatory submission problem statement
├── README.md                            # Setup, compilation & usage guide
├── PROJECT_BLUEPRINT.md                 # Master blueprint specification
├── docs/
│   ├── architecture.md                  # Detailed design patterns & flowcharts
│   └── report_content.md                # 15-section academic project report
├── src/
│   └── org/packetcraft/
│       ├── Main.java                    # Interactive CLI & automated demo driver
│       ├── model/
│       │   ├── IPv4Header.java          # Layer 3 IPv4 header state & bitwise serialization
│       │   ├── TransportHeader.java     # Abstract Layer 4 contract
│       │   └── UDPHeader.java           # Concrete UDP Layer 4 implementation
│       ├── engine/
│       │   ├── ChecksumEngine.java      # RFC 791 16-bit One's Complement algorithm
│       │   ├── FragmentEngine.java      # MTU slicing with 8-byte offset alignment
│       │   └── ReassemblyEngine.java    # Lossless in-order reconstruction engine
│       ├── formatting/
│       │   └── HexDumpFormatter.java    # Wireshark-style canonical hex/ASCII formatter
│       └── exception/
│           ├── CorruptPacketException.java
│           └── FragmentationException.java
└── test/
    └── org/packetcraft/
        └── PacketCraftTestSuite.java    # Self-contained native test harness
```

---

## Setup & Compilation Instructions

### Prerequisites
* Java Development Kit (JDK 17 or later)
* Standard Command Prompt, PowerShell, or Bash terminal

### 1. Compile the Source Code

#### Linux / macOS (Bash):
```bash
mkdir -p bin
javac -d bin $(find src -name "*.java")
```

#### Windows (PowerShell):
```powershell
if (!(Test-Path "bin")) { New-Item -ItemType Directory -Path "bin" }
javac -d bin (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

#### Windows (Command Prompt):
```cmd
if not exist bin mkdir bin
dir /s /b src\*.java > sources.txt
javac -d bin @sources.txt
del sources.txt
```

---

### 2. Run the Interactive CLI Workbench

```bash
java -cp bin org.packetcraft.Main
```

To run the automated demonstration mode directly:
```bash
java -cp bin org.packetcraft.Main --demo
```

---

### 3. Run the Automated Test Suite

#### Compile and run the test harness:

##### Linux / macOS (Bash):
```bash
javac -d bin -cp bin $(find test -name "*.java")
java -cp bin org.packetcraft.PacketCraftTestSuite
```

##### Windows (PowerShell):
```powershell
javac -d bin -cp bin (Get-ChildItem -Recurse -Filter *.java test | ForEach-Object { $_.FullName })
java -cp bin org.packetcraft.PacketCraftTestSuite
```

---

## Example Interactive Session

```plaintext
=========================================================
   PacketCraft CLI - Layer 3 Protocol Engineering Tool   
=========================================================

Select an operational mode:
 1. Craft IPv4 Packet & Calculate Checksum
 2. Simulate MTU Fragmentation & Reassembly
 3. Inject Bit-Flip Error & Test Verification Engine
 4. Run Built-in Automated System Demonstration
 5. Exit
packetcraft> 1
Enter Source IP (e.g. 192.168.1.100): 192.168.1.50
Enter Dest IP (e.g. 10.0.0.1): 8.8.8.8
Enter Payload Text: HELLO_NETWORK_WORLD

[+] Computed RFC 791 16-Bit Checksum: 0x2A1C
[+] Canonical Wire Dump (39 bytes):
0000   45 00 00 27 BE EF 00 00  40 06 2A 1C C0 A8 01 32   |E..'....@.*....2|
0010   08 08 08 08 48 45 4C 4C  4F 5F 4E 45 54 57 4F 52   |....HELLO_NETWOR|
0020   4B 5F 57 4F 52 4C 44                               |K_WORLD|
```
