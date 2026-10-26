package net.vortex.visuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class HudRenderer {
    private static String draggingElement = null;
    private static int dragOffsetX = 0;
    private static int dragOffsetY = 0;
    private static boolean wasMousePressed = false;

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        boolean inChat = client.currentScreen instanceof ChatScreen;

        double mouseX = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
        double mouseY = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();
        boolean isMousePressed = GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

        if (inChat) {
            if (!isMousePressed) draggingElement = null;
            if (draggingElement != null) {
                if (draggingElement.equals("playerInfo")) {
                    Modules.playerInfoX = (int) mouseX - dragOffsetX;
                    Modules.playerInfoY = (int) mouseY - dragOffsetY;
                } else if (draggingElement.equals("potions")) {
                    Modules.potionsX = (int) mouseX - dragOffsetX;
                    Modules.potionsY = (int) mouseY - dragOffsetY;
                } else if (draggingElement.equals("hotkeys")) {
                    Modules.hotkeysX = (int) mouseX - dragOffsetX;
                    Modules.hotkeysY = (int) mouseY - dragOffsetY;
                }
            }
        } else {
            draggingElement = null;
        }
        wasMousePressed = isMousePressed;

        // 1. Панель игрока (как на скриншоте: аватарка, ник, HP и фиолетовая полоска)
        if (Modules.playerInfo) {
            int px = Modules.playerInfoX;
            int py = Modules.playerInfoY;
            int w = 135, h = 50;
            drawHudCard(context, px, py, w, h, "playerInfo", inChat, mouseX, mouseY);

            context.drawTextWithShadow(client.textRenderer, client.player.getName().getString(), px + 12, py + 8, 0xFFFFFF);
            context.drawTextWithShadow(client.textRenderer, "HP / " + (int)client.player.getHealth(), px + 12, py + 22, 0x888599);
            
            // Фиолетовая полоска здоровья
            context.fill(px + 12, py + 36, px + 12 + 110, py + 40, 0xFF1E1B2C);
            context.fill(px + 12, py + 36, px + 12 + (int)(110 * (client.player.getHealth() / 20.0f)), py + 40, 0xFF7A3EE4);
        }

        // 2. Панель Зелий (Potions) как на референсе
        if (Modules.potionsEnabled) {
            int px = Modules.potionsX;
            int py = Modules.potionsY;
            int w = 135, h = 60;
            drawHudCard(context, px, py, w, h, "potions", inChat, mouseX, mouseY);
            context.drawTextWithShadow(client.textRenderer, "§bPotions", px + 10, py + 8, 0xFFFFFF);
            context.drawTextWithShadow(client.textRenderer, "Strength II          ∞", px + 10, py + 24, 0xAAAAAA);
            context.drawTextWithShadow(client.textRenderer, "Speed II           3:12", px + 10, py + 40, 0xAAAAAA);
        }

        // 3. Панель Хоткеев (Hot Keys) справа
        if (Modules.hotkeysEnabled) {
            int hx = Modules.hotkeysX < 0 ? width + Modules.hotkeysX : Modules.hotkeysX;
            int hy = Modules.hotkeysY;
            int w = 140, h = 85;
            drawHudCard(context, hx, hy, w, h, "hotkeys", inChat, mouseX, mouseY);
            context.drawTextWithShadow(client.textRenderer, "⌨ Hot Keys", hx + 10, hy + 8, 0xFFFFFF);
            context.drawTextWithShadow(client.textRenderer, "AutoFish          V", hx + 10, hy + 26, 0xAAAAAA);
            context.drawTextWithShadow(client.textRenderer, "Brightness       H", hx + 10, hy + 42, 0xAAAAAA);
            context.drawTextWithShadow(client.textRenderer, "HUD                 M", hx + 10, hy + 58, 0xAAAAAA);
            context.drawTextWithShadow(client.textRenderer, "Music Bar        Q", hx + 10, hy + 74, 0xAAAAAA);
        }

        // 4. Аккуратный Армор-Худ вокруг хотбара снизу
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

    private static void drawHudCard(DrawContext context, int x, int y, int w, int h, String id, boolean inChat, double mouseX, double mouseY) {
        context.fill(x, y, x + w, y + h, 0xEE0B0B10);
        context.drawBorder(x, y, w, h, inChat ? 0xFF7A3EE4 : 0xFF1C1A29);

        if (inChat && !wasMousePressed) {
            MinecraftClient client = MinecraftClient.getInstance();
            boolean isPressed = GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            if (isPressed && mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
                draggingElement = id;
                dragOffsetX = (int) mouseX - x;
                dragOffsetY = (int) mouseY - y;
            }
        }
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
