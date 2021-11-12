package com.notunfound.smoothfocus.event;

import java.util.OptionalInt;

import org.lwjgl.glfw.GLFW;

import com.notunfound.smoothfocus.SmoothFocusClient;
import com.notunfound.smoothfocus.client.screen.ConfigEnums.MouseSensitivityModifier;
import com.notunfound.smoothfocus.client.screen.ConfigEnums.ToggleType;
import com.notunfound.smoothfocus.client.screen.ConfigScreen;
import com.notunfound.smoothfocus.client.settings.SmoothFocusSettings;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
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
@SuppressWarnings("resource")
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
        
        // open the config
        if (SmoothFocusClient.keyBindConfigure.consumeClick() && Minecraft.getInstance().screen == null) {
            Minecraft.getInstance().setScreen(new ConfigScreen(null, null));
        }

        /*
         * Resets the all values when the menu is opened, removing the mouse button
         * untoggle problems
         */
        if (Minecraft.getInstance().screen != null) {

            SmoothFocusClient.smoothCamera = false;
            isToggled = false;
            timer = 0;
            fovModifier = 0;
            SmoothFocusClient.sensitvityModifier = 0;

        } else if (!SmoothFocusClient.keyBindZoom.isDown() && !isToggled) {

            fovModifier = 0;
            SmoothFocusClient.sensitvityModifier = 0;

        } else {

            activeTick();

        }

    }

    /*
     * Called every tick that the default fov should be changed
     */

    private static void activeTick() {

        if (MODSETTINGS.mouseSensitivityModType.get().equals(MouseSensitivityModifier.SCALED)) {

            SmoothFocusClient.sensitvityModifier = Mth.lerp((fovModifier / -(MODSETTINGS.maxZoom.get()) * 100), 0,
                    MODSETTINGS.mouseSensitivityReduction.get());

        } else {

            SmoothFocusClient.sensitvityModifier = MODSETTINGS.mouseSensitivityReduction.get();

        }

        /*
         * Changes the smoothCamera in the Main class that the mixin then reads
         */
        if (isToggled && MODSETTINGS.smoothType.get().toggle()) {

            SmoothFocusClient.smoothCamera = true;

        } else if (!isToggled && fovModifier < 0 && MODSETTINGS.smoothType.get().scroll()) {

            SmoothFocusClient.smoothCamera = true;

        }

    }

    /*
     * Supplies the smooth zooming effect
     */
    @SubscribeEvent
    public static void changeSmoothFOV(final FOVUpdateEvent event) {

        timer = (int) Math.max(0, timer - 1f);

//        Minecraft game = Minecraft.getInstance();
//        
// this might not be needed
//        if (fovModifier == 0 && game.screen == null)
//            game.worldRenderer.setDisplayListEntitiesDirty();

        /*
         * Act as if the fov effects slider was disabled when zooming to keep zoom
         * consistent
         */
        float fovEffects = fovModifier == 0 ? Minecraft.getInstance().options.fovEffectScale : 0.0f;

        event.setNewfov(Mth.lerp(fovEffects, 1.0F, event.getFov()));

        event.setNewfov((float) ((event.getNewfov() + fovModifier) + 0.08));
    }

    /*
     * Allows the zoom to increase a lot
     */
    @SubscribeEvent
    public static void changeFOV(final EntityViewRenderEvent.FOVModifier event) {

        event.setFOV(event.getFOV() + fovModifier * 8.5);

        /*
         * For debug, may implement as a feature later on
         */

//		Minecraft.getInstance().player.sendStatusMessage(new StringTextComponent("FovMod = " + fovModifier + " SkipRenderWorld = " + Minecraft.getInstance().skipRenderWorld), true);

    }

    /*
     * Handles the scroll feature
     */
    @SubscribeEvent
    public static void scrollEvent(final MouseScrollEvent event) {

        boolean flag = MODSETTINGS.scrollWhenToggled.get() ? true : !isToggled;
        boolean flag1 = MODSETTINGS.scrollWhenToggled.get() && isToggled;

        if ((SmoothFocusClient.keyBindZoom.isDown() || flag1) && flag) {

            /*
             * Sets the modifier
             */
            fovModifier = Mth.clamp(
                    fovModifier - (event.getScrollDelta() / (40 - (MODSETTINGS.scrollZoomSpeed.get() * 2))), maxFov(),
                    0D);

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
        if (Minecraft.getInstance().screen == null) {
            zoomInput(event.getButton(), event.getAction());
        }
    }

    /*
     * For if the keybind is set to a key
     */
    @SubscribeEvent
    public static void handleKeyToggle(final KeyInputEvent event) {
        if (Minecraft.getInstance().screen == null) {
            zoomInput(event.getKey(), event.getAction());
        }
    }

    /*
     * Handles the toggle/untoggle
     */
    private static void zoomInput(int button, int action) {

        OptionalInt keyCode = SmoothFocusClient.keyBindZoom.getKey().getNumericKeyValue();

        if (keyCode.isEmpty()) {
            return;
        } else if (button == keyCode.getAsInt()) {

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

                    fovModifier = maxFov();

                }

            } else if (action == GLFW.GLFW_RELEASE && !isToggled) {

                /*
                 * restores original smoothness
                 */

                SmoothFocusClient.smoothCamera = false;

            }

        }
    }

    /*
     * Sets the fov modifier to the maximum allowed value
     */
    private static double maxFov() {
        return -Math.log(MODSETTINGS.maxZoom.get() + 1.41) / 4.3 + 0.08;
    }

    /*
     * Called when the key is tapped once, not counting the tap at the beginning of
     * a double tap
     */

    private static void singleTap() {

        if (!MODSETTINGS.toggleType.get().turnOn() && !isToggled) {

            isToggled = true;
            fovModifier = maxFov();

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
            fovModifier = maxFov();

        } else if (MODSETTINGS.toggleType.get().turnOff() && isToggled) {

            isToggled = false;

        }
    }

}
