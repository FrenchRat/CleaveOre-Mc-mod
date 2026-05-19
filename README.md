# CleaveOre

CleaveOre changes ore mining into a two-step flow:

1. Break ore quickly to cleave out the valuable drop.
2. A hollow shell remains and can be mined after.

## Available Builds (Both)

### 1) NeoForge (Current)
- Minecraft: `1.21.1` (source target in `main`)
- Loader: `NeoForge`
- Java: `21`
- Source on `main` branch
- Output jar: `build/libs/cleaveore-1.1.0-beta-neoforge-1.21.1.jar`

Additional NeoForge release:
- `v1.1.0-beta-neoforge` for Minecraft `1.21.4`

### 2) Fabric (Legacy Beta)
- Minecraft: `1.20.4`
- Loader: `Fabric`
- Java: `17`
- Legacy release/tag: `v1.0.1-beta`

## Features

- Fast ore extraction phase so mining does not feel slower.
- Fortune/Silk Touch-compatible ore drops on cleave.
- Ore replaced by hollow shell blocks for cleanup mining.
- Shader-friendly particle burst on ore cleave.
- Broad mod compatibility with tag/class/name ore matching.

## Build (NeoForge 1.21.4)

```powershell
.\gradlew.bat build
```

## Install

### NeoForge 1.21.4
1. Install NeoForge for Minecraft 1.21.1 or 1.21.4 (matching the jar you downloaded).
2. Put the matching CleaveOre jar into your `mods` folder.
3. Launch with NeoForge profile.

### Fabric 1.20.4 (Legacy)
1. Install Fabric Loader for 1.20.4.
2. Install Fabric API.
3. Use the legacy `v1.0.1-beta` release jar in your `mods` folder.

## Repository

- GitHub: [FrenchRat/CleaveOre-Mc-mod](https://github.com/FrenchRat/CleaveOre-Mc-mod)

## License

MIT
