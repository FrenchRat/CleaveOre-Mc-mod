package com.cleaveore.mod;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CleaveOreModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Particles are spawned with vanilla BLOCK + GLOW particle types,
        // so no client-side factory registration is required.
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (!CleaveOreConfig.get().showPickaxeTooltipHint) {
                return;
            }
            if (isPickaxeLikeTool(stack)) {
                lines.add(Text.translatable("tooltip.cleaveore.pickaxe_hint").formatted(Formatting.DARK_GRAY));
            }
        });
    }

    private static boolean isPickaxeLikeTool(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.isSuitableFor(Blocks.STONE.getDefaultState()) || stack.getMiningSpeedMultiplier(Blocks.STONE.getDefaultState()) > 1.0F;
    }
}
