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
        matrices.translate(Modules.handOffsetX / 100.0, Modules.handOffsetY / 100.0, Modules.handOffsetZ / 100.0);
        float scale = Math.max(25, Math.min(200, Modules.handScalePercent)) / 100.0f;
        matrices.scale(scale, scale, scale);
        if (Modules.handRotation != 0) matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(Modules.handRotation));
        if (Modules.handAngle != 0) matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Modules.handAngle));
    }

    @Inject(method = "renderFirstPersonItem", at = @At("RETURN"))
    private void vortexVisuals$customHandPop(AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand, float swingProgress, ItemStack stack, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (Modules.customHand) matrices.pop();
    }
}
