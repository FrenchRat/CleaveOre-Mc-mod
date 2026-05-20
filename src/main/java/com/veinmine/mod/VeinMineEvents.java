package com.veinmine.mod;

import com.veinmine.mod.registry.ModBlocks;
import com.veinmine.mod.util.OreClassifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class VeinMineEvents {

    private static final float SHELL_SPEED_MULTIPLIER = 1.8f;
    private static final float PLUCK_DURABILITY_CHANCE = 0.6f;

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        BlockState state = event.getState();
        if (state == null) {
            return;
        }
        if (ModBlocks.isShell(state)) {
            event.setNewSpeed(event.getNewSpeed() * SHELL_SPEED_MULTIPLIER);
        }
    }

    @SubscribeEvent
    public void onRightClickOre(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer) || player.isCreative()) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos pos = event.getPos();
        BlockState state = serverLevel.getBlockState(pos);
        boolean hasMainPickaxe = player.getMainHandItem().canPerformAction(ToolActions.PICKAXE_DIG);
        boolean targetIsOre = OreClassifier.isPluckableOre(state);

        // Prevent offhand placements/uses (shield, lantern, etc.) while pluck conditions are met.
        if (event.getHand() == InteractionHand.OFF_HAND && hasMainPickaxe && targetIsOre) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        ItemStack tool = player.getMainHandItem();
        if (!tool.canPerformAction(ToolActions.PICKAXE_DIG)) {
            return;
        }
        if (!OreClassifier.isPluckableOre(state)) {
            return;
        }

        Block replacement = ModBlocks.getShellFor(state);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);

        Direction face = event.getFace() == null ? Direction.UP : event.getFace();
        for (ItemStack drop : Block.getDrops(state, serverLevel, pos, null, player, tool)) {
            Block.popResourceFromFace(serverLevel, pos, face, drop);
        }
        state.spawnAfterBreak(serverLevel, pos, tool, true);
        if (!tool.isEmpty() && serverLevel.random.nextFloat() < PLUCK_DURABILITY_CHANCE) {
            tool.hurtAndBreak(1, serverPlayer, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
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
