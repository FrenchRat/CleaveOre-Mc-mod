package com.cleaveore.mod.util;

import com.cleaveore.mod.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;

public final class OreClassifier {

    private OreClassifier() {
    }

    public static boolean isPluckableOre(BlockState state) {
        Block block = state.getBlock();
        Identifier id = Registries.BLOCK.getId(block);
        String path = id.getPath();

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

    public static Block getReplacementShell(BlockState state) {
        Block block = state.getBlock();
        Identifier id = Registries.BLOCK.getId(block);
        String path = id.getPath();

        if (block == Blocks.ANCIENT_DEBRIS) {
            return ModBlocks.HOLLOW_ANCIENT_DEBRIS_SHELL;
        }
        if (path.contains("deepslate")) {
            return ModBlocks.HOLLOW_DEEPSLATE_COAL_SHELL;
        }
        if (path.contains("nether")) {
            return ModBlocks.HOLLOW_NETHER_QUARTZ_SHELL;
        }
        return ModBlocks.HOLLOW_COAL_SHELL;
    }
}
