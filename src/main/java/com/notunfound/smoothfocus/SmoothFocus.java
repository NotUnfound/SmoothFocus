package com.notunfound.smoothfocus;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SmoothFocus.MODID)
public class SmoothFocus {

	public static final String MODID = "smoothfocus";

	public static KeyBinding keyBindZoom;

	public SmoothFocus() {

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerKeybind);

		MinecraftForge.EVENT_BUS.register(this);
		
	}

	private void registerKeybind(final FMLClientSetupEvent event) {
		
		keyBindZoom = new KeyBinding("key.zoom_in", 76, "key.categories.smoothfocus");

		ClientRegistry.registerKeyBinding(keyBindZoom);

	}
}
