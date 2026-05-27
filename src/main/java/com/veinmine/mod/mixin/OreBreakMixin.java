package com.veinmine.mod.mixin;

import com.veinmine.mod.util.OreClassifier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class OreBreakMixin {

    @Shadow public ServerPlayerEntity player;
    @Shadow public ServerWorld world;

    @Inject(method = "tryBreakBlock", at = @At("HEAD"), cancellable = true)
    private void cleaveore$onBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (this.player.isCreative()) {
            return;
        }

        BlockState state = this.world.getBlockState(pos);
        if (!OreClassifier.isPluckableOre(state)) {
            return;
        }

        Block shellBlock = OreClassifier.getReplacementShell(state);
        ItemStack tool = this.player.getMainHandStack();
        if (!tool.isSuitableFor(state)) {
            this.world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_NOTE_BLOCK_BASS,
                SoundCategory.BLOCKS,
                0.35F,
                0.65F
            );
            return;
        }

        LootContextParameterSet.Builder lootContext = new LootContextParameterSet.Builder(this.world)
            .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
            .add(LootContextParameters.TOOL, tool)
            .add(LootContextParameters.THIS_ENTITY, this.player)
            .add(LootContextParameters.BLOCK_STATE, state);

        List<ItemStack> drops = state.getDroppedStacks(lootContext);
        for (ItemStack stack : drops) {
            Block.dropStack(this.world, pos, stack);
        }

        if (state.getBlock() instanceof ExperienceDroppingBlock expBlock) {
            expBlock.onStacksDropped(state, this.world, pos, tool, true);
        }

        if (!tool.isEmpty()) {
            tool.damage(1, this.player, p -> p.sendToolBreakStatus(this.player.getActiveHand()));
        }

        state.getBlock().onBroken(this.world, pos, state);
        this.world.setBlockState(pos, shellBlock.getDefaultState(), Block.NOTIFY_ALL);

        BlockSoundGroup sounds = state.getSoundGroup();
        this.world.playSound(
            null,
            pos,
            sounds.getBreakSound(),
            SoundCategory.BLOCKS,
            (sounds.getVolume() + 1.0F) / 2.0F,
            sounds.getPitch() * 1.18F
        );

        Vec3d center = Vec3d.ofCenter(pos);
        this.world.spawnParticles(
            new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
            center.x, center.y, center.z,
            28,
            0.35, 0.35, 0.35,
            0.08
        );
        this.world.spawnParticles(
            ParticleTypes.GLOW,
            center.x, center.y, center.z,
            4,
            0.22, 0.22, 0.22,
            0.01
        );

        cir.setReturnValue(true);
        cir.cancel();
    }
}
