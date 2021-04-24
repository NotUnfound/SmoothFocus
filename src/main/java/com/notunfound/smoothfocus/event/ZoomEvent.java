package com.notunfound.smoothfocus.event;

import org.lwjgl.glfw.GLFW;

import com.notunfound.smoothfocus.SmoothFocus;
import com.notunfound.smoothfocus.client.settings.SmoothFocusSettings;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.client.event.InputEvent.RawMouseEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("resource")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZoomEvent {

	/*
	 * Where all the magic happens
	 * 
	 * This class changes the fov, reads the settings, and handles the toggle
	 */

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

	/*
	 * Called every render tick
	 */
	@SubscribeEvent
	public static void renderEvent(final EntityViewRenderEvent event) {

		/*
		 * Resets the all values when a GUI is opened, removing the mouse button
		 * untoggle problems
		 */
		if (Minecraft.getInstance().currentScreen != null) {
			
			SmoothFocus.SMOOTH_CAMERA = false;
			isToggled = false;
			toggleTimer = 0;
			fovModifier = 0;

		} else if (!SmoothFocus.KEY_BIND_ZOOM.isKeyDown() && !isToggled) {

			fovModifier = 0;
			
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

			SmoothFocus.SMOOTH_CAMERA = true;

		} else if (!isToggled && fovModifier < 0 && MODSETTINGS.smoothOnScroll.get()) {

			SmoothFocus.SMOOTH_CAMERA = true;

		}

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

	/*
	 * For if the keybind is set to a mouse button
	 */
	@SubscribeEvent
	public static void handleMouseToggle(final RawMouseEvent event) {
		if (Minecraft.getInstance().currentScreen == null) {
			zoomInput(event.getButton(), event.getAction());
		}
	}

	/*
	 * For if the keybind is set to a key
	 */
	@SubscribeEvent
	public static void handleKeyToggle(final KeyInputEvent event) {
		if (Minecraft.getInstance().currentScreen == null) {
			zoomInput(event.getKey(), event.getAction());
		}
	}

	/*
	 * Handles the toggle/untoggle
	 */
	private static void zoomInput(int button, int action) {

		if (button == SmoothFocus.KEY_BIND_ZOOM.getKey().getKeyCode()) {

			if (action == GLFW.GLFW_PRESS) {

				if (!MODSETTINGS.disableToggle.get()) {
					if (toggleTimer == 0) {
						toggleTimer = 7;
						singleTap();
					} else {
						toggleTimer = 0;
						doubleTap();
					}
				}
				if (MODSETTINGS.startAtMaxZoom.get() && !isToggled) {
					fovModifier = -(MODSETTINGS.maxZoom.get() / 100D);
				}

			} else if (action == GLFW.GLFW_RELEASE && !isToggled) {

				/*
				 * restores original smoothness
				 */

				SmoothFocus.SMOOTH_CAMERA = false;

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
