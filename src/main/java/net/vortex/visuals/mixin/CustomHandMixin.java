package net.vortex.visuals.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
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
    private void vortexVisuals$customHandPush(AbstractClientPlayerEntity player, float tickProgress, float pitch,
            Hand hand, float swingProgress, ItemStack stack, float equipProgress, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!Modules.customHand) return;
        boolean off = hand == Hand.OFF_HAND;
        int x = off ? Modules.offHandOffsetX : Modules.handOffsetX;
        int y = off ? Modules.offHandOffsetY : Modules.handOffsetY;
        int z = off ? Modules.offHandOffsetZ : Modules.handOffsetZ;
        int scaleValue = off ? Modules.offHandScalePercent : Modules.handScalePercent;
        int rotX = off ? Modules.offHandRotationX : Modules.handAngle;
        int rotY = off ? Modules.offHandRotationY : Modules.handRotationY;
        int rotZ = off ? Modules.offHandRotationZ : Modules.handRotation;
        matrices.push();
        matrices.translate(x / 100.0, y / 100.0, z / 100.0);
        float scale = Math.max(25, Math.min(200, scaleValue)) / 100.0f;
        matrices.scale(scale, scale, scale);
        // Three independent Euler-style rotations; each hand has its own values.
        if (rotX != 0) matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotX));
        if (rotY != 0) matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotY));
        if (rotZ != 0) matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotZ));
    }

    @Inject(method = "renderFirstPersonItem", at = @At("RETURN"))
    private void vortexVisuals$customHandPop(AbstractClientPlayerEntity player, float tickProgress, float pitch,
            Hand hand, float swingProgress, ItemStack stack, float equipProgress, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (Modules.customHand) matrices.pop();
    }
}
