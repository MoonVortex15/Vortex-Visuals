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
        // Затемнение фона игры вместо стандартного размытия, чтобы точно не было багов
        ctx.fill(0, 0, this.width, this.height, 0x80000000);
        
        MinecraftClient mc = MinecraftClient.getInstance();

        int w = 420;
        int h = 260;
        int x = (this.width - w) / 2;
        int y = (this.height - h) / 2;

        // Отрисовка основной панели
        Ui.card(ctx, x, y, w, h);

        // Заголовок
        Ui.text(ctx, mc, "Vortex Visuals — HUD Menu", x + 16, y + 16, Ui.TEXT);
        Ui.text(ctx, mc, "Нажми на модуль, чтобы включить/выключить", x + 16, y + 30, Ui.SUB);

        // Список модулей
        int startX = x + 16;
        int startY = y + 60;
        int itemW = 190;
        int itemH = 40;

        for (int i = 0; i < Modules.ALL.size(); i++) {
            Modules.Module mod = Modules.ALL.get(i);
            int col = i % 2;
            int row = i / 2;
            int ix = startX + col * (itemW + 8);
            int iy = startY + row * (itemH + 8);

            // Фон элемента списка
            Ui.fill(ctx, ix, iy, itemW, itemH, mod.enabled ? 0x338A75FF : 0x22FFFFFF);
            Ui.text(ctx, mc, mod.name, ix + 10, iy + 8, Ui.TEXT);
            Ui.text(ctx, mc, mod.enabled ? "Статус: Включен" : "Статус: Выключен", ix + 10, iy + 22, mod.enabled ? Ui.ACCENT : Ui.SUB);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int w = 420;
        int h = 260;
        int x = (this.width - w) / 2;
        int y = (this.height - h) / 2;

        int startX = x + 16;
        int startY = y + 60;
        int itemW = 190;
        int itemH = 40;

        for (int i = 0; i < Modules.ALL.size(); i++) {
            Modules.Module mod = Modules.ALL.get(i);
            int col = i % 2;
            int row = i / 2;
            int ix = startX + col * (itemW + 8);
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
