package net.vortex.visuals.hud;

import net.vortex.visuals.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Lightweight rounded, translucent HUD for Vortex Visuals. */
public final class HudRenderer {
    private static String draggingElement;
    private static int dragOffsetX, dragOffsetY;
    private static long lastPotionRefresh;
    private static List<StatusEffectInstance> cachedEffects = List.of();

    private HudRenderer() {}

    public static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null || mc.options.hudHidden) return;
        int sw = mc.getWindow().getScaledWidth(), sh = mc.getWindow().getScaledHeight();
        boolean editing = mc.currentScreen instanceof ChatScreen;
        double mx = mc.mouse.getX() * (double) sw / mc.getWindow().getWidth();
        double my = mc.mouse.getY() * (double) sh / mc.getWindow().getHeight();
        boolean pressed = GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        if (!editing || !pressed) draggingElement = null;
        if (editing && pressed && draggingElement != null) move(draggingElement, (int) mx - dragOffsetX, (int) my - dragOffsetY);

        if (Modules.watermarkEnabled) renderWatermark(ctx, mc, editing, mx, my, pressed);
        if (Modules.playerInfo) renderTarget(ctx, mc, sw, sh, editing, mx, my, pressed);
        if (Modules.potionsEnabled) renderPotions(ctx, mc, sw, sh, editing, mx, my, pressed);
        if (Modules.cooldownsEnabled) renderCooldowns(ctx, mc, sw, sh, editing, mx, my, pressed);
    }

    private static void renderWatermark(DrawContext c, MinecraftClient mc, boolean edit, double mx, double my, boolean pressed) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        int ping = 0;
        if (mc.getNetworkHandler() != null && mc.player != null && mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()) != null)
            ping = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency();
        String title = "VORTEX";
        String stats = "FPS " + mc.getCurrentFps() + "   PING " + ping + "ms   " + time;
        int w = mc.textRenderer.getWidth(title) + mc.textRenderer.getWidth(stats) + 34;
        int x = Modules.watermarkX, y = Modules.watermarkY;
        card(c, x, y, w, 24, "watermark", edit, mx, my, pressed);
        c.drawText(mc.textRenderer, title, x + 10, y + 8, 0xFFFFFFFF, false);
        c.fill(x + 10 + mc.textRenderer.getWidth(title) + 8, y + 6, x + 11 + mc.textRenderer.getWidth(title) + 8, y + 18, 0x66FFFFFF);
        c.drawText(mc.textRenderer, stats, x + mc.textRenderer.getWidth(title) + 24, y + 8, 0xDFFFFFFF, false);
    }

    private static void renderTarget(DrawContext c, MinecraftClient mc, int sw, int sh, boolean edit, double mx, double my, boolean pressed) {
        LivingEntity target = null;
        HitResult hit = mc.crosshairTarget;
        if (hit instanceof EntityHitResult ehr && ehr.getEntity() instanceof LivingEntity living && living != mc.player && living.isAlive()) target = living;
        if (target == null && !edit) return;
        int x = Modules.playerInfoX, y = Modules.playerInfoY, w = 174, h = 62;
        card(c, x, y, w, h, "playerInfo", edit, mx, my, pressed);
        if (target == null) {
            c.drawText(mc.textRenderer, "TARGET HUD", x + 10, y + 8, 0xFFFFFFFF, false);
            c.drawText(mc.textRenderer, "Aim at a player", x + 10, y + 25, 0xBFFFFFFF, false);
            return;
        }
        // The icon is a lightweight player-head item; no per-frame texture allocation.
        c.drawItem(new ItemStack(Items.PLAYER_HEAD), x + 8, y + 9);
        String name = target.getName().getString();
        if (name.length() > 15) name = name.substring(0, 14) + "…";
        c.drawText(mc.textRenderer, name, x + 32, y + 8, 0xFFFFFFFF, false);
        c.drawText(mc.textRenderer, String.format(java.util.Locale.ROOT, "%.1f HP", target.getHealth()), x + 32, y + 21, 0xDFFFFFFF, false);
        int barX = x + 32, barY = y + 35, barW = 132;
        rounded(c, barX, barY, barW, 5, 2, 0x55333333);
        float max = Math.max(1.0f, target.getMaxHealth());
        rounded(c, barX, barY, Math.max(1, Math.min(barW, (int)(barW * target.getHealth() / max))), 5, 2, 0xDFFFFFFF);
        int ix = x + 8;
        EquipmentSlot[] armorSlots = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};
        for (int slot = 0; slot < armorSlots.length; slot++) {
            ItemStack armor = target.getEquippedStack(armorSlots[slot]);
            if (!armor.isEmpty()) c.drawItem(armor, ix + slot * 19, y + 42);
        }
        ItemStack held = target.getMainHandStack();
        if (!held.isEmpty()) c.drawItem(held, x + w - 25, y + 40);
    }

    private static void renderPotions(DrawContext c, MinecraftClient mc, int sw, int sh, boolean edit, double mx, double my, boolean pressed) {
        if (System.currentTimeMillis() - lastPotionRefresh > 500) {
            cachedEffects = new ArrayList<>(mc.player.getStatusEffects());
            lastPotionRefresh = System.currentTimeMillis();
        }
        int x = Modules.potionsX, y = Modules.potionsY;
        if (cachedEffects.isEmpty() && !edit) return;
        int rows = Math.min(5, cachedEffects.size());
        int h = 22 + Math.max(1, rows) * 13;
        card(c, x, y, 158, h, "potions", edit, mx, my, pressed);
        c.drawText(mc.textRenderer, "EFFECTS", x + 9, y + 7, 0xFFFFFFFF, false);
        if (cachedEffects.isEmpty()) {
            c.drawText(mc.textRenderer, "No active effects", x + 9, y + 21, 0xBFFFFFFF, false);
            return;
        }
        for (int i = 0; i < rows; i++) {
            StatusEffectInstance e = cachedEffects.get(i);
            String label = e.getEffectType().value().getName().getString();
            String duration = formatDuration(e.getDuration());
            int maxName = 105;
            while (mc.textRenderer.getWidth(label) > maxName && label.length() > 3) label = label.substring(0, label.length() - 2) + "…";
            c.drawText(mc.textRenderer, label, x + 9, y + 21 + i * 13, 0xEFFFFFFF, false);
            c.drawText(mc.textRenderer, duration, x + 112, y + 21 + i * 13, 0xBFFFFFFF, false);
        }
    }

    private static void renderCooldowns(DrawContext c, MinecraftClient mc, int sw, int sh, boolean edit, double mx, double my, boolean pressed) {
        int x = Modules.cooldownsX, y = Modules.cooldownsY;
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && mc.player.getItemCooldownManager().isCoolingDown(stack.getItem())) stacks.add(stack);
        }
        if (stacks.isEmpty() && !edit) return;
        int w = Math.max(100, 12 + Math.min(5, stacks.size()) * 22);
        card(c, x, y, w, 42, "cooldowns", edit, mx, my, pressed);
        c.drawText(mc.textRenderer, "COOLDOWNS", x + 8, y + 5, 0xFFFFFFFF, false);
        if (stacks.isEmpty()) {
            c.drawText(mc.textRenderer, "No cooldowns", x + 8, y + 21, 0xBFFFFFFF, false);
            return;
        }
        for (int i = 0; i < Math.min(5, stacks.size()); i++) {
            ItemStack stack = stacks.get(i);
            int sx = x + 7 + i * 22;
            c.drawItem(stack, sx, y + 18);
            float progress = mc.player.getItemCooldownManager().getCooldownProgress(stack.getItem(), 0.0f);
            c.drawText(mc.textRenderer, Math.round(progress * 100) + "%", sx, y + 31, 0xDFFFFFFF, false);
        }
    }

    private static String formatDuration(int ticks) {
        if (ticks < 0) return "∞";
        int seconds = ticks / 20;
        return String.format(java.util.Locale.ROOT, "%02d:%02d", seconds / 60, seconds % 60);
    }

    private static void card(DrawContext c, int x, int y, int w, int h, String id, boolean edit, double mx, double my, boolean pressed) {
        rounded(c, x, y, w, h, 6, 0xB8141418);
        // subtle light edge, intentionally low contrast so text stays readable
        c.fill(x + 6, y, x + w - 6, y + 1, 0x55FFFFFF);
        if (edit && pressed && draggingElement == null && mx >= x && mx <= x + w && my >= y && my <= y + h) {
            draggingElement = id;
            dragOffsetX = (int) mx - x;
            dragOffsetY = (int) my - y;
        }
    }

    private static void rounded(DrawContext c, int x, int y, int w, int h, int r, int color) {
        if (w <= 0 || h <= 0) return;
        r = Math.min(r, Math.min(w / 2, h / 2));
        c.fill(x + r, y, x + w - r, y + h, color);
        for (int row = 0; row < r; row++) {
            int inset = r - (int)Math.floor(Math.sqrt((double)r * r - (double)(r - row) * (r - row)));
            c.fill(x + inset, y + row, x + w - inset, y + row + 1, color);
            c.fill(x + inset, y + h - row - 1, x + w - inset, y + h - row, color);
        }
    }

    private static void move(String id, int x, int y) {
        switch (id) {
            case "watermark" -> { Modules.watermarkX = x; Modules.watermarkY = y; }
            case "playerInfo" -> { Modules.playerInfoX = x; Modules.playerInfoY = y; }
            case "potions" -> { Modules.potionsX = x; Modules.potionsY = y; }
            case "cooldowns" -> { Modules.cooldownsX = x; Modules.cooldownsY = y; }
        }
    }
}
