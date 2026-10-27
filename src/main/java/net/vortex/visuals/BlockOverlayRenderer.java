
package net.vortex.visuals;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public final class BlockOverlayRenderer {
    private BlockOverlayRenderer() {}

    public static void register() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(
                BlockOverlayRenderer::render
        );
    }

    private static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!Modules.blockOverlayEnabled || client.world == null
                || client.player == null
                || !(client.crosshairTarget instanceof BlockHitResult hit)
                || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        MatrixStack matrices = context.matrixStack();
        if (matrices == null || context.consumers() == null) return;

        BlockPos pos = hit.getBlockPos();
        Vec3d camera = context.camera().getPos();
        Box box = new Box(pos).expand(0.002)
                .offset(-camera.x, -camera.y, -camera.z);

        float fr = clamp(Modules.blockOverlayFillRed) / 255f;
        float fg = clamp(Modules.blockOverlayFillGreen) / 255f;
        float fb = clamp(Modules.blockOverlayFillBlue) / 255f;
        float fa = clamp(Modules.blockOverlayAlpha) / 255f;

        float or = clamp(Modules.blockOverlayOutlineRed) / 255f;
        float og = clamp(Modules.blockOverlayOutlineGreen) / 255f;
        float ob = clamp(Modules.blockOverlayOutlineBlue) / 255f;

        matrices.push();
        try {
            Matrix4f matrix = matrices.peek().getPositionMatrix();

            if (fa > 0) {
                VertexConsumer fill = context.consumers()
                        .getBuffer(RenderLayer.getDebugFilledBox());
                drawFaces(fill, matrix, box, fr, fg, fb, fa);
            }

            VertexConsumer lines = context.consumers()
                    .getBuffer(RenderLayer.getLines());
            drawEdges(lines, matrix, box, or, og, ob, 1f);
        } finally {
            matrices.pop();
        }
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static void vertex(VertexConsumer out, Matrix4f matrix,
                               double x, double y, double z,
                               float r, float g, float b, float a) {
        out.vertex(matrix, (float)x, (float)y, (float)z)
                .color(r, g, b, a);
    }

    private static void quad(VertexConsumer out, Matrix4f m,
                             double x1, double y1, double z1,
                             double x2, double y2, double z2,
                             double x3, double y3, double z3,
                             double x4, double y4, double z4,
                             float r, float g, float b, float a) {
        vertex(out, m, x1,y1,z1,r,g,b,a);
        vertex(out, m, x2,y2,z2,r,g,b,a);
        vertex(out, m, x3,y3,z3,r,g,b,a);
        vertex(out, m, x4,y4,z4,r,g,b,a);
    }

    private static void drawFaces(VertexConsumer out, Matrix4f m, Box b,
                                  float r, float g, float bl, float a) {
        quad(out,m,b.minX,b.minY,b.minZ,b.maxX,b.minY,b.minZ,
                b.maxX,b.minY,b.maxZ,b.minX,b.minY,b.maxZ,r,g,bl,a);
        quad(out,m,b.minX,b.maxY,b.minZ,b.minX,b.maxY,b.maxZ,
                b.maxX,b.maxY,b.maxZ,b.maxX,b.maxY,b.minZ,r,g,bl,a);
        quad(out,m,b.minX,b.minY,b.minZ,b.minX,b.minY,b.maxZ,
                b.minX,b.maxY,b.maxZ,b.minX,b.maxY,b.minZ,r,g,bl,a);
        quad(out,m,b.maxX,b.minY,b.minZ,b.maxX,b.maxY,b.minZ,
                b.maxX,b.maxY,b.maxZ,b.maxX,b.minY,b.maxZ,r,g,bl,a);
        quad(out,m,b.minX,b.minY,b.minZ,b.minX,b.maxY,b.minZ,
                b.maxX,b.maxY,b.minZ,b.maxX,b.minY,b.minZ,r,g,bl,a);
        quad(out,m,b.minX,b.minY,b.maxZ,b.maxX,b.minY,b.maxZ,
                b.maxX,b.maxY,b.maxZ,b.minX,b.maxY,b.maxZ,r,g,bl,a);
    }

    private static void edge(VertexConsumer out, Matrix4f m,
                             double x1,double y1,double z1,
                             double x2,double y2,double z2,
                             float r,float g,float b,float a) {
        out.vertex(m,(float)x1,(float)y1,(float)z1).color(r,g,b,a).normal(0,1,0);
        out.vertex(m,(float)x2,(float)y2,(float)z2).color(r,g,b,a).normal(0,1,0);
    }

    private static void drawEdges(VertexConsumer out, Matrix4f m, Box b,
                                  float r,float g,float bl,float a) {
        double x0=b.minX, x1=b.maxX, y0=b.minY, y1=b.maxY,
                z0=b.minZ, z1=b.maxZ;
        edge(out,m,x0,y0,z0,x1,y0,z0,r,g,bl,a);
        edge(out,m,x0,y0,z1,x1,y0,z1,r,g,bl,a);
        edge(out,m,x0,y1,z0,x1,y1,z0,r,g,bl,a);
        edge(out,m,x0,y1,z1,x1,y1,z1,r,g,bl,a);
        edge(out,m,x0,y0,z0,x0,y1,z0,r,g,bl,a);
        edge(out,m,x1,y0,z0,x1,y1,z0,r,g,bl,a);
        edge(out,m,x0,y0,z1,x0,y1,z1,r,g,bl,a);
        edge(out,m,x1,y0,z1,x1,y1,z1,r,g,bl,a);
        edge(out,m,x0,y0,z0,x0,y0,z1,r,g,bl,a);
        edge(out,m,x1,y0,z0,x1,y0,z1,r,g,bl,a);
        edge(out,m,x0,y1,z0,x0,y1,z1,r,g,bl,a);
        edge(out,m,x1,y1,z0,x1,y1,z1,r,g,bl,a);
    }
}
