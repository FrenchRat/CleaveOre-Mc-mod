# CleaveOre

CleaveOre is currently targeted for:

- Minecraft: `1.21.1`
- Loader: `NeoForge`
- Java: `21`

## Controls (New)

- **Normal mine (left-click)**: vanilla break behavior (great for instant peeking/checking behind blocks).
- **Ore pluck (use/right-click on ore)**: cleaves ore drop out and leaves a hollow shell.
- **Tool requirement**: ore pluck only works when holding a pickaxe-capable tool (`PICKAXE_DIG` ability), including modded pickaxes/multitools that expose pickaxe behavior.

## Keybind Compatibility

- Ore pluck uses Minecraft's standard **Use Item / Place Block** input path, so it follows user key remaps.
- CleaveOre also provides its own keybind category in Controls:
  - Category: `CleaveOre`
  - Entry: `Ore Pluck (Use Item)`

## Gameplay Flow

- Left-click mine if you want immediate visibility (peek utility).
- Right-click with a pickaxe if you want the cleave aesthetic + shell transition.
- Shell blocks mine faster than standard to keep the transition smooth.

## Build

```powershell
.\gradlew.bat build
```

Output jar:

- `build/libs/cleaveore-1.1.1-beta-neoforge-1.21.1.jar`

## Install

1. Install NeoForge for Minecraft 1.21.1.
2. Put `cleaveore-1.1.1-beta-neoforge-1.21.1.jar` in your `mods` folder.
3. Launch with NeoForge 1.21.1 profile.

## Repository

- GitHub: [FrenchRat/CleaveOre-Mc-mod](https://github.com/FrenchRat/CleaveOre-Mc-mod)

## License

MIT
