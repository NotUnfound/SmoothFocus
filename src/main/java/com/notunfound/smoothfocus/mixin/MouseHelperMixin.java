package com.notunfound.smoothfocus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.notunfound.smoothfocus.SmoothFocus;
import com.notunfound.smoothfocus.client.screen.ConfigEnums.MouseSensitivityModifier;
import com.notunfound.smoothfocus.client.settings.SmoothFocusSettings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.util.MouseSmoother;
import net.minecraft.client.util.NativeUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/*
 * Woooo, mixins!
 */
@OnlyIn(Dist.CLIENT)
@Mixin(MouseHelper.class)
public class MouseHelperMixin {

	@Shadow
	Minecraft minecraft;
	@Shadow
	private double lastLookTime;
	@Shadow
	private double xVelocity;
	@Shadow
	private double yVelocity;

	private final MouseSmoother xSmoother = new MouseSmoother();
	private final MouseSmoother ySmoother = new MouseSmoother();

	@Shadow
	private boolean isMouseGrabbed() {
		throw new IllegalStateException();
	}

	@Inject(at = @At("HEAD"), method = "updatePlayerLook", cancellable = true)
	private void updatePlayerLook(CallbackInfo callback) {
		double d0 = NativeUtil.getTime();
		double d1 = d0 - this.lastLookTime;
		this.lastLookTime = d0;
		if (this.isMouseGrabbed() && this.minecraft.isGameFocused()) {

			/*
			 * Change the velocities before calculating player look
			 */

			if (!SmoothFocusSettings.INSTANCE.mouseSensitivityModType.get().equals(MouseSensitivityModifier.NONE)) {
				xVelocity /= SmoothFocus.sensitvityModifier * 4 + 1;
				yVelocity /= SmoothFocus.sensitvityModifier * 4 + 1;
			}

			double d4 = this.minecraft.gameSettings.mouseSensitivity * (double) 0.6F + (double) 0.2F;
			double d5 = d4 * d4 * d4 * 8.0D;
			double d2;
			double d3;
			if (this.minecraft.gameSettings.smoothCamera || SmoothFocus.smoothCamera) {
				double d6 = this.xSmoother.smooth(this.xVelocity * d5, d1 * d5);
				double d7 = this.ySmoother.smooth(this.yVelocity * d5, d1 * d5);
				d2 = d6;
				d3 = d7;
			} else {
				this.xSmoother.reset();
				this.ySmoother.reset();
				d2 = this.xVelocity * d5;
				d3 = this.yVelocity * d5;
			}

			this.xVelocity = 0.0D;
			this.yVelocity = 0.0D;
			int i = 1;
			if (this.minecraft.gameSettings.invertMouse) {
				i = -1;
			}

			this.minecraft.getTutorial().onMouseMove(d2, d3);
			if (this.minecraft.player != null) {
				this.minecraft.player.rotateTowards(d2, d3 * (double) i);
			}

		} else {
			this.xVelocity = 0.0D;
			this.yVelocity = 0.0D;
		}
		/*
		 * So that it doesn't happen twice
		 */
		callback.cancel();
	}

}
