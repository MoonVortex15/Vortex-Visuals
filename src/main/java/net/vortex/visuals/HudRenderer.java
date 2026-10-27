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

        // Логика перетаскивания элементов HUD мышкой в чате
        if (inChat) {
            if (!isMousePressed) {
                draggingElement = null;
            }

            if (draggingElement != null) {
                if (draggingElement.equals("musicBar")) {
                    Modules.musicBarX = (int) mouseX - dragOffsetX;
                    Modules.musicBarY = (int) mouseY - dragOffsetY;
                } else if (draggingElement.equals("potions")) {
                    Modules.potionsX = (int) mouseX - dragOffsetX;
                    Modules.potionsY = (int) mouseY - dragOffsetY;
                } else if (draggingElement.equals("playerInfo")) {
                    Modules.playerInfoX = (int) mouseX - dragOffsetX;
                    Modules.playerInfoY = (int) mouseY - dragOffsetY;
                } else if (draggingElement.equals("hotkeys")) {
                    Modules.hotkeysX = (int) mouseX - dragOffsetX;
                    Modules.hotkeysY = (int) mouseY - dragOffsetY;
                }
            }
        } else {
            draggingElement = null;
        }
        wasMousePressed = isMousePressed;

        // 1. Музыкальный плеер (как на референсе сверху слева)
        if (Modules.musicBarEnabled) {
            int w = 180, h = 65;
            drawHudCard(context, Modules.musicBarX, Modules.musicBarY, w, h, "musicBar", inChat, mouseX, mouseY);
            context.drawTextWithShadow(client.textRenderer, "§fAlien", Modules.musicBarX + 45, Modules.musicBarY + 10, 0xFFFFFF);
            context.drawTextWithShadow(client.textRenderer, "§8@ilymeady", Modules.musicBarX + 45, Modules.musicBarY + 22, 0x888599);
            // Полоса прогресса песни
            context.fill(Modules.musicBarX + 12, Modules.musicBarY + 45, Modules.musicBarX + 168, Modules.musicBarY + 48, 0xFF221F30);
            context.fill(Modules.musicBarX + 12, Modules.musicBarY + 45, Modules.musicBarX + 90, Modules.musicBarY + 48, 0xFF7A3EE4);
        }

        // 2. Панель Зелий (Potions)
        if (Modules.potionsEnabled) {
            int w = 145, h = 55;
            drawHudCard(context, Modules.potionsX, Modules.potionsY, w, h, "potions", inChat, mouseX, mouseY);
            context.drawTextWithShadow(client.textRenderer, "§bPotions", Modules.potionsX + 10, Modules.potionsY + 8, 0xFFFFFF);
            context.drawTextWithShadow(client.textRenderer, "Strength II          ∞", Modules.potionsX + 10, Modules.potionsY + 24, 0xAAAAAA);
            context.drawTextWithShadow(client.textRenderer, "Speed II           3:12", Modules.potionsX + 10, Modules.potionsY + 38, 0xAAAAAA);
        }

        // 3. Блок игрока (Player Info со здоровьем и фиолетовой полоской)
        if (Modules.playerInfo) {
            int w = 145, h = 50;
            drawHudCard(context, Modules.playerInfoX, Modules.playerInfoY, w, h, "playerInfo", inChat, mouseX, mouseY);
            context.drawTextWithShadow(client.textRenderer, client.player.getName().getString(), Modules.playerInfoX + 12, Modules.playerInfoY + 8, 0xFFFFFF);
            context.drawTextWithShadow(client.textRenderer, "HP / " + (int)client.player.getHealth(), Modules.playerInfoX + 12, Modules.playerInfoY + 22, 0x888599);
            
            context.fill(Modules.playerInfoX + 12, Modules.playerInfoY + 36, Modules.playerInfoX + 133, Modules.playerInfoY + 40, 0xFF1E1B2C);
            context.fill(Modules.playerInfoX + 12, Modules.playerInfoY + 36, Modules.playerInfoX + 12 + (int)(121 * (client.player.getHealth() / 20.0f)), Modules.playerInfoY + 40, 0xFF7A3EE4);
        }

        // 4. Панель Хоткеев (выключена по умолчанию, бинды не навязаны)
        if (Modules.hotkeysEnabled) {
            int hx = Modules.hotkeysX < 0 ? width + Modules.hotkeysX : Modules.hotkeysX;
            int hy = Modules.hotkeysY;
            int w = 140, h = 70;
            drawHudCard(context, hx, hy, w, h, "hotkeys", inChat, mouseX, mouseY);
            context.drawTextWithShadow(client.textRenderer, "⌨ Hot Keys", hx + 10, hy + 8, 0xFFFFFF);
            context.drawTextWithShadow(client.textRenderer, "FullBright       [none]", hx + 10, hy + 26, 0xAAAAAA);
            context.drawTextWithShadow(client.textRenderer, "Visuals Menu   [none]", hx + 10, hy + 42, 0xAAAAAA);
        }

        // 5. Армор-худ (вокруг хотбара снизу)
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
        context.fill(x, y, x + w, y + h, 0xEE0C0B12);
        context.drawBorder(x, y, w, h, inChat ? 0xFF7A3EE4 : 0xFF242035); // В чате подсвечиваются рамки для перетаскивания

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
