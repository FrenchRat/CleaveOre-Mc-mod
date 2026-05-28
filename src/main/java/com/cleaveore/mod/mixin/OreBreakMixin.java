package com.cleaveore.mod.mixin;

import com.cleaveore.mod.CleaveOreConfig;
import com.cleaveore.mod.util.OreClassifier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.item.ItemStack;
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
        this.world.setBlockState(pos, shellBlock.getDefaultState(), Block.NOTIFY_ALL);

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

    private void spawnFailX(BlockPos pos) {
        double scale = Math.max(0.2, CleaveOreConfig.get().failParticleScale);
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.62;
        double cz = pos.getZ() + 0.5;
        for (int i = -2; i <= 2; i++) {
            double t = i * 0.045 * scale;
            this.world.spawnParticles(ParticleTypes.GLOW, cx + t, cy + t, cz, 1, 0.0, 0.0, 0.0, 0.0);
            this.world.spawnParticles(ParticleTypes.GLOW, cx + t, cy - t, cz, 1, 0.0, 0.0, 0.0, 0.0);
        }
        this.world.spawnParticles(ParticleTypes.SMOKE, cx, cy, cz, 3, 0.06 * scale, 0.04 * scale, 0.06 * scale, 0.0);
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
            this.player.sendMessage(Text.literal("Pluck failed").formatted(Formatting.DARK_GRAY), true);
        }
        if (CleaveOreConfig.get().showFailXParticles) {
            spawnFailX(pos);
        }
    }
}
