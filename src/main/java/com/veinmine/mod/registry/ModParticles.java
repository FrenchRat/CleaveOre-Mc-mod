package com.veinmine.mod.registry;

import com.veinmine.mod.VeinMineMod;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * ModParticles holds the registration for our custom "ore pop" particle.
 *
 * We use a DefaultParticleType (no extra data needed — colour is baked into
 * the texture atlas entry we define in assets). For a shader-friendly look
 * we'll make the particle billboard-aligned, softly alpha-fading, and
 * bloom-aware via the translucent render layer.
 */
public class ModParticles {

    /**
     * ORE_POP fires when stage-1 mining completes. It bursts outward in a
     * hemisphere from the mined face, carrying the colour of the ore's gem/dust
     * (set per-call via texture variation in particles/ore_pop.json).
     */
    public static DefaultParticleType ORE_POP;

    public static void register() {
        ORE_POP = Registry.register(
            Registries.PARTICLE_TYPE,
            new Identifier(VeinMineMod.MOD_ID, "ore_pop"),
            FabricParticleTypes.simple()
        );
        VeinMineMod.LOGGER.info("VeinMine particles registered.");
    }
}
