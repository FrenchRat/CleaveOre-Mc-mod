package com.veinmine.mod.mixin;

import com.veinmine.mod.util.OreClassifier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class OreBreakSpeedMixin {

    private static final float ORE_SPEED_MULTIPLIER = 6.0f;

    @Inject(method = "calcBlockBreakingDelta", at = @At("RETURN"), cancellable = true)
    private void cleaveore$boostServerDelta(
        PlayerEntity player,
        BlockView world,
        BlockPos pos,
        CallbackInfoReturnable<Float> cir
    ) {
        if (!(player instanceof ServerPlayerEntity)) {
            return;
        }
        BlockState self = (BlockState) (Object) this;
        if (!OreClassifier.isPluckableOre(self)) {
            return;
        }
        cir.setReturnValue(cir.getReturnValue() * ORE_SPEED_MULTIPLIER);
    }
}
