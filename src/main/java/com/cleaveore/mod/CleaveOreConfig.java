package com.cleaveore.mod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CleaveOreConfig {

    public static final class Data {
        public boolean showFailActionBar = true;
        public boolean showFailXParticles = true;
        public int failCooldownTicks = 4;
        public double failParticleScale = 1.0;
        public double pluckDurabilityChance = 0.6;
        public boolean allowAncientDebrisPluck = false;
        public boolean showPickaxeTooltipHint = true;
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("cleaveore.json");
    private static Data DATA = new Data();

    private CleaveOreConfig() {
    }

    public static synchronized void load() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                saveDefaults();
                return;
            }
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                Data loaded = GSON.fromJson(reader, Data.class);
                if (loaded != null) {
                    DATA = loaded;
                }
            }
        } catch (IOException | JsonParseException e) {
            CleaveOreMod.LOGGER.warn("Failed to load cleaveore config, using defaults: {}", e.getMessage());
            DATA = new Data();
        }
    }

    public static synchronized Data get() {
        return DATA;
    }

    private static void saveDefaults() throws IOException {
        Files.createDirectories(CONFIG_PATH.getParent());
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(DATA, writer);
        }
    }
}
