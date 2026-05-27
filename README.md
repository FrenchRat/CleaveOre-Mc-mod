# CleaveOre

CleaveOre adds two-stage ore mining.

- Left-click mine: vanilla mining behavior.
- Right-click pluck (with pickaxe-capable tools): pops ore out, leaves a shell block.

## Supported Releases

- NeoForge 1.21.1: `v1.1.3-beta-neoforge-1.21.1`\r\n- NeoForge 1.20.1: `v1.1.2-beta-neoforge-1.20.1-compat`
- Forge 1.20.1: `v1.1.2-beta-forge-1.20.1`
- Fabric 1.20.1: `v1.1.2-beta-fabric-1.20.1`

## Download

Use the GitHub Releases page:

- [Releases](https://github.com/FrenchRat/CleaveOre-Mc-mod/releases)

Pick the release for your loader, then download the `.jar` named like:

- `cleaveore-1.1.2-beta-neoforge-1.20.1-compat.jar`
- `cleaveore-1.1.2-beta-forge-1.20.1.jar`
- `cleaveore-1.1.2-beta-fabric-1.20.1.jar`

## Why You Saw "2 Assets"

That was duplicate mod jars in the same release (old filename + new filename). I cleaned those up.
Now each release keeps one mod `.jar` upload. GitHub may also show auto-generated source archives separately.

## Install

1. Install the correct loader for your Minecraft 1.20.1 setup.
2. Put the matching CleaveOre `.jar` into `.minecraft/mods`.
3. Launch the game with that loader profile.

## Build

```powershell
.\gradlew.bat build
```

## License

MIT