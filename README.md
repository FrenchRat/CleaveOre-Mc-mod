# CleaveOre

CleaveOre is currently maintained for one target only:

- Minecraft: `1.21.1`
- Loader: `NeoForge`
- Java: `21`

## What It Does

CleaveOre changes ore mining into two steps:

1. Break ore quickly to cleave out drops.
2. A hollow shell remains and can be mined afterward.

Behavior details:

- Faster ore extraction phase (so mining does not feel slower).
- Fortune/Silk Touch-compatible drop behavior.
- Ore block replaced by a shell block for cleanup.
- Shader-friendly particle burst on cleave.
- Broad ore support using tags + block class + name fallback.

## Build

```powershell
.\gradlew.bat build
```

Output jar:

- `build/libs/cleaveore-1.1.1-beta-neoforge-1.21.1.jar`

## Install

1. Install NeoForge for Minecraft 1.21.1.
2. Put `cleaveore-1.1.1-beta-neoforge-1.21.1.jar` in your `mods` folder.
3. Launch with a NeoForge 1.21.1 profile.

## Repository

- GitHub: [FrenchRat/CleaveOre-Mc-mod](https://github.com/FrenchRat/CleaveOre-Mc-mod)

## License

MIT
