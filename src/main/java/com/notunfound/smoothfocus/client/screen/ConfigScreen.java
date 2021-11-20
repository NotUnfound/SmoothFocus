package com.notunfound.smoothfocus.client.screen;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.notunfound.smoothfocus.client.screen.ConfigEnums.IModConfigEnum;
import com.notunfound.smoothfocus.client.settings.SmoothFocusSettings;

import net.minecraft.client.CycleOption;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

@OnlyIn(Dist.CLIENT)
public class ConfigScreen extends OptionsSubScreen {

    private static final SmoothFocusSettings SETTINGS = SmoothFocusSettings.INSTANCE;

    private CycleOption<?> smoothTypeOption = createIterableOption(false, "smoothfocus.config.smooth_type",
            SETTINGS.smoothType, ConfigEnums.SmoothType.values());

    private CycleOption<?> toggleTypeOption = createIterableOption(false, "smoothfocus.config.toggle_type",
            SETTINGS.toggleType, ConfigEnums.ToggleType.values());

    private CycleOption<?> mouseSensitivityTypeOption = createIterableOption(true,
            "smoothfocus.config.mouse_sensitivity", SETTINGS.mouseSensitivityModType,
            ConfigEnums.MouseSensitivityModifier.values());

    private CycleOption<Boolean> scrollWhenToggledOption = createBooleanOption("smoothfocus.config.scroll_when_toggled",
            SETTINGS.scrollWhenToggled);

    private CycleOption<Boolean> startAtMaxZoomOption = createBooleanOption("smoothfocus.config.start_at_max_zoom",
            SETTINGS.startAtMaxZoom);

    private ModSliderOption mouseSensitvityReductionOption = new ModSliderOption(
            "smoothfocus.config.mouse_sensitivity_reduction", 0, 10, 1.0f, SETTINGS.mouseSensitivityReduction);

    private ModSliderOption maxZoomOption = new ModSliderOption("smoothfocus.config.max_zoom", 1, 100, 1.0f,
            SETTINGS.maxZoom);

    private ModSliderOption scrollSpeedOption = new ModSliderOption("smoothfocus.config.scroll_speed", 1, 20, 1.0f,
            SETTINGS.scrollZoomSpeed);

    private OptionsList options;

    private Screen parentScreen;

    public ConfigScreen(Screen parent, Options settings) {
        super(parent, settings, new TranslatableComponent("smoothfocus.config.title"));
    }

    @Override
    protected void init() {

        options = new OptionsList(minecraft, width, height, 24, height - 32, 25);

        Option[] optionList = new Option[] { smoothTypeOption, toggleTypeOption, mouseSensitivityTypeOption,
                mouseSensitvityReductionOption, startAtMaxZoomOption, scrollWhenToggledOption };

        Option[] smallOptionList = new Option[] { scrollSpeedOption, maxZoomOption };

        /*
         * This is so that the buttons will be wide, one per row
         */
        for (Option o : optionList) {
            options.addBig(o);
        }

        options.addSmall(smallOptionList);

//		children.add(options);
        addWidget(options);

        this.addRenderableWidget(
                new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_DONE, (x) -> {
                    onClose();
                    minecraft.setScreen(parentScreen);
                }));

    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        renderBackground(matrixStack);

        options.render(matrixStack, mouseX, mouseY, partialTicks);

        drawCenteredString(matrixStack, this.font, this.title, width / 2, 8, 0xFFFFFF);

        // handle tooltip rendering

        List<FormattedCharSequence> list = tooltipAt(options, mouseX, mouseY);

        if (list != null) {
            this.renderTooltip(matrixStack, list, mouseX, mouseY);
        }

        // =========================

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        SETTINGS.save();
    }

    /*
     * More concise versions of the forge options for this specific usage
     */

    protected CycleOption<Boolean> createBooleanOption(String name, BooleanValue setting) {

        return CycleOption.createOnOff(name, (x) -> {
            return setting.get();
        }, (x, y, z) -> {
            setting.set(z);
        });
    }

    protected class ModSliderOption extends ProgressOption {

        public ModSliderOption(String name, double minValueIn, double maxValueIn, float stepSizeIn, IntValue setting) {
            super(name, minValueIn, maxValueIn, stepSizeIn, x -> {

                return (double) setting.get();

            }, (x, y) -> {

                setting.set(y.intValue());

            }, (x, y) -> {

                return createValuedKey(Integer.toString((int) y.get(x)), name);

            });
        }

    }

    protected CycleOption<IModConfigEnum> createIterableOption(boolean tooltip, String translationKeyIn,
            EnumValue<? extends IModConfigEnum> value, IModConfigEnum[] options) {

        /*
         * Naming Conventions:
         * 
         * - Tooltip = smoothfocus.config.<option name>.<enum value>.<tooltip name> -
         * Enum = smoothfocus.config.<option name>.<enum value>
         */

        CycleOption<IModConfigEnum> option = CycleOption.create(translationKeyIn, options, (x) -> {

            return new TranslatableComponent(translationKeyIn + "." + x.toString().toLowerCase());

        }, (x) -> {
            return value.get();
        }, (x, y, z) -> {
            value.set(value.get().next());
        });

        return tooltip ? option.setTooltip((x) -> {
            return (y) -> {
                return x.font.split(new TranslatableComponent(
                        translationKeyIn + "." + value.get().name().toLowerCase() + ".tooltip"), 200);
            };
        }) : option;

    }

    protected static TranslatableComponent createValuedKey(String value, String name) {
        return new TranslatableComponent("options.generic_value", new TranslatableComponent(name),
                new TranslatableComponent(value));
    }

}
