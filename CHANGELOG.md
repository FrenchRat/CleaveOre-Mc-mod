# Changelog

## v1.1.3-neoforge-1.21.1 / v1.1.2-1.20.1 loaders
- Added subtle fail feedback stack: low fail sound, dark-gray action-bar cue, tiny fail-X particles.
- Enforced Nether ore pluck harvest tiers (no wooden pluck bypass).
- Disabled Ancient Debris plucking.
- Removed hollow shell system and switched post-pluck replacement to base host blocks.
- Post-pluck host replacements now favor vanilla base materials (stone/deepslate/netherrack/blackstone/end stone).
- Added datapack tag controls:
  - `cleaveore:pluckable_ores`
  - `cleaveore:non_pluckable_ores`
- Added config file support via `config/cleaveore.json`.
- Added optional pickaxe tooltip hint.
- Added regression testing checklist docs.
