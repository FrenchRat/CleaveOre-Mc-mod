package com.veinmine.mod.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * OreHardnessMixin — CLIENT SIDE ONLY.
 *
 * TARGET: AbstractBlock.AbstractBlockState#calcBlockBreakingDelta
 *
 * In Yarn mappings, BlockState extends AbstractBlock.AbstractBlockState.
 * The method calcBlockBreakingDelta lives on the ABSTRACT parent class, which
 * is why we target AbstractBlock.AbstractBlockState and not BlockState directly.
 * Targeting BlockState would fail with a "mixin target not found" error at runtime
 * because Mixin looks for the method on the exact class specified, not its parents.
 *
 * This mixin makes the client's crack-animation progress bar advance ~6× faster
 * for ore blocks. This is PURELY a visual effect — the server doesn't see this.
 * The server-side speed bypass is handled by OreBreakSpeedMixin (separate file).
 *
 * The two together form a "client agrees, server agrees" handshake so no rollback occurs.
 */
@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class OreHardnessMixin {

    private static final float ORE_SPEED_MULTIPLIER = 6.0f;

    @Inject(
        method = "calcBlockBreakingDelta",
        at = @At("RETURN"),
        cancellable = true
    )
    private void veinmine_boostOreMiningDelta(
        PlayerEntity player,
        BlockView world,
        BlockPos pos,
        CallbackInfoReturnable<Float> cir
    ) {
        // Cast self to BlockState so we can read the block type.
        // This is safe because AbstractBlockState is always concretely a BlockState.
        BlockState self = (BlockState) (Object) this;
        Block block = self.getBlock();

        // Check: is this block an ore? In 1.20.4, ALL ore blocks (coal, iron,
        // diamond, deepslate variants, nether gold, nether quartz) extend
        // ExperienceDroppingBlock. Ancient Debris is the one exception — it
        // is just a regular Block — so we handle it with an identity check.
        boolean isOre = block instanceof ExperienceDroppingBlock
            || block == Blocks.ANCIENT_DEBRIS;

        // Guard: don't boost our own shell blocks (they are plain Blocks, not
        // ExperienceDroppingBlock, so they won't match — but being explicit
        // about intent is good defensive practice)
        if (!isOre) return;

        float vanilla = cir.getReturnValue();
        cir.setReturnValue(vanilla * ORE_SPEED_MULTIPLIER);
    }
}
