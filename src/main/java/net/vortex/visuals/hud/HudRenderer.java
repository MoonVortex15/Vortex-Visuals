package net.vortex.visuals.hud;

import net.vortex.visuals.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class HudRenderer {

    private static String draggingElement;
    private static int dragOffsetX, dragOffsetY;

    private static PlayerEntity lastTarget;
    private static long targetExpiresAt;
    private static final long TARGET_DURATION_MS = 15_000L;

    public static void setTarget(PlayerEntity target) {
        lastTarget = target;
        targetExpiresAt = System.currentTimeMillis() + TARGET_DURATION_MS;
    }

    private static void renderCustomCrosshair(DrawContext ctx, int screenWidth, int screenHeight) {
        int cx = screenWidth / 2;
        int cy = screenHeight / 2;
        int thickness = Math.max(1, Math.min(8, Modules.crosshairThickness));
        int gap = Math.max(0, Math.min(20, Modules.crosshairGap));
        int color = 0xFF000000 | (Math.max(0, Math.min(255, Modules.crosshairRed)) << 16)
                | (Math.max(0, Math.min(255, Modules.crosshairGreen)) << 8)
                | Math.max(0, Math.min(255, Modules.crosshairBlue));
        int arm = 5;
        ctx.fill(cx - gap - arm, cy - thickness / 2, cx - gap, cy - thickness / 2 + thickness, color);
        ctx.fill(cx + gap, cy - thickness / 2, cx + gap + arm, cy - thickness / 2 + thickness, color);
        ctx.fill(cx - thickness / 2, cy - gap - arm, cx - thickness / 2 + thickness, cy - gap, color);
        ctx.fill(cx - thickness / 2, cy + gap, cx - thickness / 2 + thickness, cy + gap + arm, color);
        if (Modules.crosshairCenterDot) {
            ctx.fill(cx - 1, cy - 1, cx + 2, cy + 2, color);
        }
    }

    /** Saves current HUD positions and module toggles to the config directory. */
    public static void saveConfig() {
        MinecraftClient mc = MinecraftClient.getInstance();
        Path path = mc.runDirectory.toPath()
                .resolve("config")
                .resolve("vortex-visuals-hud.properties");
        Properties props = new Properties();

        props.setProperty("watermarkX", String.valueOf(Modules.watermarkX));
        props.setProperty("watermarkY", String.valueOf(Modules.watermarkY));
        props.setProperty("playerInfoX", String.valueOf(Modules.playerInfoX));
        props.setProperty("playerInfoY", String.valueOf(Modules.playerInfoY));
        props.setProperty("potionsX", String.valueOf(Modules.potionsX));
        props.setProperty("potionsY", String.valueOf(Modules.potionsY));
        props.setProperty("cooldownsX", String.valueOf(Modules.cooldownsX));
        props.setProperty("cooldownsY", String.valueOf(Modules.cooldownsY));
        props.setProperty("keybindsX", String.valueOf(Modules.keybindsX));
        props.setProperty("keybindsY", String.valueOf(Modules.keybindsY));
        props.setProperty("inventoryX", String.valueOf(Modules.inventoryX));
        props.setProperty("inventoryY", String.valueOf(Modules.inventoryY));
        props.setProperty("armorX", String.valueOf(Modules.armorX));
        props.setProperty("armorY", String.valueOf(Modules.armorY));

        props.setProperty("watermarkEnabled", String.valueOf(Modules.watermarkEnabled));
        props.setProperty("playerInfo", String.valueOf(Modules.playerInfo));
        props.setProperty("armorStatusEnabled", String.valueOf(Modules.armorStatusEnabled));
        props.setProperty("potionsEnabled", String.valueOf(Modules.potionsEnabled));
        props.setProperty("cooldownsEnabled", String.valueOf(Modules.cooldownsEnabled));
        props.setProperty("keybindsEnabled", String.valueOf(Modules.keybindsEnabled));
        props.setProperty("inventoryHudEnabled", String.valueOf(Modules.inventoryHudEnabled));

        props.setProperty("deathEffect", String.valueOf(Modules.deathEffect));
        props.setProperty("killEffect", String.valueOf(Modules.killEffect));
        props.setProperty("hitSound", String.valueOf(Modules.hitSound));
        props.setProperty("deathSound", String.valueOf(Modules.deathSound));
        props.setProperty("targetEsp", String.valueOf(Modules.targetEsp));
        props.setProperty("autoShift", String.valueOf(Modules.autoShift));
        props.setProperty("chinaHit", String.valueOf(Modules.chinaHit));
        props.setProperty("customHand", String.valueOf(Modules.customHand));
        props.setProperty("fullBright", String.valueOf(Modules.fullBright));
        props.setProperty("hitColor", String.valueOf(Modules.hitColor));
        props.setProperty("hitboxCustomizer", String.valueOf(Modules.hitboxCustomizer));
        props.setProperty("jumpCircles", String.valueOf(Modules.jumpCircles));
        props.setProperty("particles", String.valueOf(Modules.particles));
        props.setProperty("renderTweaks", String.valueOf(Modules.renderTweaks));
        props.setProperty("customCrosshairEnabled", String.valueOf(Modules.customCrosshairEnabled));
        props.setProperty("crosshairThickness", String.valueOf(Modules.crosshairThickness));
        props.setProperty("crosshairGap", String.valueOf(Modules.crosshairGap));
        props.setProperty("crosshairRed", String.valueOf(Modules.crosshairRed));
        props.setProperty("crosshairGreen", String.valueOf(Modules.crosshairGreen));
        props.setProperty("crosshairBlue", String.valueOf(Modules.crosshairBlue));
        props.setProperty("crosshairCenterDot", String.valueOf(Modules.crosshairCenterDot));
        props.setProperty("blockOverlayEnabled", String.valueOf(Modules.blockOverlayEnabled));
        props.setProperty("blockOverlayFillRed", String.valueOf(Modules.blockOverlayFillRed));
        props.setProperty("blockOverlayFillGreen", String.valueOf(Modules.blockOverlayFillGreen));
        props.setProperty("blockOverlayFillBlue", String.valueOf(Modules.blockOverlayFillBlue));
        props.setProperty("blockOverlayOutlineRed", String.valueOf(Modules.blockOverlayOutlineRed));
        props.setProperty("blockOverlayOutlineGreen", String.valueOf(Modules.blockOverlayOutlineGreen));
        props.setProperty("blockOverlayOutlineBlue", String.valueOf(Modules.blockOverlayOutlineBlue));
        props.setProperty("blockOverlayAlpha", String.valueOf(Modules.blockOverlayAlpha));
        props.setProperty("blockOverlayLineWidth", String.valueOf(Modules.blockOverlayLineWidth));

        try {
            Files.createDirectories(path.getParent());
            try (OutputStream out = Files.newOutputStream(path)) {
                props.store(out, "Vortex Visuals configuration");
            }
        } catch (IOException e) {
            System.err.println("[Vortex Visuals] Could not save config: " + e.getMessage());
        }
    }

    private static long lastPotionRefresh;
    private static List<StatusEffectInstance> cachedEffects = List.of();

    private HudRenderer() {}

    public static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player == null || mc.world == null || mc.options.hudHidden)
            return;

        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();

        boolean editing = mc.currentScreen instanceof ChatScreen;

        double mx = mc.mouse.getX() * (double) sw / mc.getWindow().getWidth();
        double my = mc.mouse.getY() * (double) sh / mc.getWindow().getHeight();

        boolean pressed = GLFW.glfwGetMouseButton(
                mc.getWindow().getHandle(),
                GLFW.GLFW_MOUSE_BUTTON_LEFT
        ) == GLFW.GLFW_PRESS;

        if (!editing || !pressed)
            draggingElement = null;

        if (editing && pressed && draggingElement != null) {
            move(draggingElement, (int) mx - dragOffsetX, (int) my - dragOffsetY);
        }

        if (Modules.watermarkEnabled)
            renderWatermark(ctx, mc, editing, mx, my, pressed);

        if (Modules.playerInfo)
            renderTarget(ctx, mc, editing, mx, my, pressed);

        if (Modules.potionsEnabled)
            renderPotions(ctx, mc, editing, mx, my, pressed);

        if (Modules.cooldownsEnabled)
            renderCooldowns(ctx, mc, editing, mx, my, pressed);

        if (Modules.armorStatusEnabled)
            renderArmor(ctx, mc, editing, mx, my, pressed);

        if (Modules.keybindsEnabled)
            renderKeybinds(ctx, mc, editing, mx, my, pressed);


        if (Modules.inventoryHudEnabled)
            renderInventory(ctx, mc, editing, mx, my, pressed);

        if (Modules.customCrosshairEnabled)
            renderCustomCrosshair(ctx, sw, sh);
    }

    // ---------------- WATERMARK ----------------

    private static void renderWatermark(
            DrawContext c,
            MinecraftClient mc,
            boolean edit,
            double mx,
            double my,
            boolean pressed
    ) {

        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        int ping = 0;

        if (mc.getNetworkHandler() != null
                && mc.player != null
                && mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()) != null) {

            ping = mc.getNetworkHandler()
                    .getPlayerListEntry(mc.player.getUuid())
                    .getLatency();
        }

        String title = "VORTEX";
        String stats = "FPS " + mc.getCurrentFps()
                + "   PING " + ping + "ms   " + time;

        int w = mc.textRenderer.getWidth(title)
                + mc.textRenderer.getWidth(stats)
                + 34;

        int x = Modules.watermarkX;
        int y = Modules.watermarkY;

        card(c, x, y, w, 24,
                "watermark",
                edit, mx, my, pressed);

        c.drawText(mc.textRenderer,
                title,
                x + 10, y + 8,
                0xFFFFFFFF,
                false);

        int dividerX = x + 10 + mc.textRenderer.getWidth(title) + 8;

        c.fill(dividerX,
                y + 6,
                dividerX + 1,
                y + 18,
                0x66FFFFFF);

        c.drawText(mc.textRenderer,
                stats,
                x + mc.textRenderer.getWidth(title) + 24,
                y + 8,
                0xDFFFFFFF,
                false);
    }

    // ---------------- TARGET HUD ----------------

    private static void renderTarget(
            DrawContext c,
            MinecraftClient mc,
            boolean edit,
            double mx,
            double my,
            boolean pressed
    ) {

        LivingEntity target = null;
        if (lastTarget != null
                && System.currentTimeMillis() < targetExpiresAt
                && lastTarget.isAlive()
                && mc.world != null
                && mc.world.getEntityById(lastTarget.getId()) == lastTarget) {
            target = lastTarget;
        } else if (System.currentTimeMillis() >= targetExpiresAt) {
            lastTarget = null;
        }

        // In chat/edit mode show a preview; during normal play only show after hitting a player.
        if (target == null && !edit)
            return;

        int x = Modules.playerInfoX;
        int y = Modules.playerInfoY;

        card(c, x, y, 176, 66,
                "playerInfo",
                edit, mx, my, pressed);

        if (target == null) {
            c.drawText(mc.textRenderer,
                    "TARGET HUD",
                    x + 10,
                    y + 8,
                    0xFFFFFFFF,
                    false);

            c.drawText(mc.textRenderer,
                    "Hit a player to show HUD",
                    x + 10,
                    y + 26,
                    0xBFFFFFFF,
                    false);
            return;
        }

        c.drawItem(new ItemStack(Items.PLAYER_HEAD),
                x + 8,
                y + 10);

        String name = target.getName().getString();

        if (name.length() > 15)
            name = name.substring(0, 14) + "…";

        c.drawText(mc.textRenderer,
                name,
                x + 32,
                y + 8,
                0xFFFFFFFF,
                false);

        c.drawText(mc.textRenderer,
                String.format(java.util.Locale.ROOT,
                        "%.1f HP",
                        target.getHealth()),
                x + 32,
                y + 22,
                0xDFFFFFFF,
                false);

        int barX = x + 32;
        int barY = y + 36;
        int barW = 130;

        rounded(c, barX, barY,
                barW, 5,
                2,
                0x55333333);

        int hp = Math.max(
                1,
                (int) (barW * target.getHealth() / Math.max(1f, target.getMaxHealth()))
        );

        rounded(c,
                barX,
                barY,
                hp,
                5,
                2,
                0xFFFFFFFF);

        EquipmentSlot[] armorSlots = {
                EquipmentSlot.FEET,
                EquipmentSlot.LEGS,
                EquipmentSlot.CHEST,
                EquipmentSlot.HEAD
        };

        int ix = x + 8;

        for (int i = 0; i < armorSlots.length; i++) {
            ItemStack stack = target.getEquippedStack(armorSlots[i]);

            if (!stack.isEmpty())
                c.drawItem(stack,
                        ix + i * 19,
                        y + 43);
        }

        ItemStack hand = target.getMainHandStack();

        if (!hand.isEmpty())
            c.drawItem(hand,
                    x + 150,
                    y + 43);
    }

    // ---------------- POTIONS ----------------

    private static void renderPotions(
            DrawContext c,
            MinecraftClient mc,
            boolean edit,
            double mx,
            double my,
            boolean pressed
    ) {

        if (System.currentTimeMillis() - lastPotionRefresh > 500) {
            cachedEffects = new ArrayList<>(mc.player.getStatusEffects());
            lastPotionRefresh = System.currentTimeMillis();
        }

        int x = Modules.potionsX;
        int y = Modules.potionsY;

        if (cachedEffects.isEmpty() && !edit)
            return;

        int rows = Math.min(5, cachedEffects.size());

        card(c,
                x,
                y,
                158,
                22 + Math.max(1, rows) * 13,
                "potions",
                edit, mx, my, pressed);

        c.drawText(mc.textRenderer,
                "EFFECTS",
                x + 9,
                y + 7,
                0xFFFFFFFF,
                false);

        if (cachedEffects.isEmpty()) {
            c.drawText(mc.textRenderer,
                    "No effects",
                    x + 9,
                    y + 22,
                    0xBFFFFFFF,
                    false);
            return;
        }

        for (int i = 0; i < rows; i++) {

            StatusEffectInstance effect = cachedEffects.get(i);

            String name = effect.getEffectType()
                    .value()
                    .getName()
                    .getString();

            String time = formatDuration(effect.getDuration());

            c.drawText(mc.textRenderer,
                    name,
                    x + 9,
                    y + 22 + i * 13,
                    0xEFFFFFFF,
                    false);

            c.drawText(mc.textRenderer,
                    time,
                    x + 112,
                    y + 22 + i * 13,
                    0xBFFFFFFF,
                    false);
        }
    }

    // ---------------- COOLDOWNS ----------------

    private static void renderCooldowns(
            DrawContext c,
            MinecraftClient mc,
            boolean edit,
            double mx,
            double my,
            boolean pressed
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

        if (stacks.isEmpty() && !edit)
            return;

        card(c,
                x,
                y,
                Math.max(100, 12 + Math.min(5, stacks.size()) * 22),
                42,
                "cooldowns",
                edit, mx, my, pressed);

        c.drawText(mc.textRenderer,
                "COOLDOWNS",
                x + 8,
                y + 5,
                0xFFFFFFFF,
                false);

        for (int i = 0; i < Math.min(5, stacks.size()); i++) {

            ItemStack stack = stacks.get(i);

            int sx = x + 7 + i * 22;

            c.drawItem(stack,
                    sx,
                    y + 18);

            float progress = mc.player.getItemCooldownManager()
                    .getCooldownProgress(stack, 0f);

            c.drawText(mc.textRenderer,
                    Math.round(progress * 100) + "%",
                    sx,
                    y + 31,
                    0xDFFFFFFF,
                    false);
        }
    }


    // ---------------- ARMOR HUD ----------------
    private static void renderArmor(DrawContext c, MinecraftClient mc, boolean edit,
            double mx, double my, boolean pressed) {
        int x = Modules.armorX, y = Modules.armorY;
        card(c, x, y, 94, 43, "armor", edit, mx, my, pressed);
        c.drawText(mc.textRenderer, "ARMOR", x + 7, y + 4, 0xFFFFFFFF, false);
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = mc.player.getEquippedStack(slots[i]);
            int sx = x + 7 + i * 20;
            c.fill(sx, y + 16, sx + 18, y + 34, 0x99140F22);
            if (!stack.isEmpty()) {
                c.drawItem(stack, sx + 1, y + 16);
                c.drawStackOverlay(mc.textRenderer, stack, sx + 1, y + 16);
                int max = stack.getMaxDamage();
                if (max > 0) {
                    int remaining = max - stack.getDamage();
                    int color = remaining > max * .5 ? 0xFF55FF55 : remaining > max * .2 ? 0xFFFFCC33 : 0xFFFF4444;
                    c.fill(sx + 2, y + 35, sx + 16, y + 37, 0xFF30283A);
                    c.fill(sx + 2, y + 35, sx + 2 + Math.max(0, 14 * remaining / max), y + 37, color);
                }
            }
        }
    }

    // ---------------- KEYBINDS HUD ----------------
    private static void renderKeybinds(DrawContext c, MinecraftClient mc, boolean edit,
            double mx, double my, boolean pressed) {
        int x = Modules.keybindsX, y = Modules.keybindsY;
        List<String> active = new ArrayList<>();
        if (mc.options.forwardKey.isPressed()) active.add("Forward");
        if (mc.options.backKey.isPressed()) active.add("Back");
        if (mc.options.leftKey.isPressed()) active.add("Left");
        if (mc.options.rightKey.isPressed()) active.add("Right");
        if (mc.options.jumpKey.isPressed()) active.add("Jump");
        if (mc.options.sprintKey.isPressed()) active.add("Sprint");
        if (mc.options.sneakKey.isPressed()) active.add("Sneak");
        if (active.isEmpty() && !edit) return;
        int rows = Math.max(1, active.size());
        card(c, x, y, 126, 20 + rows * 12, "keybinds", edit, mx, my, pressed);
        c.drawText(mc.textRenderer, "KEYBINDS", x + 7, y + 4, 0xFFFFFFFF, false);
        if (active.isEmpty()) c.drawText(mc.textRenderer, "No active keys", x + 7, y + 17, 0xBFFFFFFF, false);
        else for (int i = 0; i < active.size(); i++) c.drawText(mc.textRenderer, active.get(i), x + 7, y + 17 + i * 12, 0xEFFFFFFF, false);
    }

    // ---------------- INVENTORY HUD ----------------

    private static void renderInventory(
            DrawContext c,
            MinecraftClient mc,
            boolean edit,
            double mx,
            double my,
            boolean pressed
    ) {

        int x = Modules.inventoryX;
        int y = Modules.inventoryY;

        int cell = 18;
        int gap = 2;
        int padding = 6;

        int width = 176;
        int height = 78;

        card(c,
                x,
                y,
                width,
                height,
                "inventory",
                edit, mx, my, pressed);

        c.drawText(mc.textRenderer,
                "INVENTORY",
                x + 8,
                y + 5,
                0xFFFFFFFF,
                false);

        int startX = x + padding;
        int startY = y + 18;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {

                int slot = 9 + row * 9 + col;

                int sx = startX + col * (cell + gap);
                int sy = startY + row * (cell + gap);

                c.fill(
                        sx,
                        sy,
                        sx + cell,
                        sy + cell,
                        0x99140F22
                );

                c.fill(
                        sx,
                        sy,
                        sx + cell,
                        sy + 1,
                        0xFF5D2EFF
                );

                ItemStack stack = mc.player
                        .getInventory()
                        .getStack(slot);

                if (!stack.isEmpty()) {
                    c.drawItem(stack,
                            sx + 1,
                            sy + 1);

                    c.drawStackOverlay(
                            mc.textRenderer,
                            stack,
                            sx + 1,
                            sy + 1
                    );
                }
            }
        }
    }

    // ---------------- HELPERS ----------------

    private static String formatDuration(int ticks) {
        int s = ticks / 20;

        return String.format(
                java.util.Locale.ROOT,
                "%02d:%02d",
                s / 60,
                s % 60
        );
    }

    private static void card(
            DrawContext c,
            int x,
            int y,
            int w,
            int h,
            String id,
            boolean edit,
            double mx,
            double my,
            boolean pressed
    ) {

        rounded(c,
                x,
                y,
                w,
                h,
                6,
                0xB8141418);

        c.fill(
                x + 6,
                y,
                x + w - 6,
                y + 1,
                0x55FFFFFF);

        if (edit
                && pressed
                && draggingElement == null
                && mx >= x
                && mx <= x + w
                && my >= y
                && my <= y + h) {

            draggingElement = id;
            dragOffsetX = (int) mx - x;
            dragOffsetY = (int) my - y;
        }
    }

    private static void rounded(
            DrawContext c,
            int x,
            int y,
            int w,
            int h,
            int r,
            int color
    ) {

        c.fill(
                x + r,
                y,
                x + w - r,
                y + h,
                color);

        for (int i = 0; i < r; i++) {

            int inset = r - (int) Math.floor(
                    Math.sqrt(r * r - (r - i) * (r - i))
            );

            c.fill(
                    x + inset,
                    y + i,
                    x + w - inset,
                    y + i + 1,
                    color);

            c.fill(
                    x + inset,
                    y + h - i - 1,
                    x + w - inset,
                    y + h - i,
                    color);
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
            case "armor" -> {
                Modules.armorX = x;
                Modules.armorY = y;
            }
            case "keybinds" -> {
                Modules.keybindsX = x;
                Modules.keybindsY = y;
            }
        }
    }
}
