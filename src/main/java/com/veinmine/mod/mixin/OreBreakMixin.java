package com.veinmine.mod.mixin;

import com.veinmine.mod.registry.ModBlocks;
import com.veinmine.mod.registry.ModParticles;
import net.minecraft.block.*;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

/**
 * OreBreakMixin — the heart of VeinMine.
 *
 * TARGET: ServerPlayerInteractionManager#tryBreakBlock
 *
 * This is the correct injection point for server-side block breaking.
 * tryBreakBlock is the method the server calls when it has validated that
 * a block is fully mined — it is responsible for calling onBroken, dropping
 * loot, and removing the block from the world. By injecting at HEAD with
 * cancellable=true, we intercept the entire process for ore blocks and
 * replace it with our two-stage logic.
 *
 * WHY NOT PlayerEntity#breakBlock?
 *   breakBlock exists on PlayerEntity but in practice the server NEVER calls
 *   it for survival mining — it only gets called in Creative. The real code
 *   path for survival is ServerPlayerInteractionManager → tryBreakBlock →
 *   ServerWorld#breakBlock. Injecting into PlayerEntity#breakBlock would
 *   silently never fire in Survival mode, which is exactly the bug the first
 *   version had.
 *
 * LOOT & XP SAFETY:
 *   Because we inject at HEAD and cancel, vanilla NEVER gets to run its own
 *   drop/XP code for our intercepted ore blocks. This means we are the sole
 *   source of drops — no double-drop is possible. We rebuild the loot context
 *   manually using the proper LootContextParameterSet so Fortune and Silk Touch
 *   are respected correctly.
 *
 * CREATIVE MODE:
 *   We check isCreative() first and bail out. Creative mode should still
 *   insta-break blocks and produce no drops, exactly as vanilla does.
 */
@Mixin(ServerPlayerInteractionManager.class)
public abstract class OreBreakMixin {

    // @Shadow gives us access to the private fields on ServerPlayerInteractionManager
    // without reflection. Mixin resolves these at compile/load time.
    @Shadow public ServerPlayerEntity player;
    @Shadow public ServerWorld world;

    // ── Ore → Shell lookup ────────────────────────────────────────────────────

    /**
     * Returns the hollow shell block that should replace a given ore block,
     * or null if the block is not an ore we handle.
     *
     * Deepslate variants share Java classes with their stone counterparts
     * (e.g. DeepslateIronOreBlock extends IronOreBlock in older MC, or they
     * share ExperienceDroppingBlock in 1.20+). We check the registry name
     * prefix first so deepslate variants always get the harder deepslate shell.
     */
    private static Block getShellFor(BlockState state) {
        Block block = state.getBlock();
        String name = Blocks.getId(block).getPath(); // e.g. "deepslate_iron_ore"

        // ── Deepslate ores (check name before instanceof to avoid wrong shell) ──
        if (name.startsWith("deepslate_")) {
            if (block instanceof ExperienceDroppingBlock) {
                // All deepslate ores extend ExperienceDroppingBlock; discriminate by name
                if (name.contains("coal"))     return ModBlocks.HOLLOW_DEEPSLATE_COAL_SHELL;
                if (name.contains("iron"))     return ModBlocks.HOLLOW_DEEPSLATE_IRON_SHELL;
                if (name.contains("gold"))     return ModBlocks.HOLLOW_DEEPSLATE_GOLD_SHELL;
                if (name.contains("diamond"))  return ModBlocks.HOLLOW_DEEPSLATE_DIAMOND_SHELL;
                if (name.contains("emerald"))  return ModBlocks.HOLLOW_DEEPSLATE_EMERALD_SHELL;
                if (name.contains("lapis"))    return ModBlocks.HOLLOW_DEEPSLATE_LAPIS_SHELL;
                if (name.contains("redstone")) return ModBlocks.HOLLOW_DEEPSLATE_REDSTONE_SHELL;
                if (name.contains("copper"))   return ModBlocks.HOLLOW_DEEPSLATE_COPPER_SHELL;
            }
        }

        // ── Nether & special ores (identity check — these are singletons) ──────
        if (block == Blocks.NETHER_GOLD_ORE)  return ModBlocks.HOLLOW_NETHER_GOLD_SHELL;
        if (block == Blocks.NETHER_QUARTZ_ORE) return ModBlocks.HOLLOW_NETHER_QUARTZ_SHELL;
        if (block == Blocks.ANCIENT_DEBRIS)    return ModBlocks.HOLLOW_ANCIENT_DEBRIS_SHELL;

        // ── Stone-based ores (all extend ExperienceDroppingBlock in 1.20.4) ────
        if (block instanceof ExperienceDroppingBlock) {
            if (name.contains("coal"))     return ModBlocks.HOLLOW_COAL_SHELL;
            if (name.contains("iron"))     return ModBlocks.HOLLOW_IRON_SHELL;
            if (name.contains("gold"))     return ModBlocks.HOLLOW_GOLD_SHELL;
            if (name.contains("diamond"))  return ModBlocks.HOLLOW_DIAMOND_SHELL;
            if (name.contains("emerald"))  return ModBlocks.HOLLOW_EMERALD_SHELL;
            if (name.contains("lapis"))    return ModBlocks.HOLLOW_LAPIS_SHELL;
            if (name.contains("redstone")) return ModBlocks.HOLLOW_REDSTONE_SHELL;
            if (name.contains("copper"))   return ModBlocks.HOLLOW_COPPER_SHELL;
        }

        return null; // Not an ore we handle → let vanilla run normally
    }

    // ── Main injection ────────────────────────────────────────────────────────

    @Inject(method = "tryBreakBlock", at = @At("HEAD"), cancellable = true)
    private void veinmine_interceptOreBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {

        // Creative mode: let vanilla insta-break with no drops as normal
        if (this.player.isCreative()) return;

        BlockState state = this.world.getBlockState(pos);
        Block shellBlock = getShellFor(state);

        // Not one of our ores → don't touch it
        if (shellBlock == null) return;

        ItemStack tool = this.player.getMainHandStack();

        // ── 1. Drop ore loot, honouring Fortune / Silk Touch ─────────────────
        //
        // We build a proper LootContextParameterSet here rather than calling
        // the convenience Block.dropStacks() helper. The helper is fine for
        // most cases but it doesn't let us pass the block entity (null here,
        // which is correct — ores have no block entity), and more importantly
        // doing it explicitly makes the Fortune/Silk Touch path crystal clear.
        //
        // The loot table for e.g. coal_ore lives at:
        //   data/minecraft/loot_tables/blocks/coal_ore.json
        // and it already handles Silk Touch (drops the ore block) vs Fortune
        // (multiplies raw drop count). We just need to provide the right context.
        LootContextParameterSet lootContext = new LootContextParameterSet.Builder(this.world)
            .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
            .add(LootContextParameters.TOOL, tool)
            .add(LootContextParameters.THIS_ENTITY, this.player)
            .addOptional(LootContextParameters.BLOCK_ENTITY, null)
            .build(LootContextTypes.BLOCK);

        List<ItemStack> drops = state.getDroppedStacks(lootContext);
        for (ItemStack drop : drops) {
            Block.dropStack(this.world, pos, drop);
        }

        // ── 2. Award XP ───────────────────────────────────────────────────────
        //
        // In 1.20.4, ExperienceDroppingBlock exposes onStacksDropped(BlockState,
        // ServerWorld, BlockPos, ItemStack, boolean) which internally decides the
        // XP amount and spawns the orbs. The boolean parameter is "dropExperience"
        // — passing true makes it behave like a normal survival mine. This is the
        // safest public API to call; it correctly handles Fortune-boosted XP for
        // diamonds/emeralds and the 0-XP case for coal/nether quartz.
        if (state.getBlock() instanceof ExperienceDroppingBlock expBlock) {
            expBlock.onStacksDropped(state, this.world, pos, tool, true);
        }

        // ── 3. Fire onBroken callback (updates stats, triggers advancements) ──
        //
        // This is the callback Minecraft uses to count blocks mined toward
        // the statistics screen and to trigger advancements like "Getting Wood"
        // or "Diamonds!". We must call it ourselves since we're cancelling the
        // vanilla tryBreakBlock which would normally call it.
        state.getBlock().onBroken(this.world, pos, state);

        // ── 4. Damage the player's tool (same wear as normal mining) ──────────
        //
        // ItemStack.damage() handles Unbreaking enchantment automatically.
        // The second argument is the damage amount (1 per block mined in vanilla).
        // We pass the player so Unbreaking's random chance uses their luck stat.
        if (!tool.isEmpty()) {
            tool.damage(1, this.player, p -> p.sendToolBreakStatus(this.player.getActiveHand()));
        }

        // ── 5. Replace ore block with hollow shell ────────────────────────────
        this.world.setBlockState(pos, shellBlock.getDefaultState(), Block.NOTIFY_ALL);

        // ── 6. Play a high-pitched crack sound ────────────────────────────────
        BlockSoundGroup sounds = state.getSoundGroup();
        this.world.playSound(
            null,                              // null = broadcast to all nearby players
            pos,
            sounds.getBreakSound(),
            SoundCategory.BLOCKS,
            (sounds.getVolume() + 1.0F) / 2.0F,
            sounds.getPitch() * 1.35F          // higher pitch = lighter "pop" feel
        );

        // ── 7. Spawn particles ────────────────────────────────────────────────
        //
        // We call spawnParticles on ServerWorld which broadcasts the packet to
        // all nearby clients. Clients with Iris/Optifine will render the
        // OrePopParticle through the translucent gbuffers pass, giving bloom/glow.
        Vec3d center = Vec3d.ofCenter(pos);
        this.world.spawnParticles(
            ModParticles.ORE_POP,
            center.x, center.y, center.z,
            22,            // particle count — enough for a satisfying burst
            0.4, 0.4, 0.4, // spread radius
            0.18           // initial speed
        );
        // A few vanilla crit sparkles for extra visual punch
        this.world.spawnParticles(
            ParticleTypes.CRIT,
            center.x, center.y, center.z,
            8, 0.3, 0.3, 0.3, 0.08
        );

        // ── 8. Cancel vanilla and signal success ──────────────────────────────
        cir.setReturnValue(true);
        cir.cancel();
    }
}
