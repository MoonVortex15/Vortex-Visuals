package net.vortex.visuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class HudRenderer {

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        // 1. Панель Логотипа и FPS (сверху слева в красивом стиле)
        if (Modules.logoEnabled || Modules.fpsEnabled) {
            int panelW = 120;
            int panelH = Modules.logoEnabled && Modules.fpsEnabled ? 38 : 22;
            drawFloatingPanel(context, 12, 12, panelW, panelH);

            int textY = 18;
            if (Modules.logoEnabled) {
                context.drawTextWithShadow(client.textRenderer, "§bVortex Visuals", 20, textY, 0xFFFFFF);
                textY += 14;
            }
            if (Modules.fpsEnabled) {
                context.drawTextWithShadow(client.textRenderer, "FPS: " + client.getCurrentFps(), 20, textY, 0x55FF55);
            }
        }

        // 2. Блок информации об игроке (слева по центру)
        if (Modules.playerInfo) {
            int px = 12;
            int py = height / 2 - 30;
            drawFloatingPanel(context, px, py, 110, 42);
            context.drawTextWithShadow(client.textRenderer, client.player.getName().getString(), px + 10, py + 7, 0xFFFFFF);
            context.drawTextWithShadow(client.textRenderer, "HP: " + (int)client.player.getHealth(), px + 10, py + 22, 0xFF5555);
        }

        // 3. Панель Хоткеев (справа по центру)
        if (Modules.hotkeysEnabled) {
            int hx = width - 115;
            int hy = height / 2 - 40;
            drawFloatingPanel(context, hx, hy, 105, 60);
            context.drawTextWithShadow(client.textRenderer, "§bHotkeys", hx + 10, hy + 6, 0xFFFFFF);
            context.drawTextWithShadow(client.textRenderer, "Menu: RShift", hx + 10, hy + 22, 0xAAAAAA);
            context.drawTextWithShadow(client.textRenderer, "Brightness: H", hx + 10, hy + 36, 0xAAAAAA);
        }

        // 4. Аккуратный Армор-Худ вокруг хотбара
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

    private static void drawFloatingPanel(DrawContext context, int x, int y, int w, int h) {
        context.fill(x, y, x + w, y + h, 0xCC121216);
        context.drawBorder(x, y, w, h, 0xFF2A2A35);
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
