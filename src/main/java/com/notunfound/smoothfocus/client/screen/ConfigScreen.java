package com.notunfound.smoothfocus.client.screen;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.notunfound.smoothfocus.client.screen.ConfigEnums.IModConfigEnum;
import com.notunfound.smoothfocus.client.settings.SmoothFocusSettings;

import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

@OnlyIn(Dist.CLIENT)
public class ConfigScreen extends SettingsScreen {

	private static final SmoothFocusSettings SETTINGS = SmoothFocusSettings.INSTANCE;

	private ModIteratableOption smoothTypeOption = new ModIteratableOption(false, "smoothfocus.config.smooth_type",
			SETTINGS.smoothType);

	private ModIteratableOption toggleTypeOption = new ModIteratableOption(false, "smoothfocus.config.toggle_type",
			SETTINGS.toggleType);

	private ModIteratableOption mouseSensitivityTypeOption = new ModIteratableOption(true,
			"smoothfocus.config.mouse_sensitivity", SETTINGS.mouseSensitivityModType);

	private ModBooleanOption scrollWhenToggledOption = new ModBooleanOption("smoothfocus.config.scroll_when_toggled",
			SETTINGS.scrollWhenToggled);

	private ModBooleanOption startAtMaxZoomOption = new ModBooleanOption("smoothfocus.config.start_at_max_zoom",
			SETTINGS.startAtMaxZoom);

	private ModSliderOption mouseSensitvityReductionOption = new ModSliderOption(
			"smoothfocus.config.mouse_sensitivity_reduction", 0, 10, 1.0f, SETTINGS.mouseSensitivityReduction);

	private ModSliderOption maxZoomOption = new ModSliderOption("smoothfocus.config.max_zoom", 1, 100, 1.0f,
			SETTINGS.maxZoom);

	private ModSliderOption scrollSpeedOption = new ModSliderOption("smoothfocus.config.scroll_speed", 1, 20, 1.0f,
			SETTINGS.scrollZoomSpeed);

	private OptionsRowList options;

	private Screen parentScreen;

	public ConfigScreen(Screen parent, GameSettings settings) {
		super(parent, settings, new TranslationTextComponent("smoothfocus.config.title"));
	}

	@Override
	protected void init() {

		options = new OptionsRowList(minecraft, width, height, 24, height - 32, 25);

		AbstractOption[] optionList = new AbstractOption[] { smoothTypeOption, toggleTypeOption,
				mouseSensitivityTypeOption, mouseSensitvityReductionOption, startAtMaxZoomOption,
				scrollWhenToggledOption };
		AbstractOption[] smallOptionList = new AbstractOption[] { scrollSpeedOption, maxZoomOption };

		/*
		 * This is so that the buttons will be wide, one per row
		 */
		for (AbstractOption o : optionList) {
			options.addOption(o);
		}

		options.addOptions(smallOptionList);

		children.add(options);

		this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, DialogTexts.GUI_DONE, (x) -> {
			onClose();
			minecraft.displayGuiScreen(parentScreen);
		}));

	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

		renderBackground(matrixStack);

		options.render(matrixStack, mouseX, mouseY, partialTicks);

		drawCenteredString(matrixStack, this.font, this.title, width / 2, 8, 0xFFFFFF);

		List<IReorderingProcessor> list = func_243293_a(options, mouseX, mouseY);
		if (list != null) {
			this.renderTooltip(matrixStack, list, mouseX, mouseY);
		}

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public void onClose() {
		SETTINGS.save();
	}

	/*
	 * More concise versions of the forge options for this specific usage
	 */

	protected class ModBooleanOption extends BooleanOption {

		public ModBooleanOption(String name, BooleanValue setting) {
			super(name, (x) -> {
				return setting.get();
			}, (x, y) -> {
				setting.set(y);
			});
		}
	}

	protected class ModSliderOption extends SliderPercentageOption {

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

	protected class ModIteratableOption extends IteratableOption {

		/*
		 * Naming Conventions:
		 * 
		 * - Tooltip = smoothfocus.config.<option name>.<enum value>.<tooltip name> -
		 * Enum = smoothfocus.config.<option name>.<enum value>
		 */

		public ModIteratableOption(boolean tooltip, String translationKeyIn,
				EnumValue<? extends IModConfigEnum> value) {
			super(translationKeyIn, (x, y) -> {

				value.set(value.get().next());

			}, (x, y) -> {

				String valueKey = translationKeyIn + "." + value.get().name().toLowerCase();

				if (tooltip)
					y.setOptionValues(Minecraft.getInstance().fontRenderer
							.trimStringToWidth(new TranslationTextComponent(valueKey + ".tooltip"), 200));

				return createValuedKey(valueKey, translationKeyIn);
			});
		}

	}

	protected static ITextComponent createValuedKey(String value, String name) {
		return new TranslationTextComponent("options.generic_value", new TranslationTextComponent(name),
				new TranslationTextComponent(value));
	}

}
