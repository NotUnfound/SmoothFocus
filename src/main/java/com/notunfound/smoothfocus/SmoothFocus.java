package com.notunfound.smoothfocus;

import org.lwjgl.glfw.GLFW;

import com.notunfound.smoothfocus.client.screen.ConfigScreen;

import net.minecraft.client.settings.KeyBinding;
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
	public static boolean SMOOTH_CAMERA = false;
	public static KeyBinding KEY_BIND_ZOOM;
	

	public SmoothFocus() {

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerOptions);

		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY,
				() -> (x, y) -> new ConfigScreen(y));

		MinecraftForge.EVENT_BUS.register(this);

	}

	private void registerOptions(final FMLClientSetupEvent event) {

		KEY_BIND_ZOOM = new KeyBinding("key.zoom_in", GLFW.GLFW_KEY_J, "key.categories.smoothfocus");

		ClientRegistry.registerKeyBinding(KEY_BIND_ZOOM);
		
	}
}
