package com.veinmine.mod;

import com.mojang.logging.LogUtils;
import com.veinmine.mod.registry.ModBlocks;
import org.slf4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(VeinMineMod.MOD_ID)
public class VeinMineMod {
    public static final String MOD_ID = "veinmine";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VeinMineMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(new VeinMineEvents());
        LOGGER.info("CleaveOre (Forge) initialized.");
    }
}
