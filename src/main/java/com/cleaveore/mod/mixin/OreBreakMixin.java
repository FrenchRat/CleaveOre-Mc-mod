package com.cleaveore.mod.mixin;

import com.cleaveore.mod.CleaveOreConfig;
import com.cleaveore.mod.util.OreClassifier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class OreBreakMixin {
    private static final Map<UUID, Long> FAIL_COOLDOWN = new ConcurrentHashMap<>();
    private static final Map<PluckedPos, Block> PLUCKED_HOSTS = new ConcurrentHashMap<>();

    @Shadow public ServerPlayerEntity player;
    @Shadow public ServerWorld world;

    @Inject(method = "tryBreakBlock", at = @At("HEAD"), cancellable = true)
    private void cleaveore$onBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (this.player.isCreative()) {
            return;
        }

        BlockState state = this.world.getBlockState(pos);
        PluckedPos key = new PluckedPos(this.world.getRegistryKey().getValue().toString(), pos.asLong());
        Block expectedHost = PLUCKED_HOSTS.get(key);
        if (expectedHost != null && state.isOf(expectedHost)) {
            PLUCKED_HOSTS.remove(key);

            ItemStack tool = this.player.getMainHandStack();
            if (!this.player.isCreative() && !tool.isEmpty()) {
                tool.damage(1, this.player, p -> p.sendToolBreakStatus(this.player.getActiveHand()));
            }

            if (tool.isSuitableFor(state)) {
                Item asItem = state.getBlock().asItem();
                if (asItem != Items.AIR) {
                    Block.dropStack(this.world, pos, new ItemStack(asItem));
                }
            }

            BlockSoundGroup sounds = state.getSoundGroup();
            this.world.playSound(
                null,
                pos,
                sounds.getBreakSound(),
                SoundCategory.BLOCKS,
                (sounds.getVolume() + 1.0F) / 2.0F,
                sounds.getPitch()
            );

            Vec3d center = Vec3d.ofCenter(pos);
            this.world.spawnParticles(
                new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
                center.x, center.y, center.z,
                14,
                0.2, 0.2, 0.2,
                0.05
            );
            this.world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);

            cir.setReturnValue(true);
            cir.cancel();
            return;
        }

        if (!OreClassifier.isPluckableOre(state)) {
            return;
        }

        BlockState replacementState = getHostReplacementState(pos, state);
        ItemStack tool = this.player.getMainHandStack();
        if (isAncientDebris(state) && !CleaveOreConfig.get().allowAncientDebrisPluck) {
            failFeedback(pos);
            return;
        }
        if (!canHarvestPluck(tool, state)) {
            failFeedback(pos);
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

        if (!tool.isEmpty() && this.world.random.nextDouble() < CleaveOreConfig.get().pluckDurabilityChance) {
            tool.damage(1, this.player, p -> p.sendToolBreakStatus(this.player.getActiveHand()));
        }

        state.getBlock().onBroken(this.world, pos, state);
        this.world.setBlockState(pos, replacementState, Block.NOTIFY_ALL);
        PLUCKED_HOSTS.put(key, replacementState.getBlock());

        BlockSoundGroup sounds = state.getSoundGroup();
        this.world.playSound(
            null,
            pos,
            sounds.getBreakSound(),
            SoundCategory.BLOCKS,
            (sounds.getVolume() + 1.0F) / 2.0F,
            getSuccessPitch(state, sounds.getPitch())
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

    private static boolean canHarvestPluck(ItemStack tool, BlockState state) {
        String path = Registries.BLOCK.getId(state.getBlock()).getPath();
        if ("nether_gold_ore".equals(path) || "nether_quartz_ore".equals(path)) {
            return tool.isSuitableFor(Blocks.IRON_ORE.getDefaultState());
        }
        return tool.isSuitableFor(state);
    }

    private static boolean isAncientDebris(BlockState state) {
        return "ancient_debris".equals(Registries.BLOCK.getId(state.getBlock()).getPath());
    }

    private static Block getHostBlockFor(BlockState state) {
        String path = Registries.BLOCK.getId(state.getBlock()).getPath();
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

    private BlockState getHostReplacementState(BlockPos pos, BlockState oreState) {
        Block host = getHostBlockFor(oreState);
        for (Direction direction : Direction.values()) {
            BlockState neighbor = this.world.getBlockState(pos.offset(direction));
            if (neighbor.isOf(host)) {
                return neighbor;
            }
        }
        return host.getDefaultState();
    }

    private float getSuccessPitch(BlockState state, float basePitch) {
        String path = Registries.BLOCK.getId(state.getBlock()).getPath();
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

    private void failFeedback(BlockPos pos) {
        long now = this.world.getTime();
        int cooldown = Math.max(0, CleaveOreConfig.get().failCooldownTicks);
        long last = FAIL_COOLDOWN.getOrDefault(this.player.getUuid(), Long.MIN_VALUE / 2);
        if (now - last < cooldown) {
            return;
        }
        FAIL_COOLDOWN.put(this.player.getUuid(), now);

        this.world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), SoundCategory.BLOCKS, 0.35F, 0.65F);
        if (CleaveOreConfig.get().showFailActionBar) {
            this.player.sendMessage(Text.literal("failed").formatted(Formatting.DARK_GRAY, Formatting.ITALIC), true);
        }
    }

    private record PluckedPos(String dimensionKey, long packedPos) {
    }
}

