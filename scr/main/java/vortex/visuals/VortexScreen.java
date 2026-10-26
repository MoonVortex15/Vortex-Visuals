package vortex.visuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class VortexScreen extends Screen {
    public VortexScreen() {
        super(Text.literal("Vortex Visuals"));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx, mouseX, mouseY, delta);
        MinecraftClient mc = MinecraftClient.getInstance();

        int sw = this.width;
        int sh = this.height;
        int w = 460;
        int h = 300;
        int x = (sw - w) / 2;
        int y = (sh - h) / 2;

        // Основное окно карточки
        Ui.card(ctx, x, y, w, h, 14);

        // Заголовок
        Ui.text(ctx, mc, "Vortex Visuals", x + 20, y + 20, Ui.TEXT);
        Ui.text(ctx, mc, "Настройка модулей HUD", x + 20, y + 34, Ui.SUB);

        // Отрисовка списка модулей (тумблеры)
        int startX = x + 20;
        int startY = y + 70;
        int itemW = 205;
        int itemH = 45;

        for (int i = 0; i < Modules.ALL.size(); i++) {
            Modules.Module mod = Modules.ALL.get(i);
            int col = i % 2;
            int row = i / 2;
            int ix = startX + col * (itemW + 10);
            int iy = startY + row * (itemH + 8);

            Ui.roundRect(ctx, ix, iy, itemW, itemH, 8, 0x15FFFFFF);
            Ui.text(ctx, mc, mod.name, ix + 12, iy + 10, Ui.TEXT);
            Ui.text(ctx, mc, mc.textRenderer.trimToWidth(mod.desc, itemW - 24), ix + 12, iy + 24, Ui.SUB);

            // Кнопка-тумблер
            int swX = ix + itemW - 36;
            int swY = iy + 14;
            Ui.roundRect(ctx, swX, swY, 24, 14, 7, mod.enabled ? Ui.ACCENT : 0xFF353646);
            Ui.roundRect(ctx, mod.enabled ? swX + 12 : swX + 2, swY + 2, 10, 10, 5, 0xFFFFFFFF);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int sw = this.width;
        int sh = this.height;
        int w = 460;
        int h = 300;
        int x = (sw - w) / 2;
        int y = (sh - h) / 2;

        int startX = x + 20;
        int startY = y + 70;
        int itemW = 205;
        int itemH = 45;

        for (int i = 0; i < Modules.ALL.size(); i++) {
            Modules.Module mod = Modules.ALL.get(i);
            int col = i % 2;
            int row = i / 2;
            int ix = startX + col * (itemW + 10);
            int iy = startY + row * (itemH + 8);

            if (mouseX >= ix && mouseX <= ix + itemW && mouseY >= iy && mouseY <= iy + itemH) {
                mod.enabled = !mod.enabled;
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
