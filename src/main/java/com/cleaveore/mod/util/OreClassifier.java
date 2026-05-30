package com.cleaveore.mod.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class OreClassifier {
    private static final TagKey<Block> PLUCKABLE_ORES = TagKey.create(
        BuiltInRegistries.BLOCK.key(),
        ResourceLocation.fromNamespaceAndPath("cleaveore", "pluckable_ores")
    );
    private static final TagKey<Block> NON_PLUCKABLE_ORES = TagKey.create(
        BuiltInRegistries.BLOCK.key(),
        ResourceLocation.fromNamespaceAndPath("cleaveore", "non_pluckable_ores")
    );

    private OreClassifier() {
    }

    public static boolean isPluckableOre(BlockState state) {
        Block block = state.getBlock();
        String path = BuiltInRegistries.BLOCK.getKey(block).getPath();

        if (state.is(NON_PLUCKABLE_ORES)) return false;
        if (state.is(PLUCKABLE_ORES)) return true;
        if (block == Blocks.ANCIENT_DEBRIS) return false;
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

