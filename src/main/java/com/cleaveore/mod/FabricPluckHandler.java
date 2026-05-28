package com.cleaveore.mod;

import com.cleaveore.mod.util.OreClassifier;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MiningToolItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FabricPluckHandler {
    private static final Map<UUID, Long> FAIL_COOLDOWN = new HashMap<>();
    private static final Map<PluckedPos, Long> RECENT_PLUCK_TICKS = new HashMap<>();

    private FabricPluckHandler() {
    }

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);

            // Fabric quirk: returning SUCCESS on client guarantees the use-block packet is sent.
            // Without this, right-click on inert blocks can appear as "nothing happens".
            if (world.isClient()) {
                if (hand != Hand.MAIN_HAND) {
                    return ActionResult.PASS;
                }
                ItemStack clientTool = player.getMainHandStack();
                if (!isPickaxeLikeTool(clientTool, state)) {
                    return ActionResult.PASS;
                }
                if (OreClassifier.isPluckableOre(state) || isAncientDebris(state)) {
                    return ActionResult.SUCCESS;
                }
                return ActionResult.PASS;
            }

            if (!(world instanceof ServerWorld serverWorld) || !(player instanceof ServerPlayerEntity serverPlayer)) {
                return ActionResult.PASS;
            }

            PluckedPos key = new PluckedPos(serverWorld.getRegistryKey().getValue().toString(), pos.asLong());
            long now = serverWorld.getTime();
            boolean hasMainPickaxe = isPickaxeLikeTool(player.getMainHandStack(), state);
            boolean targetIsOre = OreClassifier.isPluckableOre(state);
            boolean justPluckedHere = now - RECENT_PLUCK_TICKS.getOrDefault(key, Long.MIN_VALUE / 2) <= 2;

            if (hand == Hand.OFF_HAND && hasMainPickaxe && (targetIsOre || justPluckedHere)) {
                return ActionResult.SUCCESS;
            }
            if (hand != Hand.MAIN_HAND) {
                return ActionResult.PASS;
            }

            ItemStack tool = player.getMainHandStack();
            if (!isPickaxeLikeTool(tool, state)) {
                return ActionResult.PASS;
            }
            if (isAncientDebris(state) && !CleaveOreConfig.get().allowAncientDebrisPluck) {
                failFeedback(serverPlayer, serverWorld, pos);
                return ActionResult.SUCCESS;
            }
            if (!OreClassifier.isPluckableOre(state)) {
                return ActionResult.PASS;
            }
            if (!canHarvestPluck(serverPlayer, tool, state)) {
                failFeedback(serverPlayer, serverWorld, pos);
                return ActionResult.SUCCESS;
            }

            BlockState replacementState = getHostReplacementState(serverWorld, pos, state);
            for (ItemStack drop : Block.getDroppedStacks(state, serverWorld, pos, null, player, tool)) {
                Block.dropStack(serverWorld, pos, drop);
            }

            if (state.getBlock() instanceof ExperienceDroppingBlock expBlock) {
                expBlock.onStacksDropped(state, serverWorld, pos, tool, true);
            }
            if (!player.isCreative() && !tool.isEmpty() && serverWorld.random.nextDouble() < CleaveOreConfig.get().pluckDurabilityChance) {
                tool.damage(1, serverPlayer, p -> p.sendToolBreakStatus(player.getActiveHand()));
            }

            state.getBlock().onBroken(serverWorld, pos, state);
            serverWorld.setBlockState(pos, replacementState, Block.NOTIFY_ALL);
            RECENT_PLUCK_TICKS.put(key, now);

            BlockSoundGroup sounds = state.getSoundGroup();
            serverWorld.playSound(null, pos, sounds.getBreakSound(), SoundCategory.BLOCKS, (sounds.getVolume() + 1.0F) / 2.0F, getSuccessPitch(state, sounds.getPitch()));
            Vec3d center = Vec3d.ofCenter(pos);
            serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), center.x, center.y, center.z, 28, 0.35, 0.35, 0.35, 0.08);
            serverWorld.spawnParticles(ParticleTypes.GLOW, center.x, center.y, center.z, 4, 0.22, 0.22, 0.22, 0.01);

            return ActionResult.SUCCESS;
        });
    }

    private static boolean isPickaxeLikeTool(ItemStack tool, BlockState state) {
        if (tool.isEmpty()) {
            return false;
        }
        if (tool.isIn(ItemTags.PICKAXES)) {
            return true;
        }
        if (tool.getItem() instanceof MiningToolItem) {
            return true;
        }
        return state.isIn(BlockTags.PICKAXE_MINEABLE) && tool.getMiningSpeedMultiplier(state) > 1.0F;
    }

    private static boolean canHarvestPluck(ServerPlayerEntity player, ItemStack tool, BlockState state) {
        String path = Registries.BLOCK.getId(state.getBlock()).getPath();
        if ("nether_gold_ore".equals(path) || "nether_quartz_ore".equals(path)) {
            return tool.isSuitableFor(Blocks.IRON_ORE.getDefaultState());
        }
        return player.canHarvest(state);
    }

    private static boolean isAncientDebris(BlockState state) {
        return "ancient_debris".equals(Registries.BLOCK.getId(state.getBlock()).getPath());
    }

    private static Block getHostBlockFor(BlockState state) {
        String path = Registries.BLOCK.getId(state.getBlock()).getPath();
        if (path.contains("deepslate")) return Blocks.DEEPSLATE;
        if (path.contains("stone")) return Blocks.STONE;
        if (path.contains("nether") || path.contains("netherrack")) return Blocks.NETHERRACK;
        return Blocks.STONE;
    }

    private static BlockState getHostReplacementState(ServerWorld world, BlockPos pos, BlockState oreState) {
        Block host = getHostBlockFor(oreState);
        for (Direction direction : Direction.values()) {
            BlockState neighbor = world.getBlockState(pos.offset(direction));
            if (neighbor.isOf(host)) return neighbor;
        }
        return host.getDefaultState();
    }

    private static float getSuccessPitch(BlockState state, float basePitch) {
        String path = Registries.BLOCK.getId(state.getBlock()).getPath();
        if (path.contains("diamond") || path.contains("emerald")) return basePitch * 1.22F;
        if (path.contains("gold") || path.contains("quartz")) return basePitch * 1.2F;
        if (path.contains("redstone") || path.contains("copper")) return basePitch * 1.16F;
        return basePitch * 1.18F;
    }

    private static void failFeedback(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
        long now = world.getTime();
        int cooldown = Math.max(0, CleaveOreConfig.get().failCooldownTicks);
        long last = FAIL_COOLDOWN.getOrDefault(player.getUuid(), Long.MIN_VALUE / 2);
        if (now - last < cooldown) return;
        FAIL_COOLDOWN.put(player.getUuid(), now);

        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), SoundCategory.BLOCKS, 0.35F, 0.65F);
        if (CleaveOreConfig.get().showFailActionBar) {
            player.sendMessage(Text.literal("failed").formatted(Formatting.DARK_GRAY, Formatting.ITALIC), true);
        }
    }

    private record PluckedPos(String dimensionKey, long packedPos) {
    }
}
