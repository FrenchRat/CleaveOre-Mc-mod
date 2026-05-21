# CleaveOre

CleaveOre is currently targeted for:

- Minecraft: `1.21.1`
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

Output jar:

- `build/libs/cleaveore-1.1.1-neoforge-1.21.1.jar`

## Install

1. Install NeoForge for Minecraft 1.21.1.
2. Put `cleaveore-1.1.1-neoforge-1.21.1.jar` in your `mods` folder.
3. Launch with NeoForge 1.21.1 profile.

## Repository

- GitHub: [FrenchRat/CleaveOre-Mc-mod](https://github.com/FrenchRat/CleaveOre-Mc-mod)

## License

MIT
