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
        int panelX = centerX - 230;
        int panelY = centerY - 140;

        if (mouseY >= panelY + 18 && mouseY <= panelY + 42) {
            if (mouseX >= panelX + 25 && mouseX <= panelX + 90) { currentTab = Tab.VISUALS; return true; }
            if (mouseX >= panelX + 100 && mouseX <= panelX + 150) { currentTab = Tab.HUD; return true; }
            if (mouseX >= panelX + 160 && mouseX <= panelX + 240) { currentTab = Tab.UTILITIES; return true; }
        }

        int startX = panelX + 25;
        int startY = panelY + 70;
        int cardW = 195;
        int cardH = 34;

        if (currentTab == Tab.VISUALS) {
            if (checkClick(mouseX, mouseY, startX, startY, cardW, cardH, 0, 0)) Modules.chinaHit = !Modules.chinaHit;
            if (checkClick(mouseX, mouseY, startX, startY, cardW, cardH, 1, 0)) Modules.customHand = !Modules.customHand;
            if (checkClick(mouseX, mouseY, startX, startY, cardW, cardH, 0, 1)) Modules.fullBright = !Modules.fullBright;
            if (checkClick(mouseX, mouseY, startX, startY, cardW, cardH, 1, 1)) {
                if (button == 1) this.client.setScreen(new FeatureSettingsScreen(this, "Hit Color"));
                else Modules.hitColor = !Modules.hitColor;
            }
            if (checkClick(mouseX, mouseY, startX, startY, cardW, cardH, 0, 2)) Modules.hitboxCustomizer = !Modules.hitboxCustomizer;
            if (checkClick(mouseX, mouseY, startX, startY, cardW, cardH, 1, 2)) Modules.jumpCircles = !Modules.jumpCircles;
            if (checkClick(mouseX, mouseY, startX, startY, cardW, cardH, 0, 3)) Modules.particles = !Modules.particles;
            if (checkClick(mouseX, mouseY, startX, startY, cardW, cardH, 1, 3)) Modules.renderTweaks = !Modules.renderTweaks;
        } else if (currentTab == Tab.HUD) {
            if (checkClick(mouseX, mouseY, startX, startY, cardW, cardH, 0, 0)) Modules.playerInfo = !Modules.playerInfo;
            if (checkClick(mouseX, mouseY, startX, startY, cardW, cardH, 1, 0)) Modules.armorStatusEnabled = !Modules.armorStatusEnabled;
            if (checkClick(mouseX, mouseY, startX, startY, cardW, cardH, 0, 1)) Modules.potionsEnabled = !Modules.potionsEnabled;
            if (checkClick(mouseX, mouseY, startX, startY, cardW, cardH, 1, 1)) Modules.cooldownsEnabled = !Modules.cooldownsEnabled;
            if (checkClick(mouseX, mouseY, startX, startY, cardW, cardH, 0, 2)) Modules.hotkeysEnabled = !Modules.hotkeysEnabled;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean checkClick(double mx, double my, int startX, int startY, int w, int h, int col, int row) {
        int x = startX + col * (w + 12);
        int y = startY + row * (h + 10);
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Чистый и плотный фон без мути
        context.fill(0, 0, this.width, this.height, 0xB0000000);

        int panelW = 460;
        int panelH = 300;
        int panelX = (this.width - panelW) / 2;
        int panelY = (this.height - panelH) / 2;

        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xFF0B0A10);
        context.drawBorder(panelX, panelY, panelW, panelH, 0xFF242035);

        drawTab(context, "Visuals", panelX + 25, panelY + 18, 65, 20, currentTab == Tab.VISUALS);
        drawTab(context, "HUD", panelX + 100, panelY + 18, 45, 20, currentTab == Tab.HUD);
        drawTab(context, "Utilities", panelX + 155, panelY + 18, 65, 20, currentTab == Tab.UTILITIES);

        int searchX = panelX + panelW - 170;
        int searchY = panelY + 16;
        context.fill(searchX, searchY, searchX + 145, searchY + 24, 0xFF14131C);
        context.drawBorder(searchX, searchY, 145, 24, 0xFF221F30);
        context.drawTextWithShadow(this.textRenderer, "🔍 Search", searchX + 10, searchY + 8, 0x777588);

        int startX = panelX + 25;
        int startY = panelY + 70;
        int cardW = 195;
        int cardH = 34;

        if (currentTab == Tab.VISUALS) {
            drawFeatureCard(context, "China Hit", Modules.chinaHit, false, startX, startY, 0, 0, cardW, cardH);
            drawFeatureCard(context, "Custom Hand", Modules.customHand, false, startX, startY, 1, 0, cardW, cardH);
            drawFeatureCard(context, "Full Bright", Modules.fullBright, false, startX, startY, 0, 1, cardW, cardH);
            drawFeatureCard(context, "Hit Color", Modules.hitColor, true, startX, startY, 1, 1, cardW, cardH);
            drawFeatureCard(context, "Hitbox Customizer", Modules.hitboxCustomizer, false, startX, startY, 0, 2, cardW, cardH);
            drawFeatureCard(context, "Jump Circles", Modules.jumpCircles, false, startX, startY, 1, 2, cardW, cardH);
            drawFeatureCard(context, "Particles", Modules.particles, false, startX, startY, 0, 3, cardW, cardH);
            drawFeatureCard(context, "Render Tweaks", Modules.renderTweaks, false, startX, startY, 1, 3, cardW, cardH);
        } else if (currentTab == Tab.HUD) {
            drawFeatureCard(context, "Player Info", Modules.playerInfo, false, startX, startY, 0, 0, cardW, cardH);
            drawFeatureCard(context, "Armor Status", Modules.armorStatusEnabled, false, startX, startY, 1, 0, cardW, cardH);
            drawFeatureCard(context, "Potions HUD", Modules.potionsEnabled, false, startX, startY, 0, 1, cardW, cardH);
            drawFeatureCard(context, "Cooldowns", Modules.cooldownsEnabled, false, startX, startY, 1, 1, cardW, cardH);
            drawFeatureCard(context, "Hotkeys HUD", Modules.hotkeysEnabled, false, startX, startY, 0, 2, cardW, cardH);
        }
    }

    private void drawTab(DrawContext context, String name, int x, int y, int w, int h, boolean active) {
        int color = active ? 0xFFFFFFFF : 0x777588;
        context.drawTextWithShadow(this.textRenderer, name, x, y + 6, color);
    }

    private void drawFeatureCard(DrawContext context, String name, boolean state, boolean hasSettings, int startX, int startY, int col, int row, int w, int h) {
        int x = startX + col * (w + 12);
        int y = startY + row * (h + 10);

        context.fill(x, y, x + w, y + h, state ? 0xFF161422 : 0xFF111017);
        context.drawBorder(x, y, w, h, state ? 0xFF362E52 : 0xFF191722);

        String title = name + (hasSettings ? " §b[⚙]" : "");
        context.drawTextWithShadow(this.textRenderer, title, x + 14, y + 11, state ? 0xFFEEEEFF : 0x9995A6);

        int tw = 32, th = 16;
        int tx = x + w - tw - 12;
        int ty = y + (h - th) / 2;
        context.fill(tx, ty, tx + tw, ty + th, state ? 0xFF7A3EE4 : 0xFF1E1C2B);
        int kx = state ? (tx + tw - 14) : (tx + 2);
        context.fill(kx, ty + 2, kx + 12, ty + 12, 0xFFFFFFFF);
    }

    @Override
    public boolean shouldPause() { return false; }
}
