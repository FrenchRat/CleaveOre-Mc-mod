# CleaveOre

CleaveOre is currently targeted for popular NeoForge 1.21 builds:

- Minecraft: `1.21.1` and `1.21.4`
- Loader: `NeoForge`
- Java: `21`

## Controls (New)

- **Normal mine (left-click)**: vanilla break behavior (great for instant peeking/checking behind blocks).
- **Ore pluck (use/right-click on ore)**: cleaves ore drop out and leaves a hollow shell.
- **Tool requirement**: ore pluck only works when holding a pickaxe-capable tool (`PICKAXE_DIG` ability), including modded pickaxes/multitools that expose pickaxe behavior.
- **Offhand safety**: while ore-plucking with a main-hand pickaxe, offhand right-click actions (lantern/shield/etc.) are suppressed on that ore click.

## Keybind Compatibility

- Ore pluck uses Minecraft's standard **Use Item / Place Block** input path, so it follows user key remaps.
- CleaveOre also provides its own keybind category in Controls:
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

## Suggested Test Pass

1. Compare wooden/stone/iron/diamond/netherite pickaxe shell-break speed after pluck.
2. Test right-click pluck with common modded pickaxes/multitools.
3. Hold shield/lantern/torch in offhand and confirm no accidental placement/use while plucking.
4. Pluck each ore from different faces and verify drop direction looks correct.
5. Verify Fortune/Silk Touch outcomes on pluck for vanilla and modded ores.
6. Confirm durability trend is lower than full vanilla mining over 100+ plucks.

## Build

```powershell
.\gradlew.bat build
```

Default output jar (current default target: 1.21.1):

- `build/libs/cleaveore-1.1.1-beta-neoforge-1.21.1.jar`

Build both popular targets:

```powershell
.\scripts\build-neoforge-versions.ps1
```

Multi-version jars are copied into `dist/`.

## Install

1. Install NeoForge for the matching Minecraft version (`1.21.1` or `1.21.4`).
2. Put the matching `cleaveore-...` jar in your `mods` folder.
3. Launch with the matching NeoForge profile.

## Repository

- GitHub: [FrenchRat/CleaveOre-Mc-mod](https://github.com/FrenchRat/CleaveOre-Mc-mod)

## License

MIT
