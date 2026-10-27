package net.vortex.visuals;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class VortexScreen extends Screen {
    private enum Category { COMBAT, VISUALS, HUD, UTILITIES, SETTINGS }
    private Category currentCategory = Category.HUD;

    public VortexScreen() {
        super(Text.literal("Vortex Visuals"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int panelX = centerX - 240;
        int panelY = centerY - 150;

        // Клик по левой панели категорий
        if (mouseX >= panelX + 15 && mouseX <= panelX + 115) {
            int catY = panelY + 110;
            if (mouseY >= catY && mouseY <= catY + 20) { currentCategory = Category.COMBAT; return true; }
            if (mouseY >= catY + 25 && mouseY <= catY + 45) { currentCategory = Category.VISUALS; return true; }
            if (mouseY >= catY + 50 && mouseY <= catY + 70) { currentCategory = Category.HUD; return true; }
            if (mouseY >= catY + 75 && mouseY <= catY + 95) { currentCategory = Category.UTILITIES; return true; }
            if (mouseY >= catY + 100 && mouseY <= catY + 120) { currentCategory = Category.SETTINGS; return true; }
        }

        int startX = panelX + 135;
        int startY = panelY + 75;
        int cardW = 330;
        int cardH = 42;

        if (currentCategory == Category.COMBAT) {
            if (checkCard(mouseX, mouseY, startX, startY, cardW, cardH, 0)) Modules.deathEffect = !Modules.deathEffect;
            if (checkCard(mouseX, mouseY, startX, startY, cardW, cardH, 1)) Modules.killEffect = !Modules.killEffect;
            if (checkCard(mouseX, mouseY, startX, startY, cardW, cardH, 2)) Modules.hitSound = !Modules.hitSound;
            if (checkCard(mouseX, mouseY, startX, startY, cardW, cardH, 3)) Modules.deathSound = !Modules.deathSound;
            if (checkCard(mouseX, mouseY, startX, startY, cardW, cardH, 4)) Modules.targetEsp = !Modules.targetEsp;
        } else if (currentCategory == Category.VISUALS) {
            if (checkCard(mouseX, mouseY, startX, startY, cardW, cardH, 0)) Modules.fullBright = !Modules.fullBright;
            if (checkCard(mouseX, mouseY, startX, startY, cardW, cardH, 1)) Modules.particles = !Modules.particles;
        } else if (currentCategory == Category.HUD) {
            if (checkCard(mouseX, mouseY, startX, startY, cardW, cardH, 0)) Modules.watermarkEnabled = !Modules.watermarkEnabled;
            if (checkCard(mouseX, mouseY, startX, startY, cardW, cardH, 1)) Modules.playerInfo = !Modules.playerInfo;
            if (checkCard(mouseX, mouseY, startX, startY, cardW, cardH, 2)) Modules.potionsEnabled = !Modules.potionsEnabled;
            if (checkCard(mouseX, mouseY, startX, startY, cardW, cardH, 3)) Modules.cooldownsEnabled = !Modules.cooldownsEnabled;
            if (checkCard(mouseX, mouseY, startX, startY, cardW, cardH, 4)) Modules.keybindsEnabled = !Modules.keybindsEnabled;
            if (checkCard(mouseX, mouseY, startX, startY, cardW, cardH, 5)) Modules.keystrokesEnabled = !Modules.keystrokesEnabled;
            if (checkCard(mouseX, mouseY, startX, startY, cardW, cardH, 6)) Modules.inventoryHudEnabled = !Modules.inventoryHudEnabled;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean checkCard(double mx, double my, int startX, int startY, int w, int h, int index) {
        int y = startY + index * (h + 6);
        return mx >= startX && mx <= startX + w && my >= y && my <= y + h;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x80000000);

        int panelW = 480;
        int panelH = 340;
        int panelX = (this.width - panelW) / 2;
        int panelY = (this.height - panelH) / 2;

        // Основное окно интерфейса
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xEE121118);
        context.drawBorder(panelX, panelY, panelW, panelH, 0xFF2A263B);

        // Левая панель категорий
        context.fill(panelX, panelY, panelX + 125, panelY + panelH, 0xFF0D0C12);
        context.drawTextWithShadow(this.textRenderer, "P", panelX + 18, panelY + 20, 0x7A3EE4);
        context.drawTextWithShadow(this.textRenderer, "Vortex", panelX + 34, panelY + 18, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, "1.1.1", panelX + 34, panelY + 30, 0x777588);

        int catY = panelY + 100;
        drawCategoryButton(context, "⚔ Бой", panelX + 15, catY, currentCategory == Category.COMBAT);
        drawCategoryButton(context, "🎨 Визуалы", panelX + 15, catY + 25, currentCategory == Category.VISUALS);
        drawCategoryButton(context, "🖥️ Интерфейс", panelX + 15, catY + 50, currentCategory == Category.HUD);
        drawCategoryButton(context, "⚙️ Утилиты", panelX + 15, catY + 75, currentCategory == Category.UTILITIES);
        drawCategoryButton(context, "🔧 Настройки", panelX + 15, catY + 100, currentCategory == Category.SETTINGS);

        int searchX = panelX + 280;
        int searchY = panelY + 18;
        context.fill(searchX, searchY, searchX + 180, searchY + 24, 0xFF191722);
        context.drawTextWithShadow(this.textRenderer, "🔍 Поиск...", searchX + 10, searchY + 8, 0x666375);

        String catTitle = currentCategory == Category.COMBAT ? "Бой" : (currentCategory == Category.HUD ? "Интерфейс HUD" : "Визуальные модули");
        context.drawTextWithShadow(this.textRenderer, "Категория: " + catTitle, panelX + 135, panelY + 24, 0xFFFFFF);

        int startX = panelX + 135;
        int startY = panelY + 65;
        int cardW = 330;
        int cardH = 36;

        if (currentCategory == Category.COMBAT) {
            drawFeatureCard(context, "Death Effect", "Эффект при убийстве врага.", Modules.deathEffect, startX, startY, 0, cardW, cardH);
            drawFeatureCard(context, "Kill Effect", "Эффект души при убийстве.", Modules.killEffect, startX, startY, 1, cardW, cardH);
            drawFeatureCard(context, "Hit Sound", "Звук при попадании по цели.", Modules.hitSound, startX, startY, 2, cardW, cardH);
            drawFeatureCard(context, "Death Sound", "Звук при вашей смерти.", Modules.deathSound, startX, startY, 3, cardW, cardH);
            drawFeatureCard(context, "Target ESP", "Подсвечивает текущую цель.", Modules.targetEsp, startX, startY, 4, cardW, cardH);
        } else if (currentCategory == Category.VISUALS) {
            drawFeatureCard(context, "Full Bright", "Максимальная яркость мира.", Modules.fullBright, startX, startY, 0, cardW, cardH);
            drawFeatureCard(context, "Particles", "Кастомные частицы.", Modules.particles, startX, startY, 1, cardW, cardH);
        } else if (currentCategory == Category.HUD) {
            drawFeatureCard(context, "Watermark", "Отображение вотермарки сверху.", Modules.watermarkEnabled, startX, startY, 0, cardW, cardH);
            drawFeatureCard(context, "Player Info", "HP, ник и полоска здоровья.", Modules.playerInfo, startX, startY, 1, cardW, cardH);
            drawFeatureCard(context, "Potions HUD", "Список активных зелий.", Modules.potionsEnabled, startX, startY, 2, cardW, cardH);
            drawFeatureCard(context, "Cooldowns", "Динамические кулдауны предметов.", Modules.cooldownsEnabled, startX, startY, 3, cardW, cardH);
            drawFeatureCard(context, "Keybinds", "Панель активных биндов.", Modules.keybindsEnabled, startX, startY, 4, cardW, cardH);
            drawFeatureCard(context, "Keystrokes", "Отображение клавиш WASD.", Modules.keystrokesEnabled, startX, startY, 5, cardW, cardH);
            drawFeatureCard(context, "Inventory HUD", "Мини-инвентарь на экране.", Modules.inventoryHudEnabled, startX, startY, 6, cardW, cardH);
        }
    }

    private void drawCategoryButton(DrawContext context, String name, int x, int y, boolean active) {
        if (active) {
            context.fill(x, y - 2, x + 95, y + 18, 0xFF242035);
        }
        context.drawTextWithShadow(this.textRenderer, name, x + 8, y + 4, active ? 0xFFFFFFFF : 0x888599);
    }

    private void drawFeatureCard(DrawContext context, String title, String desc, boolean state, int startX, int startY, int index, int w, int h) {
        int y = startY + index * (h + 6);
        context.fill(startX, y, startX + w, y + h, 0xFF191722);
        context.drawTextWithShadow(this.textRenderer, title, startX + 12, y + 6, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, desc, startX + 12, y + 18, 0x777588);

        int tw = 32, th = 16;
        int tx = startX + w - tw - 12;
        int ty = y + (h - th) / 2;
        context.fill(tx, ty, tx + tw, ty + th, state ? 0xFF7A3EE4 : 0xFF2A263B);
        int kx = state ? (tx + tw - 14) : (tx + 2);
        context.fill(kx, ty + 2, kx + 12, ty + 12, 0xFFFFFFFF);
    }

    @Override
    public boolean shouldPause() { return false; }
}
