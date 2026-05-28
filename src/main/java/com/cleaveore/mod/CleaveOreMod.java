package com.cleaveore.mod;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(CleaveOreMod.MOD_ID)
public class CleaveOreMod {
    public static final String MOD_ID = "cleaveore";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CleaveOreMod() {
        CleaveOreConfig.load();
        MinecraftForge.EVENT_BUS.register(new CleaveOreEvents());
        MinecraftForge.EVENT_BUS.register(new CleaveOreTooltipEvents());
        LOGGER.info("CleaveOre (Forge) initialized.");
    }
}
