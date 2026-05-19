package com.veinmine.mod.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * OreBreakSpeedMixin — SERVER SIDE ONLY.
 *
 * This is the missing piece that makes fast ore mining actually work without rollback.
 *
 * Here is the problem it solves: Minecraft has an anti-cheat mechanism inside
 * ServerPlayNetworkHandler that checks how long a player took to mine a block.
 * If the client says "I mined this block in 3 ticks" but the server calculates
 * it should have taken 18 ticks, the server REJECTS the break and sends a block
 * update back to the client restoring the original block. This is what causes
 * the classic "rubber-banding" block restoration you see with speed hacks.
 *
 * OreHardnessMixin speeds up the CLIENT's progress bar, but the SERVER still
 * uses the vanilla calcBlockBreakingDelta to validate the break time. Without
 * this mixin, every ore stage-1 break would be rejected and rolled back.
 *
 * The fix is elegant: we apply the EXACT SAME multiplier (6.0×) to the server's
 * calcBlockBreakingDelta for ore blocks. Now both sides agree that the ore takes
 * 1/6th the normal time to mine, so the server happily accepts the fast break.
 *
 * TARGET: AbstractBlock.AbstractBlockState#calcBlockBreakingDelta
 *   Same target as OreHardnessMixin. This is intentional — we want BOTH the
 *   client and the server code paths to return the boosted value. In a singleplayer
 *   world, both run in the same JVM so both fire. On a dedicated server, only the
 *   server-side path fires, but that's all that matters for validation.
 *
 * ENVIRONMENT: "*" (runs on both client and server).
 *   We need this to run on dedicated servers too, where there is no client mixin.
 *   It is safe to run on both — it's a pure math operation with no side effects.
 */
@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class OreBreakSpeedMixin {

    // Must match OreHardnessMixin exactly so client and server agree
    private static final float ORE_SPEED_MULTIPLIER = 6.0f;

    @Inject(
        method = "calcBlockBreakingDelta",
        at = @At("RETURN"),
        cancellable = true
    )
    private void veinmine_serverBoostOreDelta(
        PlayerEntity player,
        BlockView world,
        BlockPos pos,
        CallbackInfoReturnable<Float> cir
    ) {
        // Only apply on the server player entity type.
        // On the client, OreHardnessMixin (in the client mixin config) handles it.
        // This guard prevents double-application in singleplayer where BOTH mixins load.
        if (!(player instanceof ServerPlayerEntity)) return;

        BlockState self = (BlockState) (Object) this;
        Block block = self.getBlock();

        boolean isOre = block instanceof ExperienceDroppingBlock
            || block == Blocks.ANCIENT_DEBRIS;

        if (!isOre) return;

        float vanilla = cir.getReturnValue();
        cir.setReturnValue(vanilla * ORE_SPEED_MULTIPLIER);
    }
}
