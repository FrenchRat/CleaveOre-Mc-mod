package com.veinmine.mod;

import net.fabricmc.api.ClientModInitializer;

public class VeinMineModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Particles are spawned with vanilla BLOCK + GLOW particle types,
        // so no client-side factory registration is required.
    }
}
