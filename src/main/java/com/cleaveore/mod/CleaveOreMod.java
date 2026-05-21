package com.cleaveore.mod;

import com.mojang.logging.LogUtils;
import com.cleaveore.mod.registry.ModBlocks;
import org.slf4j.Logger;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(CleaveOreMod.MOD_ID)
public class CleaveOreMod {
    public static final String MOD_ID = "cleaveore";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CleaveOreMod(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        NeoForge.EVENT_BUS.register(new CleaveOreEvents());
        LOGGER.info("CleaveOre (NeoForge) initialized.");
    }
}

