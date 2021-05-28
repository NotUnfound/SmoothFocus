package com.notunfound.smoothfocus.client.settings;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.notunfound.smoothfocus.SmoothFocus;
import com.notunfound.smoothfocus.client.screen.ConfigEnums;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
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

	public EnumValue<ConfigEnums.SmoothType> smoothType;
	public EnumValue<ConfigEnums.ToggleType> toggleType;
	public EnumValue<ConfigEnums.MouseSensitivityModifier> mouseSensitivityModType;

	public BooleanValue scrollWhenToggled;
	public BooleanValue startAtMaxZoom;
	public BooleanValue disableToggle;
	
	public IntValue mouseSensitivityReduction;
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
		
		smoothType = builder.translation("smoothfocus.config.smooth_type").defineEnum("smoothType", ConfigEnums.SmoothType.BOTH);
		
		toggleType = builder.translation("smoothfocus.config.smooth_type").defineEnum("toggleType", ConfigEnums.ToggleType.DOUBLE_TAP_ON);
		
		mouseSensitivityModType = builder.translation("smoothfocus.config.mouse_sensitivity").defineEnum("mouseSensitivityMod", ConfigEnums.MouseSensitivityModifier.NONE);
		
		scrollWhenToggled = builder.translation("smoothfocus.config.scroll_when_toggled").define("scrollWhenToggled", false);
		
		startAtMaxZoom = builder.translation("smoothfocus.config.start_at_max_zoom").define("sartAtMaxZoom", false);
				
		mouseSensitivityReduction = builder.translation("smoothfocus.config.mouse_sensitivity_reduction").defineInRange("mouseSensitvityReduction", 5, 0, 10);
		
		scrollZoomSpeed = builder.translation("smoothfocus.config.scroll_zoom_speed").defineInRange("scrollZoomSpeed", 12, 1, 20);
		
		maxZoom = builder.translation("smoothfocus.config.max_zoom").defineInRange("maxZoom", 50, 0, 100);

	}

	public void save() {
		SPEC.save();
	}

}
