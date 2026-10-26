package vortex.visuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class Ui {
    public static final int CARD_BG = 0xF2151622;
    public static final int BORDER = 0x25FFFFFF;
    public static final int TEXT = 0xFFFFFFFF;
    public static final int ACCENT = 0xFF9B8AFF;

    public static void roundRect(DrawContext ctx, int x, int y, int w, int h, int r, int c) {
        if (w <= 0 || h <= 0) return;
        r = Math.min(r, Math.min(w, h) / 2);
        if (r <= 0) { ctx.fill(x, y, x + w, y + h, c); return; }
        ctx.fill(x, y + r, x + w, y + h - r, c);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = (int) Math.round(r - Math.sqrt(Math.max(0, r * r - dy * dy)));
            ctx.fill(x + inset, y + i, x + w - inset, y + i + 1, c);
            ctx.fill(x + inset, y + h - 1 - i, x + w - inset, y + h - i, c);
        }
    }

    public static void card(DrawContext ctx, int x, int y, int w, int h, int r) {
        roundRect(ctx, x - 1, y - 1, w + 2, h + 2, r + 1, BORDER);
        roundRect(ctx, x, y, w, h, r, CARD_BG);
    }

    public static void text(DrawContext ctx, MinecraftClient mc, String s, int x, int y, int c) {
        ctx.drawText(mc.textRenderer, s, x, y, c, false);
    }

    private Ui() {}
}
