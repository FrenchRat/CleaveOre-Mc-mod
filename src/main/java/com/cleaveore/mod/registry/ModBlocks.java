package com.cleaveore.mod.registry;

import com.cleaveore.mod.CleaveOreMod;
import com.cleaveore.mod.block.HollowOreShellBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * ModBlocks registers every "hollow shell" variant — one per ore type.
 *
 * Each shell block is just stone that looks like it had the ore ripped out of it.
 * It mines at normal stone speed, drops nothing (the ore already dropped in stage 1),
 * and has no special properties beyond being a regular full block.
 *
 * We copy settings from vanilla stone so hardness, blast resistance, and tool
 * requirements all match stone exactly.
 */
public class ModBlocks {

    // ── Deepslate-based ores ──────────────────────────────────────────────────
    public static final Block HOLLOW_DEEPSLATE_COAL_SHELL    = register("hollow_deepslate_coal_shell",    true);
    public static final Block HOLLOW_DEEPSLATE_IRON_SHELL    = register("hollow_deepslate_iron_shell",    true);
    public static final Block HOLLOW_DEEPSLATE_GOLD_SHELL    = register("hollow_deepslate_gold_shell",    true);
    public static final Block HOLLOW_DEEPSLATE_DIAMOND_SHELL = register("hollow_deepslate_diamond_shell", true);
    public static final Block HOLLOW_DEEPSLATE_EMERALD_SHELL = register("hollow_deepslate_emerald_shell", true);
    public static final Block HOLLOW_DEEPSLATE_LAPIS_SHELL   = register("hollow_deepslate_lapis_shell",   true);
    public static final Block HOLLOW_DEEPSLATE_REDSTONE_SHELL= register("hollow_deepslate_redstone_shell",true);
    public static final Block HOLLOW_DEEPSLATE_COPPER_SHELL  = register("hollow_deepslate_copper_shell",  true);

    // ── Regular stone-based ores ──────────────────────────────────────────────
    public static final Block HOLLOW_COAL_SHELL     = register("hollow_coal_shell",     false);
    public static final Block HOLLOW_IRON_SHELL     = register("hollow_iron_shell",     false);
    public static final Block HOLLOW_GOLD_SHELL     = register("hollow_gold_shell",     false);
    public static final Block HOLLOW_DIAMOND_SHELL  = register("hollow_diamond_shell",  false);
    public static final Block HOLLOW_EMERALD_SHELL  = register("hollow_emerald_shell",  false);
    public static final Block HOLLOW_LAPIS_SHELL    = register("hollow_lapis_shell",    false);
    public static final Block HOLLOW_REDSTONE_SHELL = register("hollow_redstone_shell", false);
    public static final Block HOLLOW_COPPER_SHELL   = register("hollow_copper_shell",   false);

    // ── Nether ores ───────────────────────────────────────────────────────────
    public static final Block HOLLOW_NETHER_GOLD_SHELL    = register("hollow_nether_gold_shell",    false);
    public static final Block HOLLOW_NETHER_QUARTZ_SHELL  = register("hollow_nether_quartz_shell",  false);
    public static final Block HOLLOW_ANCIENT_DEBRIS_SHELL = register("hollow_ancient_debris_shell", false);

    /**
     * Creates and registers a HollowOreShellBlock.
     *
     * @param name       The registry name (e.g. "hollow_coal_shell")
     * @param deepslate  If true, copies hardness from deepslate (harder); else from stone.
     */
    private static Block register(String name, boolean deepslate) {
        // Mirror the base stone's settings so fortune/silk touch, hardness, etc. are consistent
        Block baseStone = deepslate ? Blocks.DEEPSLATE : Blocks.STONE;
        FabricBlockSettings settings = FabricBlockSettings.copyOf(baseStone)
            .requiresTool(); // Must use a pickaxe to get the drop

        Block block = new HollowOreShellBlock(settings, deepslate);
        Identifier id = new Identifier(CleaveOreMod.MOD_ID, name);

        // Register the block itself
        Registry.register(Registries.BLOCK, id, block);

        // Register a corresponding BlockItem so it exists in inventories/creative
        Registry.register(Registries.ITEM, id,
            new BlockItem(block, new Item.Settings()));

        return block;
    }

    /** Called from CleaveOreMod.onInitialize() to trigger static field initialization. */
    public static void register() {
        CleaveOreMod.LOGGER.info("Registering CleaveOre blocks...");
    }
}
