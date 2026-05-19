package com.veinmine.mod.registry;

import com.veinmine.mod.VeinMineMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(VeinMineMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(VeinMineMod.MOD_ID);

    public static final DeferredBlock<Block> HOLLOW_STONE_SHELL = registerShell("hollow_stone_shell", Blocks.STONE);
    public static final DeferredBlock<Block> HOLLOW_DEEPSLATE_SHELL = registerShell("hollow_deepslate_shell", Blocks.DEEPSLATE);
    public static final DeferredBlock<Block> HOLLOW_NETHER_SHELL = registerShell("hollow_nether_shell", Blocks.NETHERRACK);
    public static final DeferredBlock<Block> HOLLOW_ANCIENT_DEBRIS_SHELL = registerShell("hollow_ancient_debris_shell", Blocks.ANCIENT_DEBRIS);

    private ModBlocks() {
    }

    private static DeferredBlock<Block> registerShell(String name, Block baseBlock) {
        DeferredBlock<Block> block = BLOCKS.register(name, () -> new Block(
            BlockBehaviour.Properties.ofFullCopy(baseBlock)
                .requiresCorrectToolForDrops()
                .noLootTable()
        ));
        DeferredItem<BlockItem> item = ITEMS.registerSimpleBlockItem(name, block, new Item.Properties());
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
}
