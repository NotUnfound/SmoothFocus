package com.notunfound.smoothfocus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.Blaze3D;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.util.SmoothDouble;

/*
 * Woooo, mixins!
 */
@Mixin(MouseHandler.class)
public class MouseHelperMixin {

    @Shadow
    Minecraft minecraft;
    @Shadow
    private double lastMouseEventTime;
    @Shadow
    private double accumulatedDX;
    @Shadow
    private double accumulatedDY;

    private final SmoothDouble smoothTurnX = new SmoothDouble();
    private final SmoothDouble smoothTurnY = new SmoothDouble();

    @Shadow
    private boolean isMouseGrabbed() {
        throw new IllegalStateException();
    }

    @Inject(at = @At("HEAD"), method = "turnPlayer", cancellable = true)
    public void turnPlayer(CallbackInfo callback) {
        double d0 = Blaze3D.getTime();
        double d1 = d0 - this.lastMouseEventTime;
        this.lastMouseEventTime = d0;
        if (this.isMouseGrabbed() && this.minecraft.isWindowActive()) {
            double d4 = this.minecraft.options.sensitivity * (double) 0.6F + (double) 0.2F;
            double d5 = d4 * d4 * d4;
            double d6 = d5 * 8.0D;
            double d2;
            double d3;
            if (this.minecraft.options.smoothCamera) {
                double d7 = this.smoothTurnX.getNewDeltaValue(this.accumulatedDX * d6, d1 * d6);
                double d8 = this.smoothTurnY.getNewDeltaValue(this.accumulatedDY * d6, d1 * d6);
                d2 = d7;
                d3 = d8;
            } else if (this.minecraft.options.getCameraType().isFirstPerson() && this.minecraft.player.isScoping()) {
                this.smoothTurnX.reset();
                this.smoothTurnY.reset();
                d2 = this.accumulatedDX * d5;
                d3 = this.accumulatedDY * d5;
            } else {
                this.smoothTurnX.reset();
                this.smoothTurnY.reset();
                d2 = this.accumulatedDX * d6;
                d3 = this.accumulatedDY * d6;
            }

            this.accumulatedDX = 0.0D;
            this.accumulatedDY = 0.0D;
            int i = 1;
            if (this.minecraft.options.invertYMouse) {
                i = -1;
            }

            this.minecraft.getTutorial().onMouse(d2, d3);
            if (this.minecraft.player != null) {
                this.minecraft.player.turn(d2, d3 * (double) i);
            }

        } else {
            this.accumulatedDX = 0.0D;
            this.accumulatedDY = 0.0D;
        }

        /*
         * So that it doesn't happen twice
         */
        callback.cancel();
    }

//	private void updatePlayerLook(CallbackInfo callback) {
//		double d0 = Blaze3D.getTime();
//		double d1 = d0 - this.lastLookTime;
//		this.lastLookTime = d0;
//		if (this.isMouseGrabbed() && this.minecraft.isGameFocused()) {
//
//			/*
//			 * Change the velocities before calculating player look
//			 */
//
//			if (!SmoothFocusSettings.INSTANCE.mouseSensitivityModType.get().equals(MouseSensitivityModifier.NONE)) {
//				xVelocity /= SmoothFocusClient.sensitvityModifier * 4 + 1;
//				yVelocity /= SmoothFocusClient.sensitvityModifier * 4 + 1;
//			}
//
//			double d4 = this.minecraft.gameSettings.mouseSensitivity * (double) 0.6F + (double) 0.2F;
//			double d5 = d4 * d4 * d4 * 8.0D;
//			double d2;
//			double d3;
//			if (this.minecraft.gameSettings.smoothCamera || SmoothFocusClient.smoothCamera) {
//				double d6 = this.xSmoother.smooth(this.xVelocity * d5, d1 * d5);
//				double d7 = this.ySmoother.smooth(this.yVelocity * d5, d1 * d5);
//				d2 = d6;
//				d3 = d7;
//			} else {
//				this.xSmoother.reset();
//				this.ySmoother.reset();
//				d2 = this.xVelocity * d5;
//				d3 = this.yVelocity * d5;
//			}
//
//			this.xVelocity = 0.0D;
//			this.yVelocity = 0.0D;
//			int i = 1;
//			if (this.minecraft.gameSettings.invertMouse) {
//				i = -1;
//			}
//
//			this.minecraft.getTutorial().onMouseMove(d2, d3);
//			if (this.minecraft.player != null) {
//				this.minecraft.player.rotateTowards(d2, d3 * (double) i);
//			}
//
//		} else {
//			this.xVelocity = 0.0D;
//			this.yVelocity = 0.0D;
//		}
//		/*
//		 * So that it doesn't happen twice
//		 */
//		callback.cancel();
//	}

}
