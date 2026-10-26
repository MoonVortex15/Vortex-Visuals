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
        int panelX = centerX - 170;
        int panelY = centerY - 110;

        // Кнопки вкладок сверху
        if (mouseY >= panelY + 15 && mouseY <= panelY + 35) {
            if (mouseX >= panelX + 15 && mouseX <= panelX + 105) { currentTab = Tab.VISUALS; return true; }
            if (mouseX >= panelX + 115 && mouseX <= panelX + 205) { currentTab = Tab.HUD; return true; }
            if (mouseX >= panelX + 215 && mouseX <= panelX + 325) { currentTab = Tab.UTILITIES; return true; }
        }

        // Элементы в сетке (два столбца)
        int startX = panelX + 15;
        int startY = panelY + 55;
        int btnWidth = 165;
        int btnHeight = 28;
        int gapX = 10;
        int gapY = 8;

        if (currentTab == Tab.VISUALS) {
            if (checkToggleClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 0, 0)) Modules.logoEnabled = !Modules.logoEnabled;
            if (checkToggleClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 1, 0)) Modules.fpsEnabled = !Modules.fpsEnabled;
            
            if (checkToggleClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 0, 1)) {
                if (button == 1) this.client.setScreen(new FeatureSettingsScreen(this, "Hit Color"));
                else Modules.hitColor = !Modules.hitColor;
            }
            if (checkToggleClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 1, 1)) Modules.jumpCircles = !Modules.jumpCircles;

            if (checkToggleClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 0, 2)) Modules.particles = !Modules.particles;
            if (checkToggleClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 1, 2)) {
                if (button == 1) this.client.setScreen(new FeatureSettingsScreen(this, "Time Changer"));
                else Modules.timeChanger = !Modules.timeChanger;
            }
        } else if (currentTab == Tab.HUD) {
            if (checkToggleClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 0, 0)) Modules.armorStatusEnabled = !Modules.armorStatusEnabled;
            if (checkToggleClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 1, 0)) Modules.playerInfo = !Modules.playerInfo;
            if (checkToggleClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 0, 1)) Modules.potionsEnabled = !Modules.potionsEnabled;
            if (checkToggleClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 1, 1)) Modules.cooldownsEnabled = !Modules.cooldownsEnabled;
        } else if (currentTab == Tab.UTILITIES) {
            if (checkToggleClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 0, 0)) Modules.hotkeysEnabled = !Modules.hotkeysEnabled;
            if (checkToggleClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 1, 0)) Modules.autoFish = !Modules.autoFish;
            if (checkToggleClick(mouseX, mouseY, startX, startY, btnWidth, btnHeight, 0, 1)) Modules.brightness = !Modules.brightness;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean checkToggleClick(double mx, double my, int startX, int startY, int w, int h, int col, int row) {
        int x = startX + col * (w + 10);
        int y = startY + row * (h + 8);
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fillGradient(0, 0, this.width, this.height, 0xDD000000, 0xEE050508);

        int panelWidth = 360;
        int panelHeight = 240;
        int panelX = (this.width - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2;

        // Основное окно с закругленным стилем (темный фон + неоновая обводка)
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF121216);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFF2A2A35);

        // Вкладки
        drawTabButton(context, "Visuals", panelX + 15, panelY + 15, 90, 20, currentTab == Tab.VISUALS);
        drawTabButton(context, "HUD", panelX + 115, panelY + 15, 90, 20, currentTab == Tab.HUD);
        drawTabButton(context, "Utilities", panelX + 215, panelY + 15, 110, 20, currentTab == Tab.UTILITIES);

        int startX = panelX + 15;
        int startY = panelY + 55;
        int btnWidth = 165;
        int btnHeight = 28;

        if (currentTab == Tab.VISUALS) {
            drawFeatureButton(context, "Logo", Modules.logoEnabled, false, startX, startY, 0, 0, btnWidth, btnHeight);
            drawFeatureButton(context, "FPS Counter", Modules.fpsEnabled, false, startX, startY, 1, 0, btnWidth, btnHeight);
            drawFeatureButton(context, "Hit Color", Modules.hitColor, true, startX, startY, 0, 1, btnWidth, btnHeight);
            drawFeatureButton(context, "Jump Circles", Modules.jumpCircles, false, startX, startY, 1, 1, btnWidth, btnHeight);
            drawFeatureButton(context, "Particles", Modules.particles, false, startX, startY, 0, 2, btnWidth, btnHeight);
            drawFeatureButton(context, "Time Changer", Modules.timeChanger, true, startX, startY, 1, 2, btnWidth, btnHeight);
        } else if (currentTab == Tab.HUD) {
            drawFeatureButton(context, "Armor Status", Modules.armorStatusEnabled, false, startX, startY, 0, 0, btnWidth, btnHeight);
            drawFeatureButton(context, "Player Info", Modules.playerInfo, false, startX, startY, 1, 0, btnWidth, btnHeight);
            drawFeatureButton(context, "Potions", Modules.potionsEnabled, false, startX, startY, 0, 1, btnWidth, btnHeight);
            drawFeatureButton(context, "Cooldowns", Modules.cooldownsEnabled, false, startX, startY, 1, 1, btnWidth, btnHeight);
        } else if (currentTab == Tab.UTILITIES) {
            drawFeatureButton(context, "Hotkeys HUD", Modules.hotkeysEnabled, false, startX, startY, 0, 0, btnWidth, btnHeight);
            drawFeatureButton(context, "Auto Fish", Modules.autoFish, false, startX, startY, 1, 0, btnWidth, btnHeight);
            drawFeatureButton(context, "Brightness", Modules.brightness, false, startX, startY, 0, 1, btnWidth, btnHeight);
        }

        context.drawTextWithShadow(this.textRenderer, "§8ПКМ по кнопке с [⚙] — настройки", panelX + 15, panelY + panelHeight - 20, 0x888888);
    }

    private void drawTabButton(DrawContext context, String name, int x, int y, int w, int h, boolean active) {
        int bgColor = active ? 0xFF22222D : 0xFF18181C;
        int textColor = active ? 0x00FFCC : 0xAAAAAA;
        context.fill(x, y, x + w, y + h, bgColor);
        context.drawBorder(x, y, w, h, active ? 0xFF00FFCC : 0xFF2A2A35);
        context.drawCenteredTextWithShadow(this.textRenderer, name, x + w / 2, y + 6, textColor);
    }

    private void drawFeatureButton(DrawContext context, String name, boolean state, boolean hasSettings, int startX, int startY, int col, int row, int w, int h) {
        int x = startX + col * (w + 10);
        int y = startY + row * (h + 8);

        context.fill(x, y, x + w, y + h, 0xFF1A1A22);
        context.drawBorder(x, y, w, h, state ? 0xFF334455 : 0xFF252530);

        String displayName = name + (hasSettings ? " [§b⚙§r]" : "");
        context.drawTextWithShadow(this.textRenderer, displayName, x + 10, y + 10, 0xFFFFFF);

        // Кастомный стильный переключатель (тумблер)
        int toggleW = 28;
        int toggleH = 14;
        int toggleX = x + w - toggleW - 10;
        int toggleY = y + (h - toggleH) / 2;

        context.fill(toggleX, toggleY, toggleX + toggleW, toggleY + toggleH, state ? 0xFF00AA77 : 0xFF333340);
        int knobX = state ? (toggleX + toggleW - 12) : (toggleX + 2);
        context.fill(knobX, toggleY + 2, knobX + 10, toggleY + 12, 0xFFFFFFFF);
    }

    @Override
    public boolean shouldPause() { return false; }
}
