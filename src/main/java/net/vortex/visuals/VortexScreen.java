package net.vortex.visuals;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
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
        rebuildWidgets();
    }

    private void rebuildWidgets() {
        this.clearChildren();

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        int tabWidth = 70;
        int tabHeight = 18;
        int tabY = centerY - 85;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Visuals"), b -> { currentTab = Tab.VISUALS; rebuildWidgets(); })
                .dimensions(centerX - 110, tabY, tabWidth, tabHeight).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("HUD"), b -> { currentTab = Tab.HUD; rebuildWidgets(); })
                .dimensions(centerX - 35, tabY, tabWidth, tabHeight).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Utilities"), b -> { currentTab = Tab.UTILITIES; rebuildWidgets(); })
                .dimensions(centerX + 40, tabY, tabWidth, tabHeight).build());

        int startY = centerY - 55;

        if (currentTab == Tab.VISUALS) {
            addToggle(centerX - 150, startY, "Logo", Modules.logoEnabled, val -> Modules.logoEnabled = val);
            addToggle(centerX + 5, startY, "FPS Counter", Modules.fpsEnabled, val -> Modules.fpsEnabled = val);

            addToggleWithSettings(centerX - 150, startY + 26, "Hit Color", Modules.hitColor, val -> Modules.hitColor = val);
            addToggle(centerX + 5, startY + 26, "Jump Circles", Modules.jumpCircles, val -> Modules.jumpCircles = val);

            addToggle(centerX - 150, startY + 52, "Particles", Modules.particles, val -> Modules.particles = val);
            addToggleWithSettings(centerX + 5, startY + 52, "Time Changer", Modules.timeChanger, val -> Modules.timeChanger = val);

        } else if (currentTab == Tab.HUD) {
            addToggle(centerX - 150, startY, "Armor Status", Modules.armorStatusEnabled, val -> Modules.armorStatusEnabled = val);
            addToggle(centerX + 5, startY, "Player Info", Modules.playerInfo, val -> Modules.playerInfo = val);

            addToggle(centerX - 150, startY + 26, "Potions", Modules.potionsEnabled, val -> Modules.potionsEnabled = val);
            addToggle(centerX + 5, startY + 26, "Cooldowns", Modules.cooldownsEnabled, val -> Modules.cooldownsEnabled = val);

        } else if (currentTab == Tab.UTILITIES) {
            addToggle(centerX - 150, startY, "Hotkeys HUD", Modules.hotkeysEnabled, val -> Modules.hotkeysEnabled = val);
            addToggle(centerX + 5, startY, "Auto Fish", Modules.autoFish, val -> Modules.autoFish = val);

            addToggle(centerX - 150, startY + 26, "Brightness", Modules.brightness, val -> Modules.brightness = val);
        }
    }

    private void addToggle(int x, int y, String name, boolean state, java.util.function.Consumer<Boolean> action) {
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(name + ": " + (state ? "ON" : "OFF")),
                button -> {
                    boolean newState = !state;
                    action.accept(newState);
                    button.setMessage(Text.literal(name + ": " + (newState ? "ON" : "OFF")));
                }
        ).dimensions(x, y, 145, 20).build());
    }

    private void addToggleWithSettings(int x, int y, String name, boolean state, java.util.function.Consumer<Boolean> action) {
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(name + " [⚙]"),
                button -> {
                    // ЛКМ переключает состояние
                    boolean newState = !state;
                    action.accept(newState);
                }
        ).dimensions(x, y, 145, 20).build());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) { // ПКМ (правая кнопка мыши)
            // Проверяем, кликнули ли по кнопке Time Changer или Hit Color для открытия их настроек
            int centerX = this.width / 2;
            int centerY = this.height / 2;
            int startY = centerY - 55;

            if (currentTab == Tab.VISUALS) {
                // Time Changer находится на второй колонке, третья строка (startY + 52)
                if (mouseX >= centerX + 5 && mouseX <= centerX + 5 + 145 && mouseY >= startY + 52 && mouseY <= startY + 72) {
                    this.client.setScreen(new FeatureSettingsScreen(this, "Time Changer"));
                    return true;
                }
                // Hit Color находится на первой колонке, вторая строка (startY + 26)
                if (mouseX >= centerX - 150 && mouseX <= centerX - 5 && mouseY >= startY + 26 && mouseY <= startY + 46) {
                    this.client.setScreen(new FeatureSettingsScreen(this, "Hit Color"));
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0050505);

        int panelWidth = 320;
        int panelHeight = 200;
        int panelX = (this.width - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2;

        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xEE181820);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0x44FFFFFF);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, panelY + 10, 0x00FFCC);
        context.drawTextWithShadow(this.textRenderer, "§7ПКМ по кнопке [⚙] — настройки функции", panelX + 15, panelY + panelHeight - 15, 0x888888);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
