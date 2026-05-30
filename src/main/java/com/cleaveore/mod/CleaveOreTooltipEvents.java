package com.cleaveore.mod;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class CleaveOreTooltipEvents {

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (!CleaveOreConfig.get().showPickaxeTooltipHint) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (!isPickaxeLikeTool(stack)) {
            return;
        }
        event.getToolTip().add(Component.translatable("tooltip.cleaveore.pickaxe_hint").withStyle(ChatFormatting.DARK_GRAY));
    }

    private static boolean isPickaxeLikeTool(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.canPerformAction(ItemAbilities.PICKAXE_DIG)) {
            return true;
        }
        if (stack.isCorrectToolForDrops(Blocks.STONE.defaultBlockState())) {
            return true;
        }
        return Blocks.STONE.defaultBlockState().is(BlockTags.MINEABLE_WITH_PICKAXE)
            && stack.getDestroySpeed(Blocks.STONE.defaultBlockState()) > 1.0F;
    }
}
