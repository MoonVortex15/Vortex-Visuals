package vortex.visuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class Ui {
    public static final int CARD_BG = 0xE612131F;
    public static final int BORDER  = 0x55FFFFFF;
    public static final int TEXT    = 0xFFFFFFFF;
    public static final int SUB     = 0xFFAAAAAA;
    public static final int ACCENT  = 0xFF8A75FF;

    public static void fill(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + h, color);
    }

    public static void card(DrawContext ctx, int x, int y, int w, int h) {
        fill(ctx, x - 1, y - 1, w + 2, h + 2, BORDER);
        fill(ctx, x, y, w, h, CARD_BG);
    }

    public static void text(DrawContext ctx, MinecraftClient mc, String s, int x, int y, int color) {
        ctx.drawText(mc.textRenderer, s, x, y, color, false);
    }

    private Ui() {}
}
