package com.notunfound.smoothfocus.event;

import com.notunfound.smoothfocus.SmoothFocus;

import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZoomEvent {

	static int toggleTimer;
	static double fovModifier;
	static boolean isToggled;

	@SubscribeEvent
	public static void changeFOV(final FOVUpdateEvent event) {

		/*
		 * Tick the double-tap timer.
		 */

		toggleTimer = Math.max(0, toggleTimer - 1);

		if (isToggled) {
			fovModifier = -1;
		} else if (!SmoothFocus.keyBindZoom.isKeyDown())
			fovModifier = 0;

		event.setNewfov((float) (event.getFov() + fovModifier));
	}

	@SubscribeEvent
	public static void scrollEvent(final MouseScrollEvent event) {
		if (SmoothFocus.keyBindZoom.isKeyDown()) {
			fovModifier = Math.min(Math.max(fovModifier - (event.getScrollDelta() / 10), -1), 0);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void handleToggle(final KeyInputEvent event) {

		/*
		 * On key initially pressed
		 */

		if (SmoothFocus.keyBindZoom.isKeyDown() && event.getAction() == 1) {

			isToggled = false;

			if (toggleTimer == 0) {
				toggleTimer = 7;
			} else {
				toggleTimer = 0;
				isToggled = true;
			}

		}
	}
}
