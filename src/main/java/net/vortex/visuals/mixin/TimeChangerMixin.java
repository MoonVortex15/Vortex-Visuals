package net.vortex.visuals.mixin;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import net.vortex.visuals.Modules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Client-side visual time override for Minecraft 1.21.4. */
@Mixin(World.class)
public abstract class TimeChangerMixin {
    @Inject(method = "getTimeOfDay()J", at = @At("HEAD"), cancellable = true)
    private void vortexVisuals$overrideTimeOfDay(CallbackInfoReturnable<Long> cir) {
        if ((Object) this instanceof ClientWorld && Modules.timeChanger) {
            int time = Math.floorMod(Modules.customTimeOfDay, 24000);
            cir.setReturnValue((long) time);
        }
    }
}
