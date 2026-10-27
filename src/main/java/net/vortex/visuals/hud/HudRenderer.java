
package net.vortex.visuals.hud;

import net.vortex.visuals.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class HudRenderer {
    private static PlayerEntity lastTarget;
    private static long targetExpiresAt;
    private static final long TARGET_DURATION_MS = 15_000L;

    private static String draggingElement;
    private static int dragOffsetX, dragOffsetY;

    private static long lastPotionRefresh;
    private static List<StatusEffectInstance> cachedEffects = List.of();

    private static boolean positionsLoaded;
    private static final String CONFIG_FILE = "vortex-visuals-hud.properties";

    private HudRenderer() {}

    public static void setTarget(PlayerEntity target) {
        lastTarget = target;
        targetExpiresAt = System.currentTimeMillis() + TARGET_DURATION_MS;
    }

    public static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (!positionsLoaded) {
            loadPositions(mc);
            positionsLoaded = true;
        }

        if (mc.player == null || mc.world == null || mc.options.hudHidden) {
            return;
        }

        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();

        boolean editing = mc.currentScreen instanceof ChatScreen;

        double mx = mc.mouse.getX() * (double) sw / mc.getWindow().getWidth();
        double my = mc.mouse.getY() * (double) sh / mc.getWindow().getHeight();

        boolean pressed = GLFW.glfwGetMouseButton(
                mc.getWindow().getHandle(),
                GLFW.GLFW_MOUSE_BUTTON_LEFT
        ) == GLFW.GLFW_PRESS;

        // Save coordinates when the user releases the mouse.
        if ((!editing || !pressed) && draggingElement != null) {
            savePositions(mc);
            draggingElement = null;
        }

        if (editing && pressed && draggingElement != null) {
            move(draggingElement, (int) mx - dragOffsetX, (int) my - dragOffsetY);
        }

        if (Modules.watermarkEnabled) {
            renderWatermark(ctx, mc, editing, mx, my, pressed);
        }

        if (Modules.playerInfo) {
            renderTarget(ctx, mc, editing, mx, my, pressed);
        }

        if (Modules.potionsEnabled) {
            renderPotions(ctx, mc, editing, mx, my, pressed);
        }

        if (Modules.cooldownsEnabled) {
            renderCooldowns(ctx, mc, editing, mx, my, pressed);
        }

        if (Modules.inventoryHudEnabled) {
            renderInventory(ctx, mc, editing, mx, my, pressed);
        }
    }

    private static Path configPath(MinecraftClient mc) {
        return mc.runDirectory.toPath()
                .resolve("config")
                .resolve(CONFIG_FILE);
    }

    private static void loadPositions(MinecraftClient mc) {
        Path path = configPath(mc);

        if (!Files.exists(path)) {
            return;
        }

        Properties props = new Properties();

        try (InputStream in = Files.newInputStream(path)) {
            props.load(in);

            Modules.watermarkX = readInt(props, "watermarkX", Modules.watermarkX);
            Modules.watermarkY = readInt(props, "watermarkY", Modules.watermarkY);

            Modules.playerInfoX = readInt(props, "playerInfoX", Modules.playerInfoX);
            Modules.playerInfoY = readInt(props, "playerInfoY", Modules.playerInfoY);

            Modules.potionsX = readInt(props, "potionsX", Modules.potionsX);
            Modules.potionsY = readInt(props, "potionsY", Modules.potionsY);

            Modules.cooldownsX = readInt(props, "cooldownsX", Modules.cooldownsX);
            Modules.cooldownsY = readInt(props, "cooldownsY", Modules.cooldownsY);

            Modules.inventoryX = readInt(props, "inventoryX", Modules.inventoryX);
            Modules.inventoryY = readInt(props, "inventoryY", Modules.inventoryY);
        } catch (IOException e) {
            System.err.println("[Vortex Visuals] Could not load HUD positions: " + e.getMessage());
        }
    }

    private static int readInt(Properties props, String key, int fallback) {
        try {
            return Integer.parseInt(props.getProperty(key, String.valueOf(fallback)));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static void savePositions(MinecraftClient mc) {
        Path path = configPath(mc);
        Properties props = new Properties();

        props.setProperty("watermarkX", String.valueOf(Modules.watermarkX));
        props.setProperty("watermarkY", String.valueOf(Modules.watermarkY));

        props.setProperty("playerInfoX", String.valueOf(Modules.playerInfoX));
        props.setProperty("playerInfoY", String.valueOf(Modules.playerInfoY));

        props.setProperty("potionsX", String.valueOf(Modules.potionsX));
        props.setProperty("potionsY", String.valueOf(Modules.potionsY));

        props.setProperty("cooldownsX", String.valueOf(Modules.cooldownsX));
        props.setProperty("cooldownsY", String.valueOf(Modules.cooldownsY));

        props.setProperty("inventoryX", String.valueOf(Modules.inventoryX));
        props.setProperty("inventoryY", String.valueOf(Modules.inventoryY));

        try {
            Files.createDirectories(path.getParent());

            try (OutputStream out = Files.newOutputStream(path)) {
                props.store(out, "Vortex Visuals HUD positions");
            }
        } catch (IOException e) {
            System.err.println("[Vortex Visuals] Could not save HUD positions: " + e.getMessage());
        }
    }

    private static void renderWatermark(
            DrawContext c, MinecraftClient mc,
            boolean edit, double mx, double my, boolean pressed
    ) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        int ping = 0;

        if (mc.getNetworkHandler() != null
                && mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()) != null) {
            ping = mc.getNetworkHandler()
                    .getPlayerListEntry(mc.player.getUuid())
                    .getLatency();
        }

        String title = "VORTEX";
        String stats = "FPS " + mc.getCurrentFps() + "   PING " + ping + "ms   " + time;
        int w = mc.textRenderer.getWidth(title) + mc.textRenderer.getWidth(stats) + 34;
        int x = Modules.watermarkX;
        int y = Modules.watermarkY;

        card(c, x, y, w, 24, "watermark", edit, mx, my, pressed);
        c.drawText(mc.textRenderer, title, x + 10, y + 8, 0xFFFFFFFF, false);

        int dividerX = x + 10 + mc.textRenderer.getWidth(title) + 8;
        c.fill(dividerX, y + 6, dividerX + 1, y + 18, 0x66FFFFFF);

        c.drawText(mc.textRenderer, stats,
                x + mc.textRenderer.getWidth(title) + 24, y + 8,
                0xDFFFFFFF, false);
    }

    private static void renderTarget(
            DrawContext c, MinecraftClient mc,
            boolean edit, double mx, double my, boolean pressed
    ) {
        PlayerEntity target = null;

        if (lastTarget != null
                && System.currentTimeMillis() < targetExpiresAt
                && lastTarget.isAlive()
                && mc.world.getEntityById(lastTarget.getId()) == lastTarget) {
            target = lastTarget;
        } else if (System.currentTimeMillis() >= targetExpiresAt) {
            lastTarget = null;
        }

        if (target == null && !edit) {
            return;
        }

        int x = Modules.playerInfoX;
        int y = Modules.playerInfoY;

        card(c, x, y, 176, 66, "playerInfo", edit, mx, my, pressed);

        if (target == null) {
            c.drawText(mc.textRenderer, "TARGET HUD",
                    x + 10, y + 8, 0xFFFFFFFF, false);
            c.drawText(mc.textRenderer, "Hit a player to show HUD",
                    x + 10, y + 26, 0xBFFFFFFF, false);
            return;
        }

        c.fill(x + 7, y + 8, x + 29, y + 30, 0xFF39245F);
        c.drawItem(new ItemStack(Items.PLAYER_HEAD), x + 10, y + 10);

        String name = target.getName().getString();
        if (name.length() > 15) {
            name = name.substring(0, 14) + "…";
        }

        c.drawText(mc.textRenderer, name, x + 32, y + 8, 0xFFFFFFFF, false);
        c.drawText(mc.textRenderer,
                String.format(java.util.Locale.ROOT, "%.1f HP", target.getHealth()),
                x + 32, y + 22, 0xDFFFFFFF, false);

        int barX = x + 32;
        int barY = y + 36;
        int barW = 130;

        rounded(c, barX, barY, barW, 5, 2, 0x55333333);

        int hp = Math.max(0, Math.min(barW,
                (int) (barW * target.getHealth() / Math.max(1f, target.getMaxHealth()))));

        if (hp > 0) {
            rounded(c, barX, barY, hp, 5, 2, 0xFFB06CFF);
        }

        EquipmentSlot[] armorSlots = {
                EquipmentSlot.FEET,
                EquipmentSlot.LEGS,
                EquipmentSlot.CHEST,
                EquipmentSlot.HEAD
        };

        for (int i = 0; i < armorSlots.length; i++) {
            ItemStack stack = target.getEquippedStack(armorSlots[i]);
            if (!stack.isEmpty()) {
                c.drawItem(stack, x + 8 + i * 19, y + 43);
            }
        }

        ItemStack hand = target.getMainHandStack();
        if (!hand.isEmpty()) {
            c.drawItem(hand, x + 150, y + 43);
        }
    }

    private static void renderPotions(
            DrawContext c, MinecraftClient mc,
            boolean edit, double mx, double my, boolean pressed
    ) {
        if (System.currentTimeMillis() - lastPotionRefresh > 500) {
            cachedEffects = new ArrayList<>(mc.player.getStatusEffects());
            lastPotionRefresh = System.currentTimeMillis();
        }

        int x = Modules.potionsX;
        int y = Modules.potionsY;

        if (cachedEffects.isEmpty() && !edit) {
            return;
        }

        int rows = Math.min(5, cachedEffects.size());
        card(c, x, y, 158, 22 + Math.max(1, rows) * 13,
                "potions", edit, mx, my, pressed);

        c.drawText(mc.textRenderer, "EFFECTS", x + 9, y + 7, 0xFFFFFFFF, false);

        if (cachedEffects.isEmpty()) {
            c.drawText(mc.textRenderer, "No effects",
                    x + 9, y + 22, 0xBFFFFFFF, false);
            return;
        }

        for (int i = 0; i < rows; i++) {
            StatusEffectInstance effect = cachedEffects.get(i);
            String name = effect.getEffectType().value().getName().getString();
            String time = formatDuration(effect.getDuration());

            c.drawText(mc.textRenderer, name,
                    x + 9, y + 22 + i * 13, 0xEFFFFFFF, false);
            c.drawText(mc.textRenderer, time,
                    x + 112, y + 22 + i * 13, 0xBFFFFFFF, false);
        }
    }

    private static void renderCooldowns(
            DrawContext c, MinecraftClient mc,
            boolean edit, double mx, double my, boolean pressed
    ) {
        int x = Modules.cooldownsX;
        int y = Modules.cooldownsY;
        List<ItemStack> stacks = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty()
                    && mc.player.getItemCooldownManager().isCoolingDown(stack)) {
                stacks.add(stack);
            }
        }

        if (stacks.isEmpty() && !edit) {
            return;
        }

        card(c, x, y, Math.max(100, 12 + Math.min(5, stacks.size()) * 22),
                42, "cooldowns", edit, mx, my, pressed);

        c.drawText(mc.textRenderer, "COOLDOWNS",
                x + 8, y + 5, 0xFFFFFFFF, false);

        for (int i = 0; i < Math.min(5, stacks.size()); i++) {
            ItemStack stack = stacks.get(i);
            int sx = x + 7 + i * 22;

            c.drawItem(stack, sx, y + 18);

            float progress = mc.player.getItemCooldownManager()
                    .getCooldownProgress(stack, 0f);

            c.drawText(mc.textRenderer, Math.round(progress * 100) + "%",
                    sx, y + 31, 0xDFFFFFFF, false);
        }
    }

    // Inventory HUD: main inventory only, 3 rows x 9 columns.
    private static void renderInventory(
            DrawContext c, MinecraftClient mc,
            boolean edit, double mx, double my, boolean pressed
    ) {
        int x = Modules.inventoryX;
        int y = Modules.inventoryY;

        int cell = 16;
        int gap = 2;
        int step = cell + gap;
        int padding = 6;

        int width = padding * 2 + 9 * step - gap;
        int height = 72;

        card(c, x, y, width, height, "inventory", edit, mx, my, pressed);

        c.drawText(mc.textRenderer, "INVENTORY",
                x + padding, y + 5, 0xFFFFFFFF, false);

        int startX = x + padding;
        int startY = y + 18;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slot = 9 + row * 9 + col;
                int sx = startX + col * step;
                int sy = startY + row * step;

                drawInventorySlot(c, mc, slot, sx, sy, cell);
            }
        }
    }

    private static void drawInventorySlot(
            DrawContext c, MinecraftClient mc,
            int slot, int x, int y, int size
    ) {
        c.fill(x, y, x + size, y + size, 0xCC17121F);
        c.fill(x, y, x + size, y + 1, 0xFF7544D8);
        c.fill(x, y + size - 1, x + size, y + size, 0x663F2B60);
        c.fill(x, y, x + 1, y + size, 0x663F2B60);
        c.fill(x + size - 1, y, x + size, y + size, 0x663F2B60);

        ItemStack stack = mc.player.getInventory().getStack(slot);

        if (!stack.isEmpty()) {
            c.drawItem(stack, x, y);
            c.drawStackOverlay(mc.textRenderer, stack, x, y);
        }
    }

    private static String formatDuration(int ticks) {
        int s = ticks / 20;
        return String.format(java.util.Locale.ROOT, "%02d:%02d", s / 60, s % 60);
    }

    private static void card(
            DrawContext c, int x, int y, int w, int h, String id,
            boolean edit, double mx, double my, boolean pressed
    ) {
        rounded(c, x, y, w, h, 6, 0xB8141418);
        c.fill(x + 6, y, x + w - 6, y + 1, 0x55FFFFFF);

        if (edit && pressed && draggingElement == null
                && mx >= x && mx <= x + w && my >= y && my <= y + h) {
            draggingElement = id;
            dragOffsetX = (int) mx - x;
            dragOffsetY = (int) my - y;
        }
    }

    private static void rounded(
            DrawContext c, int x, int y, int w, int h, int r, int color
    ) {
        c.fill(x + r, y, x + w - r, y + h, color);

        for (int i = 0; i < r; i++) {
            int inset = r - (int) Math.floor(
                    Math.sqrt(r * r - (r - i) * (r - i)));

            c.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
            c.fill(x + inset, y + h - i - 1, x + w - inset, y + h - i, color);
        }
    }

    private static void move(String id, int x, int y) {
        switch (id) {
            case "watermark" -> {
                Modules.watermarkX = x;
                Modules.watermarkY = y;
            }
            case "playerInfo" -> {
                Modules.playerInfoX = x;
                Modules.playerInfoY = y;
            }
            case "potions" -> {
                Modules.potionsX = x;
                Modules.potionsY = y;
            }
            case "cooldowns" -> {
                Modules.cooldownsX = x;
                Modules.cooldownsY = y;
            }
            case "inventory" -> {
                Modules.inventoryX = x;
                Modules.inventoryY = y;
            }
        }
    }
}
