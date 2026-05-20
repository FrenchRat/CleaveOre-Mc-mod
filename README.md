# CleaveOre

CleaveOre is available across the most-used loader/version targets in this repo:

- NeoForge `1.21.1` (Java `21`) - branch: `main`
- NeoForge `1.21.4` (Java `21`) - branch: `main`
- Fabric `1.20.1` (Java `17`) - branch: `fabric-1.20.1`
- Forge `1.20.1` (Java `17`) - branch: `forge-1.20.1`

## Controls

- **Normal mine (left-click)**: vanilla break behavior (great for instant peeking/checking behind blocks).
- **Ore pluck (use/right-click on ore)**: cleaves ore drop out and leaves a hollow shell.
- **Tool requirement**: ore pluck only works when holding a pickaxe-capable tool (`PICKAXE_DIG` ability), including modded pickaxes/multitools that expose pickaxe behavior.
- **Offhand safety**: while ore-plucking with a main-hand pickaxe, offhand right-click actions (lantern/shield/etc.) are suppressed on that ore click.

## Keybind Compatibility

- Ore pluck uses Minecraft's standard **Use Item / Place Block** input path, so it follows user key remaps.
- CleaveOre provides its own keybind category in Controls:
  - Category: `CleaveOre`
  - Entry: `Ore Pluck (Use Item)`

## Gameplay Flow

- Left-click mine if you want immediate visibility (peek utility).
- Right-click with a pickaxe if you want the cleave aesthetic + shell transition.
- Shell blocks mine faster than standard to keep the transition smooth.
- Ore drops eject from the clicked face direction for cleaner visual flow.
- Pluck durability is reduced versus full mining (60% chance to consume 1 durability).

## Compatibility Notes

- Pickaxe tiers and modded pickaxe traits are preserved for shell mining via proper tool tags.
- Pluck detection uses ore tags + ore-like naming fallback for broad mod compatibility.
- Mods that heavily override custom ore break logic may still behave differently on right-click pluck.

## Build

Build from the branch that matches your loader/version target.

```powershell
.\gradlew.bat build
```

Example default output jar on `main`:

- `build/libs/cleaveore-1.1.1-beta-neoforge-1.21.1.jar`

Build both NeoForge targets from `main`:

```powershell
.\scripts\build-neoforge-versions.ps1
```

Multi-version jars are copied into `dist/`.

## Releases

- NeoForge 1.21.1: `v1.1.1-beta-neoforge-1.21.1`
- NeoForge 1.21.4: build from `main` via `.\scripts\build-neoforge-versions.ps1`
- Fabric 1.20.1: `v1.1.1-beta-fabric-1.20.1`
- Forge 1.20.1: `v1.1.1-beta-forge-1.20.1`

Download jars from:

- [GitHub Releases](https://github.com/FrenchRat/CleaveOre-Mc-mod/releases)

## Install

1. Install the matching loader/version for the jar you downloaded.
2. Put the matching `cleaveore-...` jar in your `mods` folder.
3. Launch the game with that loader profile.

## Repository

- GitHub: [FrenchRat/CleaveOre-Mc-mod](https://github.com/FrenchRat/CleaveOre-Mc-mod)

## License

MIT
