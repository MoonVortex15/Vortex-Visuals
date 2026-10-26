package vortex.visuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class HudRenderer {
    public static void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null || mc.options.hudHidden) return;

        int sw = ctx.getScaledWindowWidth();
        String title = "Vortex Visuals v1.0";
        int w = mc.textRenderer.getWidth(title) + 40;
        int h = 24;
        int x = (sw - w) / 2;
        int y = 10;

        Ui.card(ctx, x, y, w, h, 8);
        Ui.roundRect(ctx, x + 8, y + 7, 10, 10, 3, Ui.ACCENT);
        Ui.text(ctx, mc, title, x + 24, y + 8, Ui.TEXT);
    }

    private HudRenderer() {}
}
