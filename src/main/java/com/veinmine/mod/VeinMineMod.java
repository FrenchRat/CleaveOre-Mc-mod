package com.veinmine.mod;

import com.mojang.logging.LogUtils;
import com.veinmine.mod.registry.ModBlocks;
import org.slf4j.Logger;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(VeinMineMod.MOD_ID)
public class VeinMineMod {
    public static final String MOD_ID = "veinmine";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VeinMineMod(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        NeoForge.EVENT_BUS.register(new VeinMineEvents());
        LOGGER.info("CleaveOre (NeoForge) initialized.");
    }
}
