package com.veinmine.mod;

import com.veinmine.mod.registry.ModBlocks;
import com.veinmine.mod.util.OreClassifier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class VeinMineEvents {

    private static final float ORE_SPEED_MULTIPLIER = 6.0f;

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        BlockState state = event.getState();
        if (state == null || !OreClassifier.isPluckableOre(state)) {
            return;
        }
        event.setNewSpeed(event.getNewSpeed() * ORE_SPEED_MULTIPLIER);
    }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (!(player instanceof ServerPlayer) || player.isCreative()) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos pos = event.getPos();
        BlockState state = serverLevel.getBlockState(pos);
        if (!OreClassifier.isPluckableOre(state)) {
            return;
        }

        Block replacement = ModBlocks.getShellFor(state);
        ItemStack tool = player.getMainHandItem();

        event.setCanceled(true);

        Block.dropResources(state, serverLevel, pos, null, player, tool);
        state.spawnAfterBreak(serverLevel, pos, tool, true);
        if (!tool.isEmpty()) {
            tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }

        serverLevel.setBlock(pos, replacement.defaultBlockState(), Block.UPDATE_ALL);

        SoundType sounds = state.getSoundType();
        serverLevel.playSound(
            null,
            pos,
            sounds.getBreakSound(),
            SoundSource.BLOCKS,
            (sounds.getVolume() + 1.0F) / 2.0F,
            sounds.getPitch() * 1.18F
        );

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), x, y, z, 28, 0.35, 0.35, 0.35, 0.08);
        serverLevel.sendParticles(ParticleTypes.GLOW, x, y, z, 4, 0.22, 0.22, 0.22, 0.01);
    }
}
