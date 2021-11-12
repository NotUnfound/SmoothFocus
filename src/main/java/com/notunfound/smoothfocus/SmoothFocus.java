package com.notunfound.smoothfocus;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(SmoothFocus.MODID)
public class SmoothFocus {

    public static final String MODID = "smoothfocus";

    public SmoothFocus() {

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> SmoothFocusClient::new);

        /*
         * Establish that it is client-side only
         */

//		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
//				() -> Pair.of(() -> "secret text", (, networkBoolean) -> networkBoolean));

        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> "secret text",
                        (version, networkBoolean) -> networkBoolean));

        MinecraftForge.EVENT_BUS.register(this);

    }
}
