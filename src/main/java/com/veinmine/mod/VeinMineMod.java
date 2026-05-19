package com.veinmine.mod;

import com.veinmine.mod.registry.ModBlocks;
import com.veinmine.mod.registry.ModParticles;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * VeinMine Mod - Two-Stage Ore Mining
 *
 * How it works:
 * - Stage 1: Player hits an ore block → ore gem/vein breaks off quickly (fast mining speed),
 *             leaving behind a "hollowed" stone shell block.
 * - Stage 2: Player mines the stone shell normally → it breaks like regular stone.
 *
 * The "stripped" blocks are registered as new block types that look like hollow stone
 * but still count as full blocks for all purposes (light, collisions, etc.)
 */
public class VeinMineMod implements ModInitializer {

    public static final String MOD_ID = "veinmine";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("VeinMine Mod initializing...");

        // Register our custom blocks (the hollowed-out shell variants)
        ModBlocks.register();

        // Register our custom particle effects for the ore-popping visual
        ModParticles.register();

        LOGGER.info("VeinMine Mod ready!");
    }
}
