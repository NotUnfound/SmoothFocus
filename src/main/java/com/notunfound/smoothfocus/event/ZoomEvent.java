package com.notunfound.smoothfocus.event;

import org.lwjgl.glfw.GLFW;

import com.notunfound.smoothfocus.SmoothFocus;
import com.notunfound.smoothfocus.client.screen.SmoothFocusSettings;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZoomEvent {

	private static final GameSettings settings = Minecraft.getInstance().gameSettings;

	private static final SmoothFocusSettings modSettings = SmoothFocusSettings.INSTANCE;

	static int toggleTimer;
	static double fovModifier;
	static boolean isToggled;

	static boolean queuedSmoothCamera;

	@SubscribeEvent
	public static void renderEvent(final EntityViewRenderEvent event) {

		if (!SmoothFocus.KEY_BIND_ZOOM.isKeyDown() && !isToggled) {
			inactiveTick();
		} else {
			activeTick();
		}

	}

	private static void activeTick() {
		/*
		 * Changing the games setting directly is a bit dirty, but since the only way to
		 * toggle it is with a keybind, it should be fine
		 */
		if (isToggled && modSettings.smoothOnToggle.get()) {
			settings.smoothCamera = true;
		}

		if (isToggled) {
			fovModifier = -(modSettings.maxZoom.get() / 100D);
		}
	}

	private static void inactiveTick() {
		fovModifier = 0;
		queuedSmoothCamera = settings.smoothCamera;
	}

	@SubscribeEvent
	public static void changeFOV(final FOVUpdateEvent event) {

		event.setNewfov((float) (event.getFov() + fovModifier));

		toggleTimer = Math.max(0, toggleTimer - 1);

	}

	@SubscribeEvent
	public static void scrollEvent(final MouseScrollEvent event) {
		if (SmoothFocus.KEY_BIND_ZOOM.isKeyDown()) {
			if (modSettings.smoothOnScroll.get()) {
				settings.smoothCamera = true;
			}
			fovModifier = Math
					.min(Math.max(fovModifier - (event.getScrollDelta() / (15 - modSettings.scrollZoomSpeed.get())),
							-(modSettings.maxZoom.get() / 100D)), 0);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void handleToggle(final KeyInputEvent event) {

		if (event.getKey() == SmoothFocus.KEY_BIND_ZOOM.getKey().getKeyCode()) {

			if (event.getAction() == GLFW.GLFW_PRESS) {

				singleTap();

				if (toggleTimer == 0) {
					toggleTimer = 7;
				} else {
					toggleTimer = 0;
					doubleTap();
				}

			} else if (event.getAction() == GLFW.GLFW_RELEASE && !isToggled) {

				/*
				 * restores original smoothness
				 */
				settings.smoothCamera = queuedSmoothCamera;

			}

		}

	}

	private static void singleTap() {
		if (!modSettings.doubleClickOn.get() && !isToggled) {
			isToggled = true;
		} else if (!modSettings.doubleClickOff.get() && isToggled) {
			isToggled = false;
		}
	}

	private static void doubleTap() {
		if (modSettings.doubleClickOn.get() && !isToggled) {
			isToggled = true;
		} else if (modSettings.doubleClickOff.get() && isToggled) {
			isToggled = false;
		}
	}

}
