package com.veinmine.mod.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.veinmine.mod.VeinMineMod;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = VeinMineMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class VeinMineKeybinds {
    public static final String CATEGORY = "key.categories.veinmine";

    // Mirrors the default "Use Item / Place Block" mouse button to expose a dedicated
    // CleaveOre controls category. Actual ore-pluck logic listens to RightClickBlock,
    // so player key remaps remain compatible.
    public static final KeyMapping ORE_PLUCK = new KeyMapping(
        "key.veinmine.ore_pluck",
        InputConstants.Type.MOUSE,
        GLFW.GLFW_MOUSE_BUTTON_RIGHT,
        CATEGORY
    );

    private VeinMineKeybinds() {
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ORE_PLUCK);
    }
}
