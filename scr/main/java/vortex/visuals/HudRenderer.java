package vortex.visuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public final class HudRenderer {
    public static void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden || mc.player == null) return;

        int sw = ctx.getScaledWindowWidth();
        int sh = ctx.getScaledWindowHeight();

        // 1. Логотип клиента
        if (Modules.CLIENT_NAME.enabled) {
            int w = 120;
            int h = 20;
            int x = (sw - w) / 2;
            int y = 6;

            Ui.card(ctx, x, y, w, h);
            Ui.fill(ctx, x + 6, y + 6, 8, 8, Ui.ACCENT);
            Ui.text(ctx, mc, "Vortex Visuals", x + 20, y + 6, Ui.TEXT);
        }

        // 2. Счетчик FPS
        if (Modules.FPS_COUNTER.enabled) {
            int x = sw - 100;
            int y = 10;
            int w = 90;
            int h = 24;

            Ui.card(ctx, x, y, w, h);
            Ui.text(ctx, mc, "FPS: " + MinecraftClient.getCurrentFps(), x + 8, y + 8, Ui.TEXT);
        }

        // 3. Минималистичный Армор-Худ по бокам от хотбара инвентаря
        if (Modules.ARMOR_STATUS.enabled) {
            int centerX = sw / 2;
            int hotbarY = sh - 20;

            // Индексы брони в Fabric/Minecraft: 0 - ботинки, 1 - поножи, 2 - нагрудник, 3 - шлем
            ItemStack boots = mc.player.getInventory().getArmorStack(0);
            ItemStack leggings = mc.player.getInventory().getArmorStack(1);
            ItemStack chestplate = mc.player.getInventory().getArmorStack(2);
            ItemStack helmet = mc.player.getInventory().getArmorStack(3);

            int leftX = centerX - 125; // Слева от начала хотбара
            int rightX = centerX + 95;  // Справа от конца хотбара

            // Левая сторона: Поножи (сверху) и Ботинки (снизу)
            int currentY = hotbarY - 32;
            if (!leggings.isEmpty()) {
                renderArmorItem(ctx, mc, leggings, leftX, currentY);
                currentY += 18;
            }
            if (!boots.isEmpty()) {
                renderArmorItem(ctx, mc, boots, leftX, currentY);
            }

            // Правая сторона: Шлем (сверху) и Нагрудник (снизу)
            currentY = hotbarY - 32;
            if (!helmet.isEmpty()) {
                renderArmorItem(ctx, mc, helmet, rightX, currentY);
                currentY += 18;
            }
            if (!chestplate.isEmpty()) {
                renderArmorItem(ctx, mc, chestplate, rightX, currentY);
            }
        }
    }

    private static void renderArmorItem(DrawContext ctx, MinecraftClient mc, ItemStack stack, int x, int y) {
        // Рендерим иконку брони без заливок и рамок
        ctx.drawItem(stack, x, y);

        // Расчет процентов прочности
        if (stack.isDamageable()) {
            int maxDmg = stack.getMaxDamage();
            int dmg = stack.getDamage();
            int percent = 100 - (int)(((float) dmg / maxDmg) * 100);

            String text = percent + "%";
            ctx.drawText(mc.textRenderer, text, x + 18, y + 4, Ui.TEXT, true);
        }
    }

    private HudRenderer() {}
}
