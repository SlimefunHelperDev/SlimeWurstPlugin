/*
 * Camera behavior adapted from Wurst 7.
 * Copyright (c) 2014-2026 Wurst-Imperium and contributors.
 * Licensed under the GNU General Public License, version 3. See LICENSE.txt.
 */
package me.matl114.wurstintegration.mixin;

import me.matl114.wurstintegration.modules.CameraDistanceModule;
import me.matl114.wurstintegration.modules.CameraNoClipModule;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @ModifyVariable(method = "clipToSpace(F)F", at = @At("HEAD"), argsOnly = true)
    private float changeCameraDistance(float desiredDistance) {
        CameraDistanceModule module = CameraDistanceModule.INSTANCE;
        return module != null && module.enable.get() ? (float) module.getDistance() : desiredDistance;
    }

    @Inject(method = "clipToSpace(F)F", at = @At("HEAD"), cancellable = true)
    private void disableCameraCollision(float desiredDistance, CallbackInfoReturnable<Float> cir) {
        CameraNoClipModule module = CameraNoClipModule.INSTANCE;
        if (module != null && module.enable.get()) {
            cir.setReturnValue(desiredDistance);
        }
    }
}
