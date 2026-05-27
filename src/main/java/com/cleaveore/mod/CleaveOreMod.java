package com.cleaveore.mod;

import com.mojang.logging.LogUtils;
import com.cleaveore.mod.registry.ModBlocks;
import org.slf4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CleaveOreMod.MOD_ID)
public class CleaveOreMod {
    public static final String MOD_ID = "cleaveore";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CleaveOreMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(new CleaveOreEvents());
        LOGGER.info("CleaveOre (Forge) initialized.");
    }
}
