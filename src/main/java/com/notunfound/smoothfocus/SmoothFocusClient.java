package com.notunfound.smoothfocus;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.notunfound.smoothfocus.client.screen.ConfigScreen;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmlclient.ConfigGuiHandler;
import net.minecraftforge.fmlclient.registry.ClientRegistry;

public class SmoothFocusClient {

    public static boolean smoothCamera = false;
    public static KeyMapping keyBindZoom;
    public static KeyMapping keyBindConfigure;
    public static double sensitvityModifier = 1;

    public SmoothFocusClient() {

        /*
         * Set up the event listeners
         */
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerOptions);

        /*
         * Make the config screen available from the mod's config button
         */
        
//        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
//                () -> new ConfigScreen(y, x.gameSettings));

        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory((mc, screen) -> new ConfigScreen(screen, null)));

    }

    private void registerOptions(final FMLClientSetupEvent event) {

        keyBindZoom = new KeyMapping("key.zoom_in", GLFW.GLFW_KEY_J, "key.categories.smoothfocus");

        keyBindConfigure = new KeyMapping("key.smoothfocus_configure", InputConstants.UNKNOWN.getValue(),
                "key.categories.smoothfocus");

        /*
         * Register the keybinds so that they can be configured in the controls
         */
        ClientRegistry.registerKeyBinding(keyBindZoom);
        ClientRegistry.registerKeyBinding(keyBindConfigure);

    }

}
