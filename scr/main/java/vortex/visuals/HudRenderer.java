package vortex.visuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class HudRenderer {
    public static void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden) return;

        int sw = ctx.getScaledWindowWidth();

        // 1. Плашка клиента
        if (Modules.CLIENT_NAME.enabled) {
            String title = "Vortex Visuals";
            int w = 130;
            int h = 20;
            int x = (sw - w) / 2;
            int y = 8;

            Ui.card(ctx, x, y, w, h);
            Ui.fill(ctx, x + 6, y + 6, 8, 8, Ui.ACCENT);
            Ui.text(ctx, mc, title, x + 20, y + 6, Ui.TEXT);
        }

        // 2. Блок FPS
        if (Modules.KEY_BINDS.enabled) {
            int x = sw - 110;
            int y = 10;
            int w = 100;
            int h = 30;

            Ui.card(ctx, x, y, w, h);
            Ui.text(ctx, mc, "Vortex HUD", x + 8, y + 6, Ui.TEXT);
            Ui.text(ctx, mc, "FPS: " + MinecraftClient.getCurrentFps(), x + 8, y + 18, Ui.SUB);
        }
    }

    private HudRenderer() {}
}
