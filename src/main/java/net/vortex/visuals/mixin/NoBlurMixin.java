package net.vortex.visuals.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Disables Minecraft's standard screen blur globally.
 * Target: Minecraft 1.21.4 (Yarn mappings).
 */
@Mixin(GameRenderer.class)
public abstract class NoBlurMixin {
    @Inject(method = "renderBlur()V", at = @At("HEAD"), cancellable = true)
    private void vortexVisuals$disableScreenBlur(CallbackInfo ci) {
        ci.cancel();
    }
}
