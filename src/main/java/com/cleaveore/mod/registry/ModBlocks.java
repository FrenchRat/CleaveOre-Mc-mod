package com.cleaveore.mod.registry;

import com.cleaveore.mod.CleaveOreMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CleaveOreMod.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CleaveOreMod.MOD_ID);

    public static final RegistryObject<Block> HOLLOW_STONE_SHELL = registerShell("hollow_stone_shell", Blocks.STONE, 1.25f, 1.8f);
    public static final RegistryObject<Block> HOLLOW_DEEPSLATE_SHELL = registerShell("hollow_deepslate_shell", Blocks.DEEPSLATE, 2.0f, 2.6f);
    public static final RegistryObject<Block> HOLLOW_NETHER_SHELL = registerShell("hollow_nether_shell", Blocks.NETHERRACK, 1.0f, 1.4f);
    public static final RegistryObject<Block> HOLLOW_ANCIENT_DEBRIS_SHELL = registerShell("hollow_ancient_debris_shell", Blocks.ANCIENT_DEBRIS, 2.2f, 3.2f);

    private ModBlocks() {
    }

    private static RegistryObject<Block> registerShell(String name, Block baseBlock, float destroyTime, float explosionResistance) {
        RegistryObject<Block> block = BLOCKS.register(name, () -> new Block(
            BlockBehaviour.Properties.copy(baseBlock)
                .strength(destroyTime, explosionResistance)
                .requiresCorrectToolForDrops()
        ));
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    public static Block getShellFor(BlockState state) {
        String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        if (path.contains("deepslate")) {
            return HOLLOW_DEEPSLATE_SHELL.get();
        }
        if (path.contains("nether")) {
            return HOLLOW_NETHER_SHELL.get();
        }
        if (path.contains("ancient_debris")) {
            return HOLLOW_ANCIENT_DEBRIS_SHELL.get();
        }
        return HOLLOW_STONE_SHELL.get();
    }

    public static boolean isShell(BlockState state) {
        Block block = state.getBlock();
        return block == HOLLOW_STONE_SHELL.get()
            || block == HOLLOW_DEEPSLATE_SHELL.get()
            || block == HOLLOW_NETHER_SHELL.get()
            || block == HOLLOW_ANCIENT_DEBRIS_SHELL.get();
    }
}
