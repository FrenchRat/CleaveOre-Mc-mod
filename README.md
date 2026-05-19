# VeinMine — Two-Stage Ore Mining Mod
### Fabric · Minecraft 1.20.4 · Java 17

---

## What Does This Mod Do?

VeinMine changes the fundamental feel of ore mining by splitting it into two distinct stages:

**Stage 1 — Pop the ore (fast):** When you mine any ore block, instead of the block disappearing, the ore "pops out" of the stone — the gems, ingots or dust drop immediately, and the block is replaced by a **hollow stone shell**. This first hit is roughly 6× faster than mining the full block normally. You hear a satisfying high-pitched crack, and a burst of soft glowing particles erupts from the face you mined.

**Stage 2 — Clear the shell (normal speed):** The hollow shell left behind mines at standard stone or deepslate speed and drops nothing (the ore already came out). You can leave shells as natural cave markers, or clean them up as you go.

---

## Why Does Stage 1 Feel Faster?

This is worth understanding because it's an elegant trick rather than a cheat. The client and server each independently simulate mining progress. On the **client** side, our `OreHardnessMixin` intercepts `calcBlockBreakingDelta` — the method the client calls every game tick to advance the crack overlay animation — and multiplies its result by `6.0`. This makes the progress bar fill roughly six times faster than stone normally would.

On the **server** side, our `OreBreakMixin` injects at the `HEAD` of `PlayerEntity#breakBlock`, which fires the moment the server confirms a block has been fully mined. We intercept ore blocks, drop their loot manually (respecting Fortune and Silk Touch), replace the block with a shell, spawn particles, and cancel the vanilla block removal. This means the server and client agree — the block was "broken" from the ore's perspective, so no rollback occurs.

---

## Shader Compatibility — How the Soft Particles Work

The `OrePopParticle` class is designed specifically to look great with shader packs like BSL, Complementary Reimagined, or Sildurs Vibrant Shaders. Here is how each design decision contributes to that:

**Translucent render layer.** By overriding `getType()` to return `PARTICLE_SHEET_TRANSLUCENT`, our particles are routed through the same shader pass as stained glass and water. This means any bloom, glow, depth-of-field, or lens flare effects in your shaderpack will automatically apply to the ore-pop particles — you get that gorgeous "glowing ore dust" look for free.

**Radial gradient textures.** The three particle textures (`ore_pop_0/1/2.png`) are procedurally generated 16×16 PNGs with a warm golden radial gradient — fully opaque at the centre, smoothly fading to transparent at the edges. This is the hallmark of "soft particles" that don't look like hard sprites clipping through geometry.

**Alpha envelope.** Each particle fades *in* over the first 3 ticks and *out* over the last 8 ticks, avoiding the jarring pop-in and pop-off that vanilla particles can have. Combined with air drag applied each tick, they float and settle like real dust.

**World collision.** `collidesWithWorld = true` keeps particles from clipping through floors or walls, which would shatter the illusion.

---

## Block Coverage

The mod handles all vanilla ore types:

| Ore family | Shell block |
|---|---|
| Coal, Iron, Gold, Diamond, Emerald, Lapis, Redstone, Copper | Stone-based shell (stone hardness) |
| Deepslate Coal/Iron/Gold/Diamond/Emerald/Lapis/Redstone/Copper | Deepslate-based shell (deepslate hardness) |
| Nether Gold, Nether Quartz | Netherrack-based shell |
| Ancient Debris | Ancient Debris shell |

Shell blocks look like plain stone/deepslate (they use vanilla textures), so they blend into the cave naturally. They require a pickaxe, drop nothing, and have no special behaviour.

---

## Building the Mod

You need **Java 17** and **Gradle** (the wrapper is included). No other tools are required.

```bash
# Clone or unzip this project, then:
cd veinmine

# Download Minecraft assets and generate Yarn mappings (first run takes a few minutes)
./gradlew genSources

# Build the mod jar
./gradlew build
```

The compiled `.jar` will be at:
```
build/libs/veinmine-1.0.0.jar
```

---

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.20.4.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) (version 0.96.4+ for 1.20.4).
3. Drop both `fabric-api-*.jar` and `veinmine-1.0.0.jar` into your `.minecraft/mods/` folder.
4. Launch Minecraft with the Fabric profile. Done!

### Optional but Recommended

Any shader pack that uses the Iris or Optifine pipeline will make the particles glow beautifully. **Complementary Reimagined** and **BSL Shaders** both handle the translucent particle layer especially well.

---

## Tuning / Customisation

The mod doesn't yet have a config file, but all the meaningful constants are clearly documented at the top of each relevant class:

- **Mining speed multiplier** (`OreHardnessMixin.java`): `ORE_SPEED_MULTIPLIER = 6.0f` — increase for faster, decrease toward 1.0 for close-to-vanilla speed.
- **Particle count** (`OreBreakMixin.java`): the `spawnParticles(...)` call uses count `22` — reduce for lighter effects.
- **Particle lifetime** (`OrePopParticle.java`): `maxAge = 18 + random.nextInt(8)` — increase for longer-lasting dust.
- **Particle colour** (`OrePopParticle.java`): `colorRed/Green/Blue` — change to match a specific ore type, or extend the particle class to accept per-ore colour parameters.

---

## How to Add Modded Ores

To support a modded ore (e.g. from Thermal Foundation or Create), you need to:

1. Register a new shell block in `ModBlocks.java`.
2. Add an entry to the `ORE_SHELL_MAP` or add an `instanceof` check in `getShellFor()` inside `OreBreakMixin.java`.
3. Add blockstate, block model, item model, and lang JSON files (follow the existing pattern in `resources/assets/veinmine/`).

Since `getShellFor()` walks the class hierarchy, any modded ore that *extends* a vanilla ore class (e.g. `class MyGoldOre extends GoldOreBlock`) will automatically be caught by the existing mappings.

---

## Project Structure

```
src/main/java/com/veinmine/mod/
├── VeinMineMod.java          ← Server entrypoint, calls all registries
├── VeinMineModClient.java    ← Client entrypoint, registers particle factory
├── block/
│   └── HollowOreShellBlock.java    ← The empty shell block (drops nothing)
├── mixin/
│   ├── OreBreakMixin.java          ← Core logic: intercepts ore breaking, places shell
│   └── OreHardnessMixin.java       ← Client: makes ore mining feel 6× faster
├── particle/
│   └── OrePopParticle.java         ← Shader-compatible soft glow particle
└── registry/
    ├── ModBlocks.java              ← Registers all 19 shell block variants
    └── ModParticles.java           ← Registers the ore_pop particle type
```

---

## Licence

MIT — do whatever you like with this code, attribution appreciated but not required.
