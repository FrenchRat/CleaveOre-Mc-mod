# CleaveOre Regression Checklist

## Core Pluck
1. Right-click pluck works on standard ores.
2. Left-click vanilla mining behavior remains unchanged.
3. Shell appears and can be mined immediately after pluck.

## Tier Rules
1. Wooden pick cannot pluck Nether gold ore.
2. Wooden pick cannot pluck Nether quartz ore.
3. Ancient Debris cannot be plucked.

## Fail Feedback
1. Failed pluck plays low fail sound.
2. Failed pluck shows dark-gray action-bar cue.
3. Failed pluck shows tiny fail-X particles.
4. Fail cooldown prevents spam feedback.

## Compatibility
1. Modded pickaxe-like tools still pluck where valid.
2. Datapack tags override pluckability:
   - `cleaveore:pluckable_ores`
   - `cleaveore:non_pluckable_ores`
