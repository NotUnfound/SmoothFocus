package com.notunfound.smoothfocus.client.screen;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.notunfound.smoothfocus.SmoothFocus;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class SmoothFocusSettings {

	public static final ForgeConfigSpec SPEC;
	/*
	 * Singleton of the settings
	 */
	public static final SmoothFocusSettings INSTANCE;
	/*
	 * Where the savedata will actually be stored
	 */
	private static final Path CONFIG_PATH = Paths.get("config", SmoothFocus.MODID + ".toml");

	public BooleanValue smoothOnToggle;	
	public BooleanValue smoothOnScroll;
	public BooleanValue doubleClickOn;
	public BooleanValue doubleClickOff;
	public IntValue scrollZoomSpeed;
	public IntValue maxZoom;

	static {

		Pair<SmoothFocusSettings, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder()
				.configure(SmoothFocusSettings::new);
		INSTANCE = pair.getLeft();
		SPEC = pair.getRight();
		CommentedFileConfig config = CommentedFileConfig.builder(CONFIG_PATH).sync().autoreload()
				.writingMode(WritingMode.REPLACE).build();
		config.load();
		config.save();
		SPEC.setConfig(config);
	}

	private SmoothFocusSettings(ForgeConfigSpec.Builder builder) {

		smoothOnToggle = builder.translation("smoothfocus.config.smooth_on_toggle").define("smoothOnToggle", true);
		smoothOnScroll = builder.translation("smoothfocus.config.smooth_on_scroll").define("smoothOnScroll", true);
		doubleClickOn = builder.translation("smoothfocus.config.toggle_double_click").define("toggleDoubleClick", true);
		doubleClickOff = builder.translation("smoothfocus.config.untoggle_double_click").define("untoggleDoubleClick", false);

		scrollZoomSpeed = builder.translation("smoothfocus.config.smooth_on_toggle").defineInRange("scrollZoomSpeed", 8, 1, 10);
		
		maxZoom = builder.translation("smoothfocus.config.max_zoom").defineInRange("maxZoom", 75, 0, 100);

	}

	public void save() {
		SPEC.save();
	}

}
