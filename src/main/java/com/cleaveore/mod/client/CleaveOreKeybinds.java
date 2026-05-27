package com.cleaveore.mod.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.cleaveore.mod.CleaveOreMod;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = CleaveOreMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CleaveOreKeybinds {
    public static final String CATEGORY = "key.categories.cleaveore";

    // Mirrors the default "Use Item / Place Block" mouse button to expose a dedicated
    // CleaveOre controls category. Actual ore-pluck logic listens to RightClickBlock,
    // so player key remaps remain compatible.
    public static final KeyMapping ORE_PLUCK = new KeyMapping(
        "key.cleaveore.ore_pluck",
        InputConstants.Type.MOUSE,
        GLFW.GLFW_MOUSE_BUTTON_RIGHT,
        CATEGORY
    );

    private CleaveOreKeybinds() {
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ORE_PLUCK);
    }
}
