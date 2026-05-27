package com.cleaveore.mod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.registry.Registries;

import java.util.List;

/**
 * Shell block left behind after ore pluck.
 *
 * These mine like stone/deepslate and drop a base building block so cleanup
 * after pluck still rewards normal mining.
 */
public class HollowOreShellBlock extends Block {

    private final boolean isDeepslate;

    public HollowOreShellBlock(Settings settings, boolean isDeepslate) {
        super(settings);
        this.isDeepslate = isDeepslate;
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        String path = Registries.BLOCK.getId(state.getBlock()).getPath();
        if (path.contains("ancient_debris")) {
            return List.of(new ItemStack(Blocks.ANCIENT_DEBRIS));
        }
        if (path.contains("nether")) {
            return List.of(new ItemStack(Blocks.NETHERRACK));
        }
        if (isDeepslate) {
            return List.of(new ItemStack(Blocks.COBBLED_DEEPSLATE));
        }
        return List.of(new ItemStack(Blocks.COBBLESTONE));
    }

    public boolean isDeepslate() {
        return isDeepslate;
    }
}
