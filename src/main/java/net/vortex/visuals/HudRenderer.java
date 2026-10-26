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
            if (!isMousePressed) {
                draggingElement = null;
            }

            if (draggingElement != null) {
                if (draggingElement.equals("logo")) {
                    Modules.logoX = (int) mouseX - dragOffsetX;
                    Modules.logoY = (int) mouseY - dragOffsetY;
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

        // 1. Логотип и FPS (Перетаскиваемый)
        if (Modules.logoEnabled || Modules.fpsEnabled) {
            int w = 130, h = Modules.logoEnabled && Modules.fpsEnabled ? 40 : 24;
            drawModernPanel(context, Modules.logoX, Modules.logoY, w, h, "logo", inChat, mouseX, mouseY);

            int ty = Modules.logoY + 6;
            if (Modules.logoEnabled) {
                context.drawTextWithShadow(client.textRenderer, "§bVortex Visuals", Modules.logoX + 10, ty, 0xFFFFFF);
                ty += 14;
            }
            if (Modules.fpsEnabled) {
                context.drawTextWithShadow(client.textRenderer, "FPS: " + client.getCurrentFps(), Modules.logoX + 10, ty, 0x55FF55);
            }
        }

        // 2. Блок игрока (Перетаскиваемый)
        if (Modules.playerInfo) {
            int w = 120, h = 45;
            drawModernPanel(context, Modules.playerInfoX, Modules.playerInfoY, w, h, "playerInfo", inChat, mouseX, mouseY);
            context.drawTextWithShadow(client.textRenderer, client.player.getName().getString(), Modules.playerInfoX + 10, Modules.playerInfoY + 8, 0xFFFFFF);
            context.drawTextWithShadow(client.textRenderer, "HP: " + (int)client.player.getHealth(), Modules.playerInfoX + 10, Modules.playerInfoY + 24, 0xFF5555);
        }

        // 3. Хоткеи (Перетаскиваемые)
        if (Modules.hotkeysEnabled) {
            int w = 110, h = 65;
            drawModernPanel(context, Modules.hotkeysX, Modules.hotkeysY, w, h, "hotkeys", inChat, mouseX, mouseY);
            context.drawTextWithShadow(client.textRenderer, "§bHotkeys", Modules.hotkeysX + 10, Modules.hotkeysY + 8, 0xFFFFFF);
            context.drawTextWithShadow(client.textRenderer, "Menu: RShift", Modules.hotkeysX + 10, Modules.hotkeysY + 24, 0xAAAAAA);
            context.drawTextWithShadow(client.textRenderer, "Brightness: H", Modules.hotkeysX + 10, Modules.hotkeysY + 38, 0xAAAAAA);
        }

        // 4. Армор-худ (намертво зафиксирован вокруг хотбара)
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

    private static void drawModernPanel(DrawContext context, int x, int y, int w, int h, String id, boolean inChat, double mouseX, double mouseY) {
        context.fill(x, y, x + w, y + h, 0xDD121216);
        context.drawBorder(x, y, w, h, inChat ? 0xFF00FFCC : 0xFF2A2A35);

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
