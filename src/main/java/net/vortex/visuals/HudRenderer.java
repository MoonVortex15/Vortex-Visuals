package net.vortex.visuals.hud;

import net.vortex.visuals.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HudRenderer {
    private static String draggingElement = null;
    private static int dragOffsetX = 0;
    private static int dragOffsetY = 0;

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        boolean inChat = client.currentScreen instanceof ChatScreen;

        double mouseX = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
        double mouseY = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();
        boolean isPressed = GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

        if (inChat) {
            if (!isPressed) {
                draggingElement = null;
            } else if (draggingElement != null) {
                if (draggingElement.equals("watermark")) { Modules.watermarkX = (int) mouseX - dragOffsetX; Modules.watermarkY = (int) mouseY - dragOffsetY; }
                else if (draggingElement.equals("playerInfo")) { Modules.playerInfoX = (int) mouseX - dragOffsetX; Modules.playerInfoY = (int) mouseY - dragOffsetY; }
                else if (draggingElement.equals("potions")) { Modules.potionsX = (int) mouseX - dragOffsetX; Modules.potionsY = (int) mouseY - dragOffsetY; }
                else if (draggingElement.equals("cooldowns")) { Modules.cooldownsX = (int) mouseX - dragOffsetX; Modules.cooldownsY = (int) mouseY - dragOffsetY; }
                else if (draggingElement.equals("keybinds")) { Modules.keybindsX = (int) mouseX - dragOffsetX; Modules.keybindsY = (int) mouseY - dragOffsetY; }
                else if (draggingElement.equals("keystrokes")) { Modules.keystrokesX = (int) mouseX - dragOffsetX; Modules.keystrokesY = (int) mouseY - dragOffsetY; }
                else if (draggingElement.equals("inventory")) { Modules.inventoryX = (int) mouseX - dragOffsetX; Modules.inventoryY = (int) mouseY - dragOffsetY; }
            }
        } else {
            draggingElement = null;
        }

        // 0. Watermark
        if (Modules.watermarkEnabled) {
            String timeStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String wmText = "Portex Visuals | FPS: " + client.getCurrentFps() + " | Ping: 0ms | Time: " + timeStr;
            int w = client.textRenderer.getWidth(wmText) + 16;
            int h = 22;
            drawCard(context, Modules.watermarkX, Modules.watermarkY, w, h, "watermark", inChat, mouseX, mouseY, isPressed);
            context.drawTextWithShadow(client.textRenderer, wmText, Modules.watermarkX + 8, Modules.watermarkY + 7, 0xFFFFFF);
        }

        // 1. Player Info
        if (Modules.playerInfo) {
            int w = 150, h = 50;
            drawCard(context, Modules.playerInfoX, Modules.playerInfoY, w, h, "playerInfo", inChat, mouseX, mouseY, isPressed);
            context.drawTextWithShadow(client.textRenderer, client.player.getName().getString(), Modules.playerInfoX + 10, Modules.playerInfoY + 8, 0xFFFFFF);
            context.drawTextWithShadow(client.textRenderer, "HP: " + (int)client.player.getHealth() + " (+16)", Modules.playerInfoX + 10, Modules.playerInfoY + 20, 0x888599);
            context.fill(Modules.playerInfoX + 10, Modules.playerInfoY + 34, Modules.playerInfoX + 140, Modules.playerInfoY + 38, 0xFF1E1B2C);
            context.fill(Modules.playerInfoX + 10, Modules.playerInfoY + 34, Modules.playerInfoX + 10 + (int)(130 * (client.player.getHealth() / 20.0f)), Modules.playerInfoY + 38, 0xFF7A3EE4);
        }

        // 2. Potions
        if (Modules.potionsEnabled) {
            int w = 150, h = 65;
            drawCard(context, Modules.potionsX, Modules.potionsY, w, h, "potions", inChat, mouseX, mouseY, isPressed);
            context.drawTextWithShadow(client.textRenderer, "Potions", Modules.potionsX + 10, Modules.potionsY + 8, 0xFFFFFF);
            context.drawTextWithShadow(client.textRenderer, "Регенерация 2  00:12", Modules.potionsX + 10, Modules.potionsY + 24, 0xAAAAAA);
            context.drawTextWithShadow(client.textRenderer, "Огнестойкость  04:52", Modules.potionsX + 10, Modules.potionsY + 38, 0xAAAAAA);
        }

        // 3. Cooldowns
        if (Modules.cooldownsEnabled) {
            int w = 150, h = 45;
            drawCard(context, Modules.cooldownsX, Modules.cooldownsY, w, h, "cooldowns", inChat, mouseX, mouseY, isPressed);
            context.drawTextWithShadow(client.textRenderer, "CoolDowns", Modules.cooldownsX + 10, Modules.cooldownsY + 8, 0xFFFFFF);
            context.drawTextWithShadow(client.textRenderer, "Козий рог        5.0s", Modules.cooldownsX + 10, Modules.cooldownsY + 24, 0xAAAAAA);
        }

        // 4. Keybinds
        if (Modules.keybindsEnabled) {
            int w = 150, h = 45;
            drawCard(context, Modules.keybindsX, Modules.keybindsY, w, h, "keybinds", inChat, mouseX, mouseY, isPressed);
            context.drawTextWithShadow(client.textRenderer, "Keybinds", Modules.keybindsX + 10, Modules.keybindsY + 8, 0xFFFFFF);
            context.drawTextWithShadow(client.textRenderer, "Hit Effect     [MB3]", Modules.keybindsX + 10, Modules.keybindsY + 24, 0xAAAAAA);
        }

        // 5. Keystrokes (WASD)
        if (Modules.keystrokesEnabled) {
            int kx = Modules.keystrokesX;
            int ky = Modules.keystrokesY;
            drawCard(context, kx, ky, 120, 95, "keystrokes", inChat, mouseX, mouseY, isPressed);
            drawKey(context, client, "W", kx + 44, ky + 10);
            drawKey(context, client, "A", kx + 12, ky + 42);
            drawKey(context, client, "S", kx + 44, ky + 42);
            drawKey(context, client, "D", kx + 76, ky + 42);
            drawKey(context, client, "LMB", kx + 12, ky + 72, 50, 16);
            drawKey(context, client, "RMB", kx + 64, ky + 72, 50, 16);
        }

        // 6. Inventory HUD
        if (Modules.inventoryHudEnabled) {
            int ix = Modules.inventoryX;
            int iy = Modules.inventoryY;
            drawCard(context, ix, iy, 180, 60, "inventory", inChat, mouseX, mouseY, isPressed);
            context.drawTextWithShadow(client.textRenderer, "Inventory Preview", ix + 10, iy + 8, 0xFFFFFF);
        }

        // 7. Армор-статус вокруг хотбара
        if (Modules.armorStatusEnabled) {
            int hotbarLeft = width / 2 - 91;
            int hotbarRight = width / 2 + 91;
            int hudY = height - 20;

            renderArmorItem(context, client, client.player.getEquippedStack(EquipmentSlot.FEET), hotbarLeft - 48, hudY);
            renderArmorItem(context, client, client.player.getEquippedStack(EquipmentSlot.LEGS), hotbarLeft - 24, hudY);
            renderArmorItem(context, client, client.player.getEquippedStack(EquipmentSlot.CHEST), hotbarRight + 8, hudY);
            renderArmorItem(context, client, client.player.getEquippedStack(EquipmentSlot.HEAD), hotbarRight + 32, hudY);
        }
    }

    private static void drawCard(DrawContext context, int x, int y, int w, int h, String id, boolean inChat, double mouseX, double mouseY, boolean isPressed) {
        // Плавная полупрозрачная тёмная подложка вместо убогих майнкрафтовских квадратов
        context.fill(x, y, x + w, y + h, 0xCC0E0D14);
        context.drawBorder(x, y, w, h, inChat ? 0xFF7A3EE4 : 0xFF242035);

        if (inChat && isPressed && draggingElement == null) {
            if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
                draggingElement = id;
                dragOffsetX = (int) mouseX - x;
                dragOffsetY = (int) mouseY - y;
            }
        }
    }

    private static void drawKey(DrawContext context, MinecraftClient client, String text, int x, int y) {
        drawKey(context, client, text, x, y, 28, 28);
    }

    private static void drawKey(DrawContext context, MinecraftClient client, String text, int x, int y, int w, int h) {
        context.fill(x, y, x + w, y + h, 0xCC191722);
        context.drawBorder(x, y, w, h, 0xFF2A263B);
        context.drawCenteredTextWithShadow(client.textRenderer, text, x + w / 2, y + (h - 8) / 2, 0xFFFFFF);
    }

    private static void renderArmorItem(DrawContext context, MinecraftClient client, ItemStack stack, int x, int y) {
        if (stack.isEmpty()) return;
        context.drawItem(stack, x, y);
        if (stack.isDamageable()) {
            int max = stack.getMaxDamage();
            int current = max - stack.getDamage();
            int percent = (int) (((float) current / max) * 100);
            int color = percent < 20 ? 0xFF5555 : (percent < 50 ? 0xFFFF55 : 0x55FF55);
            context.drawTextWithShadow(client.textRenderer, percent + "%", x + 16, y + 4, color);
        }
    }
}
