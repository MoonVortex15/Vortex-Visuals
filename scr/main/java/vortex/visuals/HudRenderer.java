package vortex.visuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class HudRenderer {
    public static void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null || mc.options.hudHidden) return;

        int sw = ctx.getScaledWindowWidth();

        // 1. Название клиента (если включено)
        if (Modules.CLIENT_NAME.enabled) {
            String title = "Vortex Visuals v1.0";
            int w = mc.textRenderer.getWidth(title) + 40;
            int h = 24;
            int x = (sw - w) / 2;
            int y = 10;

            Ui.card(ctx, x, y, w, h, 8);
            Ui.roundRect(ctx, x + 8, y + 7, 10, 10, 3, Ui.ACCENT);
            Ui.text(ctx, mc, title, x + 24, y + 8, Ui.TEXT);
        }

        // 2. Список клавиш / Хоткеи (если включено)
        if (Modules.KEY_BINDS.enabled) {
            int x = sw - 130;
            int y = 40;
            int w = 120;
            int h = 70;

            Ui.card(ctx, x, y, w, h, 8);
            Ui.text(ctx, mc, "Hot Keys", x + 10, y + 8, Ui.TEXT);
            Ui.text(ctx, mc, "HUD: Right Shift", x + 10, y + 28, Ui.SUB);
            Ui.text(ctx, mc, "Client: Active", x + 10, y + 44, Ui.SUB);
        }
    }

    private HudRenderer() {}
}
