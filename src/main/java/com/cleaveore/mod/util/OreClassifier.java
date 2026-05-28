package com.cleaveore.mod.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class OreClassifier {
    private static final TagKey<Block> PLUCKABLE_ORES = TagKey.of(RegistryKeys.BLOCK, new Identifier("cleaveore", "pluckable_ores"));
    private static final TagKey<Block> NON_PLUCKABLE_ORES = TagKey.of(RegistryKeys.BLOCK, new Identifier("cleaveore", "non_pluckable_ores"));

    private OreClassifier() {
    }

    public static boolean isPluckableOre(BlockState state) {
        Block block = state.getBlock();
        Identifier id = Registries.BLOCK.getId(block);
        String path = id.getPath();

        if (state.isIn(NON_PLUCKABLE_ORES)) {
            return false;
        }
        if (state.isIn(PLUCKABLE_ORES)) {
            return true;
        }
        if (block == Blocks.ANCIENT_DEBRIS) {
            return false;
        }
        if (block instanceof ExperienceDroppingBlock) {
            return true;
        }
        if (state.isIn(BlockTags.COAL_ORES)
            || state.isIn(BlockTags.IRON_ORES)
            || state.isIn(BlockTags.COPPER_ORES)
            || state.isIn(BlockTags.GOLD_ORES)
            || state.isIn(BlockTags.REDSTONE_ORES)
            || state.isIn(BlockTags.EMERALD_ORES)
            || state.isIn(BlockTags.LAPIS_ORES)
            || state.isIn(BlockTags.DIAMOND_ORES)) {
            return true;
        }
        return path.endsWith("_ore") || path.startsWith("ore_") || path.contains("_ore_");
    }

}
