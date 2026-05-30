# CleaveOre Regression Checklist

## Core Pluck
1. Right-click pluck works on standard ores.
2. Left-click vanilla mining behavior remains unchanged.
3. Shell block appears and can be mined immediately after pluck.

## Tier Rules
1. Wooden pick cannot pluck Nether gold ore.
2. Wooden pick cannot pluck Nether quartz ore.
3. Ancient Debris cannot be plucked by any pickaxe tier.

## Fail Feedback
1. Failed pluck plays low fail sound.
2. Failed pluck shows small dark-gray action-bar cue.
3. Failed pluck shows tiny fail-X particle effect.
4. Repeated spam-clicks respect cooldown and do not spam UI/particles.

## Drops / Enchants
1. Fortune/Silk behavior on pluck is correct for vanilla ores.
2. Shell cleanup drops expected base block.
3. Durability behavior follows configured chance.

## Compatibility
1. Modded pickaxe-like tools can pluck when valid.
2. Offhand shield/torch/lantern is blocked during valid pluck interactions.
3. Datapack tag overrides work:
   - `cleaveore:pluckable_ores`
   - `cleaveore:non_pluckable_ores`
