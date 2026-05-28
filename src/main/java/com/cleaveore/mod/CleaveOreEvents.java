package com.cleaveore.mod;

import com.cleaveore.mod.util.OreClassifier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CleaveOreEvents {

    private static final Map<UUID, Long> FAIL_COOLDOWN = new HashMap<>();
    private static final Map<PluckedPos, Block> PLUCKED_HOSTS = new HashMap<>();
    private static final Map<PluckedPos, Long> RECENT_PLUCK_TICKS = new HashMap<>();

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
        PluckedPos pluckedKey = new PluckedPos(serverLevel.dimension().location().toString(), pos.asLong());
        long now = serverLevel.getGameTime();
        BlockState state = serverLevel.getBlockState(pos);
        boolean hasMainPickaxe = isPickaxeLikeTool(player.getMainHandItem(), state);
        boolean targetIsOre = OreClassifier.isPluckableOre(state);

        // Prevent offhand placements/uses (shield, lantern, etc.) while pluck conditions are met.
        boolean justPluckedHere = now - RECENT_PLUCK_TICKS.getOrDefault(pluckedKey, Long.MIN_VALUE / 2) <= 2;
        if (event.getHand() == InteractionHand.OFF_HAND && hasMainPickaxe && (targetIsOre || justPluckedHere)) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        ItemStack tool = player.getMainHandItem();
        if (!isPickaxeLikeTool(tool, state)) {
            return;
        }
        if (isAncientDebris(state) && !CleaveOreConfig.get().allowAncientDebrisPluck) {
            failFeedback(serverPlayer, serverLevel, pos);
            return;
        }
        if (!OreClassifier.isPluckableOre(state)) {
            return;
        }
        if (!canHarvestOre(tool, state)) {
            failFeedback(serverPlayer, serverLevel, pos);
            return;
        }

        BlockState replacementState = getHostReplacementState(serverLevel, pos, state);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);

        Direction face = event.getFace() == null ? Direction.UP : event.getFace();
        for (ItemStack drop : Block.getDrops(state, serverLevel, pos, null, player, tool)) {
            Block.popResourceFromFace(serverLevel, pos, face, drop);
        }
        state.spawnAfterBreak(serverLevel, pos, tool, true);
        if (!tool.isEmpty() && serverLevel.random.nextDouble() < CleaveOreConfig.get().pluckDurabilityChance) {
            tool.hurtAndBreak(1, serverPlayer, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }

        serverLevel.setBlock(pos, replacementState, Block.UPDATE_ALL);
        PLUCKED_HOSTS.put(pluckedKey, replacementState.getBlock());
        RECENT_PLUCK_TICKS.put(pluckedKey, now);

        SoundType sounds = state.getSoundType();
        serverLevel.playSound(
            null,
            pos,
            sounds.getBreakSound(),
            SoundSource.BLOCKS,
            (sounds.getVolume() + 1.0F) / 2.0F,
            getSuccessPitch(state, sounds.getPitch())
        );

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), x, y, z, 28, 0.35, 0.35, 0.35, 0.08);
        serverLevel.sendParticles(ParticleTypes.GLOW, x, y, z, 4, 0.22, 0.22, 0.22, 0.01);
    }

    private static boolean isPickaxeLikeTool(ItemStack tool, BlockState state) {
        if (tool.canPerformAction(ToolActions.PICKAXE_DIG)) {
            return true;
        }
        if (tool.isCorrectToolForDrops(state)) {
            return true;
        }
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE) && tool.getDestroySpeed(state) > 1.0F;
    }

    private static boolean canHarvestOre(ItemStack tool, BlockState state) {
        String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        if ("nether_gold_ore".equals(path) || "nether_quartz_ore".equals(path)) {
            return tool.isCorrectToolForDrops(Blocks.IRON_ORE.defaultBlockState());
        }
        return tool.isCorrectToolForDrops(state);
    }

    private static boolean isAncientDebris(BlockState state) {
        return "ancient_debris".equals(BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath());
    }

    private static Block getHostBlockFor(BlockState state) {
        String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        if (path.contains("deepslate")) {
            return Blocks.DEEPSLATE;
        }
        if (path.contains("stone")) {
            return Blocks.STONE;
        }
        if (path.contains("nether") || path.contains("netherrack")) {
            return Blocks.NETHERRACK;
        }
        if (path.contains("blackstone")) {
            return Blocks.BLACKSTONE;
        }
        if (path.contains("end") || path.contains("endstone")) {
            return Blocks.END_STONE;
        }
        return Blocks.STONE;
    }

    private static BlockState getHostReplacementState(ServerLevel level, BlockPos pos, BlockState oreState) {
        Block host = getHostBlockFor(oreState);
        for (Direction direction : Direction.values()) {
            BlockState neighbor = level.getBlockState(pos.relative(direction));
            if (neighbor.getBlock() == host) {
                return neighbor;
            }
        }
        return host.defaultBlockState();
    }

    private static float getSuccessPitch(BlockState state, float basePitch) {
        String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        if (path.contains("diamond") || path.contains("emerald")) {
            return basePitch * 1.22F;
        }
        if (path.contains("gold") || path.contains("quartz")) {
            return basePitch * 1.2F;
        }
        if (path.contains("redstone") || path.contains("copper")) {
            return basePitch * 1.16F;
        }
        return basePitch * 1.18F;
    }

    private static void failFeedback(ServerPlayer serverPlayer, ServerLevel serverLevel, BlockPos pos) {
        long now = serverLevel.getGameTime();
        int cooldown = Math.max(0, CleaveOreConfig.get().failCooldownTicks);
        long last = FAIL_COOLDOWN.getOrDefault(serverPlayer.getUUID(), Long.MIN_VALUE / 2);
        if (now - last < cooldown) {
            return;
        }
        FAIL_COOLDOWN.put(serverPlayer.getUUID(), now);

        serverLevel.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.BLOCKS, 0.35F, 0.65F);
        if (CleaveOreConfig.get().showFailActionBar) {
            serverPlayer.displayClientMessage(Component.literal("failed").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC), true);
        }
    }

    @SubscribeEvent
    public void onBreakPluckedHost(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos pos = event.getPos();
        BlockState state = serverLevel.getBlockState(pos);
        PluckedPos key = new PluckedPos(serverLevel.dimension().location().toString(), pos.asLong());
        Block expected = PLUCKED_HOSTS.get(key);
        if (expected == null || state.getBlock() != expected) {
            return;
        }

        PLUCKED_HOSTS.remove(key);
        event.setCanceled(true);

        ItemStack tool = serverPlayer.getMainHandItem();
        if (!serverPlayer.isCreative() && !tool.isEmpty()) {
            tool.mineBlock(serverLevel, state, pos, serverPlayer);
        }

        if (tool.isCorrectToolForDrops(state)) {
            Item asItem = state.getBlock().asItem();
            if (asItem != Item.byBlock(Blocks.AIR)) {
                Block.popResource(serverLevel, pos, new ItemStack(asItem));
            }
        }

        SoundType sounds = state.getSoundType();
        serverLevel.playSound(
            null,
            pos,
            sounds.getBreakSound(),
            SoundSource.BLOCKS,
            (sounds.getVolume() + 1.0F) / 2.0F,
            sounds.getPitch()
        );

        serverLevel.sendParticles(
            new BlockParticleOption(ParticleTypes.BLOCK, state),
            pos.getX() + 0.5,
            pos.getY() + 0.5,
            pos.getZ() + 0.5,
            14,
            0.2,
            0.2,
            0.2,
            0.05
        );

        serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
    }

    private record PluckedPos(String dimensionKey, long packedPos) {
    }
}
