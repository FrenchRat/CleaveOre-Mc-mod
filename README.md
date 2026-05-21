# CleaveOre (NeoForge 1.20.1 Compat Build)

This branch provides a **1.20.1 compatibility jar** built from the Forge 47.2.0 codebase.

## Important

- Minecraft: `1.20.1`
- Primary loader target: `Forge 47.2.0+`
- Legacy compatibility target: `NeoForge 1.20.1` (where Forge-compatible 1.20.1 mods are accepted)
- Java: `17`

This is a compatibility build for users who specifically run a NeoForge-labeled 1.20.1 setup.

## Features

- Right-click ore pluck with pickaxe-capable tools
- Left-click normal mining preserved for peeking
- Fast shell transition block after pluck
- Texture-pack friendly particles and drop ejection from clicked face
- Offhand right-click suppression during pluck interaction

## Build

```powershell
.\gradlew.bat clean build
```

Output jar:

- `build/libs/cleaveore-1.1.2-beta-neoforge-1.20.1-compat.jar`

## Install

1. Use a Minecraft `1.20.1` profile.
2. Install Forge `47.2.0+` or a NeoForge 1.20.1 environment that accepts Forge-compatible mods.
3. Place this jar in your `mods` folder.

## Repository

- GitHub: [FrenchRat/CleaveOre-Mc-mod](https://github.com/FrenchRat/CleaveOre-Mc-mod)

## License

MIT
