package com.veinmine.mod.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class OreClassifier {

    private OreClassifier() {
    }

    public static boolean isPluckableOre(BlockState state) {
        Block block = state.getBlock();
        String path = BuiltInRegistries.BLOCK.getKey(block).getPath();

        if (block == Blocks.ANCIENT_DEBRIS) return true;
        if (block instanceof DropExperienceBlock) return true;

        if (state.is(BlockTags.COAL_ORES)
            || state.is(BlockTags.IRON_ORES)
            || state.is(BlockTags.COPPER_ORES)
            || state.is(BlockTags.GOLD_ORES)
            || state.is(BlockTags.REDSTONE_ORES)
            || state.is(BlockTags.EMERALD_ORES)
            || state.is(BlockTags.LAPIS_ORES)
            || state.is(BlockTags.DIAMOND_ORES)) {
            return true;
        }

        return path.endsWith("_ore") || path.startsWith("ore_") || path.contains("_ore_");
    }
}
