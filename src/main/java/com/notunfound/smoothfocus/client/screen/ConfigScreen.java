package com.notunfound.smoothfocus.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.notunfound.smoothfocus.client.settings.SmoothFocusSettings;

import net.minecraft.client.AbstractOption;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

@OnlyIn(Dist.CLIENT)
public class ConfigScreen extends Screen {

	private static final SmoothFocusSettings SETTINGS = SmoothFocusSettings.INSTANCE;

	private BooleanOption smoothOnToggleOption = new ModBooleanOption("smoothfocus.config.smooth_on_toggle", SETTINGS.smoothOnToggle);

	private BooleanOption smoothOnScrollOption = new ModBooleanOption("smoothfocus.config.smooth_on_scroll", SETTINGS.smoothOnScroll);

	private BooleanOption doubleClickOnOption = new ModBooleanOption("smoothfocus.config.double_click_on", SETTINGS.doubleClickOn);

	private BooleanOption doubleClickOffOption = new ModBooleanOption("smoothfocus.config.double_click_off", SETTINGS.doubleClickOff);

	private BooleanOption scrollWhenToggled = new ModBooleanOption("smoothfocus.config.scroll_when_toggled", SETTINGS.scrollWhenToggled);

	private SliderPercentageOption maxZoomOption = new ModSliderOption("smoothfocus.config.max_zoom",
			0, 100, 1.0f, SETTINGS.maxZoom);

	private SliderPercentageOption scrollSpeedOption = new ModSliderOption(
			"smoothfocus.config.scroll_speed", 1, 10, 1.0f, SETTINGS.scrollZoomSpeed);

	private OptionsRowList options;

	private Screen parentScreen;

	public ConfigScreen(Screen parent) {
		super(new TranslationTextComponent("smoothfocus.config_gui.title"));
		this.parentScreen = parent;
	}

	@Override
	protected void init() {

		options = new OptionsRowList(minecraft, width, height, 24, height - 32, 25);

		AbstractOption[] optionsArray = new AbstractOption[] { smoothOnToggleOption, smoothOnScrollOption,
				doubleClickOnOption, doubleClickOffOption, scrollWhenToggled, scrollSpeedOption, maxZoomOption };

		/*
		 * This is so that the buttons will be wide, one per row
		 */
		for (AbstractOption o : optionsArray) {
			options.addOption(o);
		}

		children.add(options);

		this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, DialogTexts.GUI_DONE, (x) -> {
			minecraft.displayGuiScreen(parentScreen);
		}));

	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

		renderBackground(matrixStack);

		options.render(matrixStack, mouseX, mouseY, partialTicks);

		drawCenteredString(matrixStack, this.font, this.title, width / 2, 8, 0xFFFFFF);

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public void onClose() {
		SETTINGS.save();
		super.onClose();
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

				return createValuedKey(y.get(x), name);

			});
		}

	}

	protected static ITextComponent createValuedKey(double value, String name) {
		return new TranslationTextComponent("options.generic_value", new TranslationTextComponent(name),
				new StringTextComponent(Integer.toString((int) value)));
	}

}
