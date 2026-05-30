package com.cleaveore.mod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CleaveOre Fabric bootstrap.
 * Runtime behavior lives in {@link FabricPluckHandler}.
 */
public class CleaveOreMod implements ModInitializer {

    public static final String MOD_ID = "cleaveore";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("CleaveOre Mod initializing...");
        CleaveOreConfig.load();
        FabricPluckHandler.register();
        LOGGER.info("CleaveOre Mod ready!");
    }
}
