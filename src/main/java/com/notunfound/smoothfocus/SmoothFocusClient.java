package com.notunfound.smoothfocus;

import org.lwjgl.glfw.GLFW;

import com.notunfound.smoothfocus.client.screen.ConfigScreen;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY,
				() -> (x, y) -> new ConfigScreen(y, x.gameSettings));

	}

	private void registerOptions(final FMLClientSetupEvent event) {

		keyBindZoom = new KeyBinding("key.zoom_in", GLFW.GLFW_KEY_J, "key.categories.smoothfocus");

		keyBindConfigure = new KeyBinding("key.smoothfocus_configure", InputMappings.INPUT_INVALID.getKeyCode(),
				"key.categories.smoothfocus");

		/*
		 * Register the keybinds so that they can be configured in the controls
		 */
		ClientRegistry.registerKeyBinding(keyBindZoom);
		ClientRegistry.registerKeyBinding(keyBindConfigure);

	}

}
