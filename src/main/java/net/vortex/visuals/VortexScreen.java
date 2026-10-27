
package net.vortex.visuals;

import net.minecraft.client.gui.DrawContext;
import net.vortex.visuals.hud.HudRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class VortexScreen extends Screen {
    private enum Category {
        COMBAT, VISUALS, HUD, UTILITIES, SETTINGS
    }

    private Category currentCategory = Category.HUD;

    private static final int PURPLE = 0xFF913CFF;
    private static final int PANEL = 0xF20B0918;
    private static final int CARD = 0xD9161028;
    private static final int BORDER = 0xFF38205C;
    private static final int TEXT = 0xFFF5EEFF;
    private static final int MUTED = 0xFFB4A2D5;

    private int panelX, panelY, panelW, panelH;
    private int settingsFeature = -1;
    private int activeSlider = -1;

    public VortexScreen() {
        super(Text.literal("Vortex Visuals"));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        layout();
        if (settingsFeature >= 0) {
            if (button == 1) { settingsFeature = -1; activeSlider = -1; return true; }
            if (button == 0) {
                int sx = panelX + 166, sw = panelW - 198;
                for (int slider = 0; slider < 2; slider++) {
                    int yy = panelY + 126 + slider * 64;
                    if (inside(mouseX, mouseY, sx, yy - 3, sw, 14)) {
                        activeSlider = slider; updateSlider(mouseX, sx, sw, slider); return true;
                    }
                }
                if (inside(mouseX, mouseY, panelX + panelW - 45, panelY + 10, 34, 25)) { settingsFeature = -1; return true; }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (button == 1) {
            int x = panelX + 140, y = panelY + 67, w = panelW - 154;
            for (int i = 0; i < getFeatureCount(); i++) {
                int cy = y + i * 38;
                if (inside(mouseX, mouseY, x, cy, w, 33)) { settingsFeature = i; return true; }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int sideX = panelX + 12;
        int sideW = 116;
        int sideY = panelY + 88;

        Category[] categories = Category.values();

        for (int i = 0; i < categories.length; i++) {
            int y = sideY + i * 29;
            if (inside(mouseX, mouseY, sideX, y, sideW, 25)) {
                currentCategory = categories[i];
                return true;
            }
        }

        int x = panelX + 140;
        int y = panelY + 67;
        int w = panelW - 154;
        int h = 33;
        int gap = 5;

        int count = getFeatureCount();
        for (int i = 0; i < count; i++) {
            int cy = y + i * (h + gap);
            if (inside(mouseX, mouseY, x, cy, w, h)) {
                toggleFeature(i);
                HudRenderer.saveConfig();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (settingsFeature >= 0 && activeSlider >= 0 && button == 0) {
            updateSlider(mouseX, panelX + 166, panelW - 198, activeSlider); return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && activeSlider >= 0) { activeSlider = -1; HudRenderer.saveConfig(); return true; }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void updateSlider(double mouseX, int x, int w, int slider) {
        int value = (int)Math.round(Math.max(0, Math.min(1, (mouseX - x) / (double)Math.max(1, w))) * 600);
        if (currentCategory != Category.HUD) return;
        switch (settingsFeature) {
            case 0 -> { if (slider == 0) Modules.watermarkX = value; else Modules.watermarkY = value; }
            case 1 -> { if (slider == 0) Modules.playerInfoX = value; else Modules.playerInfoY = value; }
            case 2 -> { if (slider == 0) Modules.armorX = value; else Modules.armorY = value; }
            case 3 -> { if (slider == 0) Modules.potionsX = value; else Modules.potionsY = value; }
            case 4 -> { if (slider == 0) Modules.cooldownsX = value; else Modules.cooldownsY = value; }
            case 5 -> { if (slider == 0) Modules.keybindsX = value; else Modules.keybindsY = value; }
            case 6 -> { if (slider == 0) Modules.inventoryX = value; else Modules.inventoryY = value; }
            default -> { }
        }
    }

    private void layout() {
        panelW = Math.min(560, width - 16);
        panelH = Math.min(390, height - 16);
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
    }

    private boolean inside(double mx, double my,
                           int x, int y, int w, int h) {
        return mx >= x && mx <= x + w
                && my >= y && my <= y + h;
    }

    private int getFeatureCount() {
        return switch (currentCategory) {
            case COMBAT -> 5;
            case VISUALS -> 2;
            case HUD -> 7;
            case UTILITIES, SETTINGS -> 0;
        };
    }

    private void toggleFeature(int i) {
        switch (currentCategory) {
            case COMBAT -> {
                switch (i) {
                    case 0 -> Modules.deathEffect = !Modules.deathEffect;
                    case 1 -> Modules.killEffect = !Modules.killEffect;
                    case 2 -> Modules.hitSound = !Modules.hitSound;
                    case 3 -> Modules.deathSound = !Modules.deathSound;
                    case 4 -> Modules.targetEsp = !Modules.targetEsp;
                }
            }
            case VISUALS -> {
                switch (i) {
                    case 0 -> Modules.fullBright = !Modules.fullBright;
                    case 1 -> Modules.particles = !Modules.particles;
                }
            }
            case HUD -> {
                switch (i) {
                    case 0 -> Modules.watermarkEnabled = !Modules.watermarkEnabled;
                    case 1 -> Modules.playerInfo = !Modules.playerInfo;
                    case 2 -> Modules.armorStatusEnabled = !Modules.armorStatusEnabled;
                    case 3 -> Modules.potionsEnabled = !Modules.potionsEnabled;
                    case 4 -> Modules.cooldownsEnabled = !Modules.cooldownsEnabled;
                    case 5 -> Modules.keybindsEnabled = !Modules.keybindsEnabled;
                    case 6 -> Modules.inventoryHudEnabled = !Modules.inventoryHudEnabled;
                }
            }
            default -> {
            }
        }
    }

    @Override
    public void render(DrawContext c, int mouseX, int mouseY, float delta) {
        layout();

        c.fill(0, 0, width, height, 0xA8000010);

        // Main window and sidebar.
        c.fill(panelX, panelY, panelX + panelW, panelY + panelH, PANEL);
        c.drawBorder(panelX, panelY, panelW, panelH, PURPLE);
        c.fill(panelX + 1, panelY + 1,
                panelX + 128, panelY + panelH - 1, 0xE6090713);
        c.fill(panelX + 128, panelY + 8,
                panelX + 129, panelY + panelH - 8, BORDER);

        // Branding.
        c.fill(panelX + 12, panelY + 13,
                panelX + 32, panelY + 33, PURPLE);
        c.drawTextWithShadow(textRenderer, "V",
                panelX + 18, panelY + 18, TEXT);
        c.drawTextWithShadow(textRenderer, "VORTEX",
                panelX + 39, panelY + 12, TEXT);
        c.drawTextWithShadow(textRenderer, "V I S U A L S",
                panelX + 39, panelY + 25, PURPLE);

        c.drawTextWithShadow(textRenderer, "1.0.0",
                panelX + 14, panelY + panelH - 20, MUTED);

        // Category navigation.
        String[] names = {
                "Combat", "Visuals", "HUD",
                "World", "Settings"
        };

        int sideX = panelX + 12;
        int sideY = panelY + 88;

        for (int i = 0; i < Category.values().length; i++) {
            int y = sideY + i * 29;
            boolean active = currentCategory == Category.values()[i];
            boolean hover = inside(mouseX, mouseY, sideX, y, 116, 25);

            if (active) {
                c.fill(sideX, y, sideX + 116, y + 25, 0xFF35145A);
                c.fill(sideX, y + 3, sideX + 2, y + 22, PURPLE);
                c.drawBorder(sideX, y, 116, 25, 0xFF7734C9);
            } else if (hover) {
                c.fill(sideX, y, sideX + 116, y + 25, CARD);
            }

            c.drawTextWithShadow(textRenderer, names[i],
                    sideX + 10, y + 9,
                    active ? TEXT : MUTED);
        }

        // Header.
        String title = switch (currentCategory) {
            case COMBAT -> "COMBAT";
            case VISUALS -> "VISUALS";
            case HUD -> "HUD EDITOR";
            case UTILITIES -> "UTILITIES";
            case SETTINGS -> "SETTINGS";
        };

        String subtitle = switch (currentCategory) {
            case COMBAT -> "Combat modules";
            case VISUALS -> "Visual enhancements";
            case HUD -> "Customize your interface";
            case UTILITIES -> "Tools and helpers";
            case SETTINGS -> "Client preferences";
        };

        int contentX = panelX + 140;
        c.drawTextWithShadow(textRenderer, title,
                contentX, panelY + 16, TEXT);
        c.drawTextWithShadow(textRenderer, subtitle,
                contentX, panelY + 31, MUTED);

        c.drawTextWithShadow(textRenderer, "—",
                panelX + panelW - 37, panelY + 16, MUTED);
        c.drawTextWithShadow(textRenderer, "X",
                panelX + panelW - 20, panelY + 16, PURPLE);

        if (settingsFeature >= 0) {
            renderModuleSettings(c);
            super.render(c, mouseX, mouseY, delta);
            return;
        }

        // Feature cards.
        int startY = panelY + 67;
        int cardW = panelW - 154;
        int cardH = 33;
        int gap = 5;

        int count = getFeatureCount();

        if (count == 0) {
            c.fill(contentX, startY, contentX + cardW,
                    startY + 64, CARD);
            c.drawBorder(contentX, startY, cardW, 64, BORDER);
            c.drawTextWithShadow(textRenderer,
                    "No configurable modules here yet",
                    contentX + 12, startY + 14, TEXT);
            c.drawTextWithShadow(textRenderer,
                    "More features can be added later",
                    contentX + 12, startY + 31, MUTED);
        }

        for (int i = 0; i < count; i++) {
            int y = startY + i * (cardH + gap);
            Feature feature = getFeature(i);
            boolean hover = inside(mouseX, mouseY,
                    contentX, y, cardW, cardH);

            c.fill(contentX, y, contentX + cardW,
                    y + cardH, hover ? 0xFF211533 : CARD);
            c.drawBorder(contentX, y, cardW, cardH,
                    hover ? PURPLE : BORDER);

            c.drawTextWithShadow(textRenderer, feature.name,
                    contentX + 9, y + 5, TEXT);
            c.drawTextWithShadow(textRenderer, feature.description,
                    contentX + 9, y + 18, MUTED);

            drawToggle(c, contentX + cardW - 38,
                    y + 9, feature.enabled);
        }

        super.render(c, mouseX, mouseY, delta);
    }

    private void renderModuleSettings(DrawContext c) {
        if (currentCategory != Category.HUD || settingsFeature < 0 || settingsFeature >= getFeatureCount()) {
            c.drawTextWithShadow(textRenderer, "No settings available for this module yet",
                    panelX + 152, panelY + 88, MUTED);
            return;
        }

        Feature feature = getFeature(settingsFeature);
        c.drawTextWithShadow(textRenderer, feature.name + " Settings",
                panelX + 152, panelY + 70, TEXT);
        c.drawTextWithShadow(textRenderer, "Drag the sliders to move this HUD",
                panelX + 152, panelY + 88, MUTED);

        int x = panelX + 166;
        int w = panelW - 198;
        int yX = panelY + 126;
        int yY = panelY + 190;
        int vx = getHudCoordinate(settingsFeature, 0);
        int vy = getHudCoordinate(settingsFeature, 1);
        drawCoordinateSlider(c, "X", x, yX, w, vx);
        drawCoordinateSlider(c, "Y", x, yY, w, vy);
        c.drawTextWithShadow(textRenderer, "Right-click outside the sliders or press X to close",
                panelX + 152, panelY + panelH - 28, MUTED);
    }

    private int getHudCoordinate(int feature, int axis) {
        return switch (feature) {
            case 0 -> axis == 0 ? Modules.watermarkX : Modules.watermarkY;
            case 1 -> axis == 0 ? Modules.playerInfoX : Modules.playerInfoY;
            case 2 -> axis == 0 ? Modules.armorX : Modules.armorY;
            case 3 -> axis == 0 ? Modules.potionsX : Modules.potionsY;
            case 4 -> axis == 0 ? Modules.cooldownsX : Modules.cooldownsY;
            case 5 -> axis == 0 ? Modules.keybindsX : Modules.keybindsY;
            case 6 -> axis == 0 ? Modules.inventoryX : Modules.inventoryY;
            default -> 0;
        };
    }

    private void drawCoordinateSlider(DrawContext c, String label, int x, int y, int w, int value) {
        c.drawTextWithShadow(textRenderer, label + "  " + value, panelX + 152, y - 18, TEXT);
        c.fill(x, y, x + w, y + 8, 0xFF30243F);
        c.fill(x, y, x + (int)(w * Math.max(0, Math.min(600, value)) / 600.0), y + 8, PURPLE);
        int knobX = x + (int)(w * Math.max(0, Math.min(600, value)) / 600.0);
        c.fill(knobX - 3, y - 3, knobX + 4, y + 11, 0xFFF5EEFF);
    }

    private record Feature(String name, String description,
                           boolean enabled) {}

    private Feature getFeature(int i) {
        return switch (currentCategory) {
            case COMBAT -> switch (i) {
                case 0 -> new Feature("Death Effect", "Effect on death", Modules.deathEffect);
                case 1 -> new Feature("Kill Effect", "Effect after a kill", Modules.killEffect);
                case 2 -> new Feature("Hit Sound", "Sound on hit", Modules.hitSound);
                case 3 -> new Feature("Death Sound", "Sound on death", Modules.deathSound);
                default -> new Feature("Target ESP", "Highlight target", Modules.targetEsp);
            };
            case VISUALS -> switch (i) {
                case 0 -> new Feature("Full Bright", "Brighter world", Modules.fullBright);
                default -> new Feature("Particles", "Visual particles", Modules.particles);
            };
            case HUD -> switch (i) {
                case 0 -> new Feature("Watermark", "Name, FPS, ping and time", Modules.watermarkEnabled);
                case 1 -> new Feature("Target HUD", "Target name and health", Modules.playerInfo);
                case 2 -> new Feature("Armor HUD", "Armor status", Modules.armorStatusEnabled);
                case 3 -> new Feature("Potions HUD", "Active effects and timers", Modules.potionsEnabled);
                case 4 -> new Feature("Cooldown HUD", "Item cooldown indicators", Modules.cooldownsEnabled);
                case 5 -> new Feature("Keybinds HUD", "Active keybinds", Modules.keybindsEnabled);
                case 6 -> new Feature("Inventory HUD", "Inventory preview", Modules.inventoryHudEnabled);
                default -> new Feature("Unavailable", "No module", false);
            };
            default -> new Feature("Unavailable", "No module", false);
        };
    }

    private void drawToggle(DrawContext c, int x, int y, boolean enabled) {
        int color = enabled ? PURPLE : 0xFF393047;
        c.fill(x, y, x + 28, y + 15, color);
        c.drawBorder(x, y, 28, 15, enabled ? 0xFFB77AFF : BORDER);

        int knobX = enabled ? x + 15 : x + 2;
        c.fill(knobX, y + 2, knobX + 11, y + 13, 0xFFFFFFFF);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
