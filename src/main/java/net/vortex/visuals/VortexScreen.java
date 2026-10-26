package net.vortex.visuals;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class VortexScreen extends Screen {
    private enum Tab { VISUALS, HUD, UTILITIES }
    private Tab currentTab = Tab.VISUALS;

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
        int panelX = centerX - 180;
        int panelY = centerY - 120;

        // Вкладки
        if (mouseY >= panelY + 16 && mouseY <= panelY + 40) {
            if (mouseX >= panelX + 16 && mouseX <= panelX + 115) { currentTab = Tab.VISUALS; return true; }
            if (mouseX >= panelX + 125 && mouseX <= panelX + 225) { currentTab = Tab.HUD; return true; }
            if (mouseX >= panelX + 235 && mouseX <= panelX + 344) { currentTab = Tab.UTILITIES; return true; }
        }

        int startX = panelX + 16;
        int startY = panelY + 60;
        int btnWidth = 170;
        int btnHeight = 32;

        if (currentTab == Tab.VISUALS) {
            if (checkClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 0, 0)) Modules.logoEnabled = !Modules.logoEnabled;
            if (checkClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 1, 0)) Modules.fpsEnabled = !Modules.fpsEnabled;
            
            if (checkClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 0, 1)) {
                if (button == 1) this.client.setScreen(new FeatureSettingsScreen(this, "Hit Color"));
                else Modules.hitColor = !Modules.hitColor;
            }
            if (checkClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 1, 1)) Modules.jumpCircles = !Modules.jumpCircles;

            if (checkClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 0, 2)) Modules.particles = !Modules.particles;
            if (checkClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 1, 2)) {
                if (button == 1) this.client.setScreen(new FeatureSettingsScreen(this, "Time Changer"));
                else Modules.timeChanger = !Modules.timeChanger;
            }
        } else if (currentTab == Tab.HUD) {
            if (checkClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 0, 0)) Modules.armorStatusEnabled = !Modules.armorStatusEnabled;
            if (checkClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 1, 0)) Modules.playerInfo = !Modules.playerInfo;
            if (checkClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 0, 1)) Modules.potionsEnabled = !Modules.potionsEnabled;
            if (checkClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 1, 1)) Modules.cooldownsEnabled = !Modules.cooldownsEnabled;
        } else if (currentTab == Tab.UTILITIES) {
            if (checkClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 0, 0)) Modules.hotkeysEnabled = !Modules.hotkeysEnabled;
            if (checkClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 1, 0)) Modules.autoFish = !Modules.autoFish;
            if (checkClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 0, 1)) Modules.brightness = !Modules.brightness;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean checkClick(double mx, double my, int startX, int startY, int w, int h, int col, int row) {
        int x = startX + col * (w + 8);
        int y = startY + row * (h + 10);
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fillGradient(0, 0, this.width, this.height, 0xEE08080C, 0xFFA000000);

        int panelW = 360;
        int panelH = 250;
        int panelX = (this.width - panelW) / 2;
        int panelY = (this.height - panelH) / 2;

        // Фон главного окна (темный премиум-стиль с точной границей)
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xFF0E0E12);
        context.drawBorder(panelX, panelY, panelW, panelH, 0xFF22222D);

        // Вкладки
        drawTab(context, "Visuals", panelX + 16, panelY + 16, 105, 24, currentTab == Tab.VISUALS);
        drawTab(context, "HUD", panelX + 127, panelY + 16, 105, 24, currentTab == Tab.HUD);
        drawTab(context, "Utilities", panelX + 238, panelY + 16, 106, 24, currentTab == Tab.UTILITIES);

        int startX = panelX + 16;
        int startY = panelY + 60;
        int btnWidth = 170;
        int btnHeight = 32;

        if (currentTab == Tab.VISUALS) {
            drawFeatureCard(context, "Logo", Modules.logoEnabled, false, startX, startY, 0, 0, btnWidth, btnHeight);
            drawFeatureCard(context, "FPS Counter", Modules.fpsEnabled, false, startX, startY, 1, 0, btnWidth, btnHeight);
            drawFeatureCard(context, "Hit Color", Modules.hitColor, true, startX, startY, 0, 1, btnWidth, btnHeight);
            drawFeatureCard(context, "Jump Circles", Modules.jumpCircles, false, startX, startY, 1, 1, btnWidth, btnHeight);
            drawFeatureCard(context, "Particles", Modules.particles, false, startX, startY, 0, 2, btnWidth, btnHeight);
            drawFeatureCard(context, "Time Changer", Modules.timeChanger, true, startX, startY, 1, 2, btnWidth, btnHeight);
        } else if (currentTab == Tab.HUD) {
            drawFeatureCard(context, "Armor Status", Modules.armorStatusEnabled, false, startX, startY, 0, 0, btnWidth, btnHeight);
            drawFeatureCard(context, "Player Info", Modules.playerInfo, false, startX, startY, 1, 0, btnWidth, btnHeight);
            drawFeatureCard(context, "Potions", Modules.potionsEnabled, false, startX, startY, 0, 1, btnWidth, btnHeight);
            drawFeatureCard(context, "Cooldowns", Modules.cooldownsEnabled, false, startX, startY, 1, 1, btnWidth, btnHeight);
        } else if (currentTab == Tab.UTILITIES) {
            drawFeatureCard(context, "Hotkeys HUD", Modules.hotkeysEnabled, false, startX, startY, 0, 0, btnWidth, btnHeight);
            drawFeatureCard(context, "Auto Fish", Modules.autoFish, false, startX, startY, 1, 0, btnWidth, btnHeight);
            drawFeatureCard(context, "Brightness", Modules.brightness, false, startX, startY, 0, 1, btnWidth, btnHeight);
        }

        context.drawTextWithShadow(this.textRenderer, "§7ПКМ по кнопке с [⚙] — настройки функции", panelX + 16, panelY + panelH - 22, 0x666677);
    }

    private void drawTab(DrawContext context, String name, int x, int y, int w, int h, boolean active) {
        int bg = active ? 0xFF1C1C24 : 0xFF121216;
        int border = active ? 0xFF00FFCC : 0xFF1E1E26;
        int color = active ? 0x00FFCC : 0x888899;
        context.fill(x, y, x + w, y + h, bg);
        context.drawBorder(x, y, w, h, border);
        context.drawCenteredTextWithShadow(this.textRenderer, name, x + w / 2, y + 8, color);
    }

    private void drawFeatureCard(DrawContext context, String name, boolean state, boolean hasSettings, int startX, int startY, int col, int row, int w, int h) {
        int x = startX + col * (w + 8);
        int y = startY + row * (h + 10);

        context.fill(x, y, x + w, y + h, 0xFF14141A);
        context.drawBorder(x, y, w, h, state ? 0xFF2A3A4A : 0xFF1E1E26);

        String title = name + (hasSettings ? " §b[⚙]" : "");
        context.drawTextWithShadow(this.textRenderer, title, x + 12, y + 12, 0xFFFFFF);

        // Тумблер
        int tw = 30, th = 14;
        int tx = x + w - tw - 12;
        int ty = y + (h - th) / 2;
        context.fill(tx, ty, tx + tw, ty + th, state ? 0xFF00B386 : 0xFF262633);
        int kx = state ? (tx + tw - 12) : (tx + 2);
        context.fill(kx, ty + 2, kx + 10, ty + 12, 0xFFFFFFFF);
    }

    @Override
    public boolean shouldPause() { return false; }
}
