
package net.vortex.visuals;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public final class TargetEspRenderer {
    private TargetEspRenderer() {}

    public static void register() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(
                TargetEspRenderer::render
        );
    }

    private static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!Modules.targetEsp
                || client.world == null
                || client.player == null
                || client.crosshairTarget == null
                || client.crosshairTarget.getType() != HitResult.Type.ENTITY
                || !(client.crosshairTarget instanceof EntityHitResult hit)) {
            return;
        }

        Entity target = hit.getEntity();
        if (target == client.player || !target.isAlive()) {
            return;
        }

        MatrixStack matrices = context.matrixStack();
        if (matrices == null || context.consumers() == null) {
            return;
        }

        Vec3d camera = context.camera().getPos();
        Box box = target.getBoundingBox().expand(0.04)
                .offset(-camera.x, -camera.y, -camera.z);

        VertexConsumer lines = context.consumers()
                .getBuffer(RenderLayer.getLines());

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float r = 0.57f;
        float g = 0.24f;
        float b = 1.0f;
        float a = 1.0f;

        double x0 = box.minX, x1 = box.maxX;
        double y0 = box.minY, y1 = box.maxY;
        double z0 = box.minZ, z1 = box.maxZ;

        // Нижняя и верхняя грани
        edge(lines, matrix, x0,y0,z0, x1,y0,z0, r,g,b,a);
        edge(lines, matrix, x0,y0,z1, x1,y0,z1, r,g,b,a);
        edge(lines, matrix, x0,y1,z0, x1,y1,z0, r,g,b,a);
        edge(lines, matrix, x0,y1,z1, x1,y1,z1, r,g,b,a);

        edge(lines, matrix, x0,y0,z0, x0,y0,z1, r,g,b,a);
        edge(lines, matrix, x1,y0,z0, x1,y0,z1, r,g,b,a);
        edge(lines, matrix, x0,y1,z0, x0,y1,z1, r,g,b,a);
        edge(lines, matrix, x1,y1,z0, x1,y1,z1, r,g,b,a);

        // Вертикальные рёбра
        edge(lines, matrix, x0,y0,z0, x0,y1,z0, r,g,b,a);
        edge(lines, matrix, x1,y0,z0, x1,y1,z0, r,g,b,a);
        edge(lines, matrix, x0,y0,z1, x0,y1,z1, r,g,b,a);
        edge(lines, matrix, x1,y0,z1, x1,y1,z1, r,g,b,a);
    }

    private static void edge(
            VertexConsumer out,
            Matrix4f matrix,
            double x1, double y1, double z1,
            double x2, double y2, double z2,
            float r, float g, float b, float a
    ) {
        out.vertex(matrix, (float)x1, (float)y1, (float)z1)
                .color(r, g, b, a)
                .normal(0, 1, 0);

        out.vertex(matrix, (float)x2, (float)y2, (float)z2)
                .color(r, g, b, a)
                .normal(0, 1, 0);
    }
}
