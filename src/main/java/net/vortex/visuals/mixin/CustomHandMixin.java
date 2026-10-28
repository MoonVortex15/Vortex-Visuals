package net.vortex.visuals.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import net.vortex.visuals.Modules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class CustomHandMixin {
    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    private void vortexVisuals$customHandPush(AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand, float swingProgress, ItemStack stack, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!Modules.customHand) return;
        matrices.push();
        boolean main = hand == Hand.MAIN_HAND;
        int ox = main ? Modules.mainHandOffsetX : Modules.offHandOffsetX;
        int oy = main ? Modules.mainHandOffsetY : Modules.offHandOffsetY;
        int oz = main ? Modules.mainHandOffsetZ : Modules.offHandOffsetZ;
        int scalePercent = main ? Modules.mainHandScalePercent : Modules.offHandScalePercent;
        int rx = main ? Modules.mainHandRotationX : Modules.offHandRotationX;
        int ry = main ? Modules.mainHandRotationY : Modules.offHandRotationY;
        int rz = main ? Modules.mainHandRotationZ : Modules.offHandRotationZ;
        matrices.translate(ox / 100.0, oy / 100.0, oz / 100.0);
        float scale = Math.max(25, Math.min(200, scalePercent)) / 100.0f;
        matrices.scale(scale, scale, scale);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rx));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(ry));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rz));
    }

    @Inject(method = "renderFirstPersonItem", at = @At("RETURN"))
    private void vortexVisuals$customHandPop(AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand, float swingProgress, ItemStack stack, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (Modules.customHand) matrices.pop();
    }
}
