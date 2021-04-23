package com.notunfound.smoothfocus.event;

import org.lwjgl.glfw.GLFW;

import com.notunfound.smoothfocus.SmoothFocus;
import com.notunfound.smoothfocus.client.settings.SmoothFocusSettings;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZoomEvent {

	/*
	 * Where all the magic happens
	 * 
	 * This class changes the fov, reads the settings, and handles the toggle
	 */

	private static final GameSettings GAMESETTINGS = Minecraft.getInstance().gameSettings;

	private static final SmoothFocusSettings MODSETTINGS = SmoothFocusSettings.INSTANCE;

	/*
	 * For the double tap
	 */
	static int toggleTimer;

	static double fovModifier;

	static boolean isToggled;

	/*
	 * This allows you to have the vanilla smooth camera on after you disable the
	 * zoom
	 */
	static boolean queuedSmoothCamera;

	@SubscribeEvent
	public static void renderEvent(final EntityViewRenderEvent event) {

		/*
		 * Called every render tick
		 */

		if (!SmoothFocus.KEY_BIND_ZOOM.isKeyDown() && !isToggled) {
			inactiveTick();
		} else {
			activeTick();
		}

	}

	/*
	 * Called every tick that the default fov should be changed
	 */

	private static void activeTick() {

		/*
		 * Changing the games setting directly is a bit dirty, but since the only way to
		 * toggle it is with a keybind it should be fine
		 */

		if (isToggled && MODSETTINGS.smoothOnToggle.get()) {

			GAMESETTINGS.smoothCamera = true;

		} else if (!isToggled && SmoothFocus.KEY_BIND_ZOOM.isKeyDown() && MODSETTINGS.smoothOnScroll.get()) {

			GAMESETTINGS.smoothCamera = true;

		}

	}

	private static void inactiveTick() {

		fovModifier = 0;

		/*
		 * prepare the queued boolean for the next time the zoom is activated
		 */

		queuedSmoothCamera = GAMESETTINGS.smoothCamera;

	}

	@SubscribeEvent
	public static void changeFOV(final FOVUpdateEvent event) {

		event.setNewfov((float) (event.getFov() + fovModifier));

		toggleTimer = Math.max(0, toggleTimer - 1);

	}

	/*
	 * Handles the scroll feature
	 */
	@SubscribeEvent
	public static void scrollEvent(final MouseScrollEvent event) {

		boolean flag = MODSETTINGS.scrollWhenToggled.get() ? true : !isToggled;
		boolean flag1 = MODSETTINGS.scrollWhenToggled.get() && isToggled;

		if ((SmoothFocus.KEY_BIND_ZOOM.isKeyDown() || flag1) && flag) {

			/*
			 * Sets the modifier
			 */
			fovModifier = MathHelper.clamp(
					fovModifier - (event.getScrollDelta() / (15 - MODSETTINGS.scrollZoomSpeed.get())),
					-MODSETTINGS.maxZoom.get() / 100D, 0D);

			/*
			 * Make the hotbar not scroll while zooming in
			 */
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

				GAMESETTINGS.smoothCamera = queuedSmoothCamera;

			}

		}

	}

	private static void singleTap() {

		if (!MODSETTINGS.doubleClickOn.get() && !isToggled) {

			isToggled = true;
			fovModifier = -(MODSETTINGS.maxZoom.get() / 100D);

		} else if (!MODSETTINGS.doubleClickOff.get() && isToggled) {

			isToggled = false;
		}
	}

	private static void doubleTap() {

		if (MODSETTINGS.doubleClickOn.get() && !isToggled) {

			isToggled = true;
			fovModifier = -(MODSETTINGS.maxZoom.get() / 100D);

		} else if (MODSETTINGS.doubleClickOff.get() && isToggled) {

			isToggled = false;
		}
	}

}
