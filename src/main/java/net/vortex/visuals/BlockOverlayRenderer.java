
package net.vortex.visuals;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class BlockOverlayRenderer {

    private BlockOverlayRenderer() {
    }

    public static void register() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(
                BlockOverlayRenderer::render
        );
    }

    private static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!Modules.blockOverlayEnabled
                || client.world == null
                || client.player == null
                || !(client.crosshairTarget instanceof BlockHitResult hit)
                || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockPos pos = hit.getBlockPos();
        Vec3d camera = context.camera().getPos();

        Box box = new Box(pos).expand(0.002)
                .offset(-camera.x, -camera.y, -camera.z);

        MatrixStack matrices = context.matrixStack();
        if (matrices == null) {
            return;
        }

        int fillRed = clamp(Modules.blockOverlayFillRed);
        int fillGreen = clamp(Modules.blockOverlayFillGreen);
        int fillBlue = clamp(Modules.blockOverlayFillBlue);
        int alpha = clamp(Modules.blockOverlayAlpha);

        int outlineRed = clamp(Modules.blockOverlayOutlineRed);
        int outlineGreen = clamp(Modules.blockOverlayOutlineGreen);
        int outlineBlue = clamp(Modules.blockOverlayOutlineBlue);

        matrices.push();

        try {
            if (context.consumers() == null) {
                return;
            }

            if (alpha > 0) {
                VertexConsumer fill = context.consumers()
                        .getBuffer(RenderLayer.getDebugFilledBox());

                WorldRenderer.drawBox(
                        matrices,
                        fill,
                        box,
                        fillRed / 255.0f,
                        fillGreen / 255.0f,
                        fillBlue / 255.0f,
                        alpha / 255.0f
                );
            }

            VertexConsumer outline = context.consumers()
                    .getBuffer(RenderLayer.getLines());

            WorldRenderer.drawBox(
                    matrices,
                    outline,
                    box,
                    outlineRed / 255.0f,
                    outlineGreen / 255.0f,
                    outlineBlue / 255.0f,
                    1.0f
            );
        } finally {
            matrices.pop();
        }
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
