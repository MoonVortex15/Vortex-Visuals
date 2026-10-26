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

        // 1. Логотип Vortex
        if (Modules.logoEnabled) {
            context.drawTextWithShadow(client.textRenderer, "Vortex Visuals", 5, 5, 0x00FFCC);
        }

        // 2. Счетчик FPS
        if (Modules.fpsEnabled) {
            int fps = client.getCurrentFps();
            context.drawTextWithShadow(client.textRenderer, "FPS: " + fps, 5, 17, 0xFFFFFF);
        }

        // 3. Умный Армор-Худ вокруг хотбара
        if (Modules.armorStatusEnabled) {
            int hotbarLeft = width / 2 - 91;
            int hotbarRight = width / 2 + 91;
            int hudY = height - 19;

            // Слева от хотбара: Ботинки и Поножи
            renderArmorItem(context, client, client.player.getEquippedStack(EquipmentSlot.FEET), hotbarLeft - 50, hudY);
            renderArmorItem(context, client, client.player.getEquippedStack(EquipmentSlot.LEGS), hotbarLeft - 25, hudY);

            // Справа от хотбара: Нагрудник и Шлем
            renderArmorItem(context, client, client.player.getEquippedStack(EquipmentSlot.CHEST), hotbarRight + 5, hudY);
            renderArmorItem(context, client, client.player.getEquippedStack(EquipmentSlot.HEAD), hotbarRight + 30, hudY);
        }
    }

    private static void renderArmorItem(DrawContext context, MinecraftClient client, ItemStack stack, int x, int y) {
        if (stack.isEmpty()) return;

        context.drawItem(stack, x, y);

        if (stack.isDamageable()) {
            int maxDurability = stack.getMaxDamage();
            int currentDurability = maxDurability - stack.getDamage();
            int percent = (int) (((float) currentDurability / maxDurability) * 100);

            int color = 0x55FF55; // Зеленый
            if (percent < 50) color = 0xFFFF55; // Желтый
            if (percent < 20) color = 0xFF5555; // Красный

            context.drawTextWithShadow(client.textRenderer, percent + "%", x + 17, y + 4, color);
        }
    }
}
