package com.notunfound.smoothfocus.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.AbstractOption;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfigScreen extends Screen {

	private static final SmoothFocusSettings SETTINGS = SmoothFocusSettings.INSTANCE;

	private BooleanOption smoothOnToggleOption = new BooleanOption("smoothfocus.config.smooth_on_toggle", (x) -> {

		return SETTINGS.smoothOnToggle.get();
	}, (x, y) -> {
		SETTINGS.smoothOnToggle.set(y);
	});
	private BooleanOption smoothOnScrollOption = new BooleanOption("smoothfocus.config.smooth_on_scroll", (x) -> {
		return SETTINGS.smoothOnScroll.get();
	}, (x, y) -> {
		SETTINGS.smoothOnScroll.set(y);
	});
	private BooleanOption doubleClickOnOption = new BooleanOption("smoothfocus.config.double_click_on", (x) -> {
		return SETTINGS.doubleClickOn.get();
	}, (x, y) -> {
		SETTINGS.doubleClickOn.set(y);
	});
	private BooleanOption doubleClickOffOption = new BooleanOption("smoothfocus.config.double_click_off", (x) -> {
		return SETTINGS.doubleClickOff.get();
	}, (x, y) -> {
		SETTINGS.doubleClickOff.set(y);
	});
	private SliderPercentageOption maxZoomOption = new SliderPercentageOption("smoothfocus.config.max_zoom", 0, 100,
			1.0f, x -> {
				return (double) SETTINGS.maxZoom.get();
			}, (x, y) -> {
				SETTINGS.maxZoom.set(y.intValue());
			}, (x, y) -> {
				return y.getMessageWithValue((int) y.get(x));
			});
	private SliderPercentageOption scrollSpeedOption = new SliderPercentageOption("smoothfocus.config.scroll_speed", 1,
			10, 1.0f, x -> {
				return (double) SETTINGS.scrollZoomSpeed.get();
			}, (x, y) -> {
				SETTINGS.scrollZoomSpeed.set(y.intValue());
			}, (x, y) -> {
				return y.getMessageWithValue((int) y.get(x));
			});

	private OptionsRowList options;

	private Screen parentScreen;

	public ConfigScreen(Screen parent) {
		super(new TranslationTextComponent("smoothfocus.config_gui.title", "SmoothFocus"));
		this.parentScreen = parent;
	}

	@Override
	protected void init() {

		options = new OptionsRowList(minecraft, width, height, 24, height - 32, 25);

		options.addOptions(new AbstractOption[] { smoothOnToggleOption, smoothOnScrollOption, doubleClickOnOption,
				doubleClickOffOption, scrollSpeedOption, maxZoomOption });

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

}
