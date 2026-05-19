# CleaveOre (Beta)

CleaveOre is a Fabric mod for Minecraft 1.20.4 that changes ore mining into a two-step "cleave" flow:

1. Mine the ore quickly to pluck the valuable drop out.
2. Leave behind a hollow shell block you can clear afterward.

This makes ore extraction feel snappier while keeping normal cleanup and tool wear.

## Current Beta Features

- Fast stage-1 ore extraction (client + server synchronized to prevent rollback).
- Loot and enchant compatibility for Fortune and Silk Touch.
- XP handling for vanilla XP-dropping ores.
- Hollow shell replacement blocks (stone/deepslate/nether-like variants).
- Shader-friendly burst effects when ore is cleaved.
- Texture-pack compatibility for core burst visuals via block-state particles.
- Broad ore support for vanilla plus most modded ores.

## How It Works

When you break a supported ore:

- The mod intercepts the break on the server.
- It drops the ore loot immediately.
- It replaces the ore with a hollow shell block.
- It spawns a burst using the broken block's own texture particles plus glow accents.

Mining speed is boosted for stage-1 ore breaks on both sides:

- Client boost: `OreHardnessMixin`
- Server validation boost: `OreBreakSpeedMixin`

Using the same multiplier in both places avoids desync and anti-cheat rollback.

## Modded Ore Coverage

The ore classifier is intentionally broad to work in modpacks:

- Vanilla ore tags (coal, iron, copper, gold, redstone, emerald, lapis, diamond)
- Blocks extending `ExperienceDroppingBlock`
- Ancient debris
- Registry-name patterns like `_ore`, `ore_`, or `_ore_`

This catches most ore blocks from other mods without hardcoding each mod.

## Requirements

- Minecraft: `1.20.4`
- Fabric Loader: `>=0.15.0`
- Fabric API: `>=0.92.0`
- Java: `17`

## Build From Source

This repo now includes a Gradle wrapper.

```bash
./gradlew build
```

On Windows PowerShell:

```powershell
.\gradlew.bat build
```

Output jar:

- `build/libs/cleaveore-1.0.1.jar`

## Install

1. Install Fabric Loader for Minecraft 1.20.4.
2. Put `fabric-api` and this mod jar in your `.minecraft/mods` folder.
3. Launch using the Fabric profile.

## Notes

- This is a beta build and may need edge-case tuning for highly custom modded ores.
- Shell visuals are generic by host type (stone/deepslate/nether-like), not per-mod custom host rock.

## Repository

- GitHub: [FrenchRat/CleaveOre-Mc-mod](https://github.com/FrenchRat/CleaveOre-Mc-mod)

## License

MIT
