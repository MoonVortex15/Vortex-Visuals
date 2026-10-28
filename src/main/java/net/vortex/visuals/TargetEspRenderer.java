
package net.vortex.visuals;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
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
                || context.matrixStack() == null
                || context.consumers() == null) {
            return;
        }

        Vec3d camera = context.camera().getPos();
        Matrix4f matrix =
                context.matrixStack().peek().getPositionMatrix();

        VertexConsumer lines = context.consumers()
                .getBuffer(RenderLayer.getLines());

        for (PlayerEntity target : client.world.getPlayers()) {
            if (target == client.player
                    || !target.isAlive()
                    || target.isSpectator()
                    || client.player.squaredDistanceTo(target)
                    > (double) Modules.targetEspRange * Modules.targetEspRange) {
                continue;
            }

            Box box = target.getBoundingBox()
                    .expand(Math.max(0.0, Math.min(0.20, Modules.targetEspExpand / 100.0)))
                    .offset(-camera.x, -camera.y, -camera.z);

            drawBox(lines, matrix, box);
        }
    }

    private static void drawBox(
            VertexConsumer out,
            Matrix4f matrix,
            Box box
    ) {
        double x0 = box.minX;
        double y0 = box.minY;
        double z0 = box.minZ;
        double x1 = box.maxX;
        double y1 = box.maxY;
        double z1 = box.maxZ;

        // Нижняя грань
        edge(out, matrix, x0,y0,z0, x1,y0,z0);
        edge(out, matrix, x1,y0,z0, x1,y0,z1);
        edge(out, matrix, x1,y0,z1, x0,y0,z1);
        edge(out, matrix, x0,y0,z1, x0,y0,z0);

        // Верхняя грань
        edge(out, matrix, x0,y1,z0, x1,y1,z0);
        edge(out, matrix, x1,y1,z0, x1,y1,z1);
        edge(out, matrix, x1,y1,z1, x0,y1,z1);
        edge(out, matrix, x0,y1,z1, x0,y1,z0);

        // Вертикальные рёбра
        edge(out, matrix, x0,y0,z0, x0,y1,z0);
        edge(out, matrix, x1,y0,z0, x1,y1,z0);
        edge(out, matrix, x0,y0,z1, x0,y1,z1);
        edge(out, matrix, x1,y0,z1, x1,y1,z1);
    }

    private static void edge(
            VertexConsumer out,
            Matrix4f matrix,
            double x1, double y1, double z1,
            double x2, double y2, double z2
    ) {
        float r = Math.max(0, Math.min(255, Modules.targetEspRed)) / 255.0f;
        float g = Math.max(0, Math.min(255, Modules.targetEspGreen)) / 255.0f;
        float b = Math.max(0, Math.min(255, Modules.targetEspBlue)) / 255.0f;
        out.vertex(matrix, (float) x1, (float) y1, (float) z1)
                .color(r, g, b, 1.0f)
                .normal(0, 1, 0);

        out.vertex(matrix, (float) x2, (float) y2, (float) z2)
                .color(r, g, b, 1.0f)
                .normal(0, 1, 0);
    }
}
