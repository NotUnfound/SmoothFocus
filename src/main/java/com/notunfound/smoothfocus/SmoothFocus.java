package com.notunfound.smoothfocus;

import org.lwjgl.glfw.GLFW;

import com.notunfound.smoothfocus.client.screen.ConfigScreen;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SmoothFocus.MODID)
public class SmoothFocus {

	public static final String MODID = "smoothfocus";
	public static boolean smoothCamera = false;
	public static KeyBinding keyBindZoom;
	public static KeyBinding keyBindConfigure;
	public static double sensitvityModifier = 1;

	public SmoothFocus() {

		/*
		 * Set up the event listeners
		 */
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerOptions);

		/*
		 * Make the config screen available from the mod's config button
		 */
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY,
				() -> (x, y) -> new ConfigScreen(y, x.gameSettings));

		/*
		 * Register for modloading
		 */
		MinecraftForge.EVENT_BUS.register(this);

	}

	private void registerOptions(final FMLClientSetupEvent event) {

		keyBindZoom = new KeyBinding("key.zoom_in", GLFW.GLFW_KEY_J, "key.categories.smoothfocus");

		keyBindConfigure = new KeyBinding("key.smoothfocus_configure", InputMappings.INPUT_INVALID.getKeyCode(),
				"key.categories.smoothfocus");

		/*
		 * Regiter the keybinds so that they can be configured in the controls
		 */
		ClientRegistry.registerKeyBinding(keyBindZoom);
		ClientRegistry.registerKeyBinding(keyBindConfigure);

	}
}
