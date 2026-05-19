package com.veinmine.mod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;

import java.util.Collections;
import java.util.List;

/**
 * HollowOreShellBlock is the block left behind after stage-1 ore extraction.
 *
 * It looks like plain stone or deepslate (using vanilla textures defined in
 * resources/assets/veinmine/models/block/), mines at normal stone/deepslate
 * speed, and drops NOTHING — because the ore items already dropped in stage 1.
 *
 * Key design decisions:
 *   - No double-drops: the loot already came out during stage 1.
 *   - Fortune and Silk Touch have no effect (nothing left inside to enchant).
 *   - No XP drops: XP was already awarded in stage 1.
 *   - requiresTool() is inherited from settings, so you still need a pickaxe
 *     to clear the shell (can't punch it away, which would feel cheaty).
 */
public class HollowOreShellBlock extends Block {

    private final boolean isDeepslate;

    public HollowOreShellBlock(Settings settings, boolean isDeepslate) {
        super(settings);
        this.isDeepslate = isDeepslate;
    }

    /**
     * Returns an empty drop list so the shell gives the player nothing when mined.
     *
     * In 1.20.4, Block.getDroppedStacks() takes a LootContextParameterSet.Builder
     * (not a built LootContextParameterSet). The Builder pattern here lets vanilla
     * add additional context (like block entity data) before building. Since we
     * return early with an empty list, the builder is never actually used.
     */
    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return Collections.emptyList();
    }

    public boolean isDeepslate() {
        return isDeepslate;
    }
}
