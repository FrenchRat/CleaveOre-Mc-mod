package com.veinmine.mod;

import com.veinmine.mod.particle.OrePopParticle;
import com.veinmine.mod.registry.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class VeinMineModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register the client-side factory for our custom ore-pop particle.
        // This tells Minecraft how to *render* the particle (the server just spawns it).
        ParticleFactoryRegistry.getInstance().register(
            ModParticles.ORE_POP,
            OrePopParticle.Factory::new
        );
    }
}
