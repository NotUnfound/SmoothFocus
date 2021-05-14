package com.notunfound.smoothfocus.event;

import org.lwjgl.glfw.GLFW;

import com.notunfound.smoothfocus.SmoothFocus;
import com.notunfound.smoothfocus.client.screen.ConfigEnums.MouseSensitivityModifier;
import com.notunfound.smoothfocus.client.screen.ConfigEnums.ToggleType;
import com.notunfound.smoothfocus.client.screen.ConfigScreen;
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

/*
 * Where all the magic happens
 *
 * This class changes the fov, reads the settings, and handles the toggle
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZoomEvent {

	private static final SmoothFocusSettings MODSETTINGS = SmoothFocusSettings.INSTANCE;

	/*
	 * For the double tap
	 */
	static int timer;

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

		if (SmoothFocus.keyBindConfigure.isPressed() && Minecraft.getInstance().isGameFocused()) {
			Minecraft.getInstance().displayGuiScreen(new ConfigScreen(Minecraft.getInstance().currentScreen, null));
		}

		/*
		 * Resets the all values when the menu is opened, removing the mouse button
		 * untoggle problems
		 */
		if (Minecraft.getInstance().isGamePaused()) {

			SmoothFocus.smoothCamera = false;
			isToggled = false;
			timer = 0;
			fovModifier = 0;
			SmoothFocus.sensitvityModifier = 0;

		} else if (!SmoothFocus.keyBindZoom.isKeyDown() && !isToggled) {

			fovModifier = 0;
			SmoothFocus.sensitvityModifier = 0;

		} else {

			activeTick();

		}

	}

	/*
	 * Called every tick that the default fov should be changed
	 */

	private static void activeTick() {

		if (MODSETTINGS.mouseSensitivityModType.get().equals(MouseSensitivityModifier.SCALED)) {

			SmoothFocus.sensitvityModifier = MathHelper.lerp((fovModifier / -(MODSETTINGS.maxZoom.get()) * 100),
					0, MODSETTINGS.mouseSensitivityReduction.get());

		} else {

			SmoothFocus.sensitvityModifier = MODSETTINGS.mouseSensitivityReduction.get();

		}

		/*
		 * Changes the smoothCamera in the Main class that the mixin then reads
		 */
		if (isToggled && MODSETTINGS.smoothType.get().toggle()) {

			SmoothFocus.smoothCamera = true;

		} else if (!isToggled && fovModifier < 0 && MODSETTINGS.smoothType.get().scroll()) {

			SmoothFocus.smoothCamera = true;

		}

	}

	@SubscribeEvent
	public static void changeSmoothFOV(final FOVUpdateEvent event) {

		timer = (int) Math.max(0, timer - 1f);

		event.setNewfov(MathHelper.lerp(Minecraft.getInstance().gameSettings.fovScaleEffect, 1.0F, event.getFov()));

		event.setNewfov((float) (event.getNewfov() + fovModifier));

	}

	@SubscribeEvent
	public static void changeFOV(final EntityViewRenderEvent.FOVModifier event) {

		event.setFOV(event.getFOV() + fovModifier * 8.5);

	}

	/*
	 * Handles the scroll feature
	 */
	@SubscribeEvent
	public static void scrollEvent(final MouseScrollEvent event) {

		boolean flag = MODSETTINGS.scrollWhenToggled.get() ? true : !isToggled;
		boolean flag1 = MODSETTINGS.scrollWhenToggled.get() && isToggled;

		if ((SmoothFocus.keyBindZoom.isKeyDown() || flag1) && flag) {

			/*
			 * Sets the modifier
			 */
			fovModifier = MathHelper.clamp(
					fovModifier - (event.getScrollDelta() / (40 - (MODSETTINGS.scrollZoomSpeed.get() * 2))),
					-MODSETTINGS.maxZoom.get() / 100D + 0.08, 0D);

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

		if (button == SmoothFocus.keyBindZoom.getKey().getKeyCode()) {

			if (action == GLFW.GLFW_PRESS) {

				if (!MODSETTINGS.toggleType.get().equals(ToggleType.DISABLE)) {

					if (timer == 0) {
						timer = 7;
						singleTap();
					} else {
						doubleTap();
						timer = 0;
					}

				}
				if (MODSETTINGS.startAtMaxZoom.get() && !isToggled) {

					maxFov();

				}

			} else if (action == GLFW.GLFW_RELEASE && !isToggled) {

				/*
				 * restores original smoothness
				 */

				SmoothFocus.smoothCamera = false;

			}

		}
	}
	
	/*
	 * Sets the fov modifier to the maximum allowed value
	 */
	private static void maxFov() {
		fovModifier = -Math.log(MODSETTINGS.maxZoom.get() + 1.41) / 4.3 + 0.08;
	}

	/*
	 * Called when the key is tapped once, not counting the tap at the beginning of
	 * a double tap
	 */

	private static void singleTap() {

		if (!MODSETTINGS.toggleType.get().turnOn() && !isToggled) {

			isToggled = true;
			maxFov();

		} else if (!MODSETTINGS.toggleType.get().turnOff() && isToggled) {

			isToggled = false;

		}
	}

	/*
	 * Called when the key is tapped twice within 7 ticks
	 */
	private static void doubleTap() {

		if (MODSETTINGS.toggleType.get().turnOn() && !isToggled) {

			isToggled = true;
			maxFov();

		} else if (MODSETTINGS.toggleType.get().turnOff() && isToggled) {

			isToggled = false;

		}
	}

}
