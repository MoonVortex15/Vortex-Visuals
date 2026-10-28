
package net.vortex.visuals;

import net.minecraft.client.gui.DrawContext;
import net.vortex.visuals.hud.HudRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class VortexScreen extends Screen {
    private enum Category {
        COMBAT, VISUALS, HUD, UTILITIES, SETTINGS
    }

    private static Category lastCategory = Category.VISUALS;
    private Category currentCategory = lastCategory;

    private static final int PURPLE = 0xFF913CFF;
    private static final int PANEL = 0xF20B0918;
    private static final int CARD = 0xD9161028;
    private static final int BORDER = 0xFF38205C;
    private static final int TEXT = 0xFFF5EEFF;
    private static final int MUTED = 0xFFB4A2D5;

    private int panelX, panelY, panelW, panelH;
    private boolean editingBlockOverlay = false;
    private boolean editingTargetEsp = false;
    private boolean editingCrosshair = false;
    private boolean editingCustomHand = false;
    private int editingHudIndex = -1;
    private int activeSlider = -1;

    public VortexScreen() {
        super(Text.literal("Vortex Visuals"));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        layout();
        if (editingHudIndex >= 0) {
            if (button == 1) { editingHudIndex = -1; return true; }
            if (button == 0) {
                for (int i = 0; i < 2; i++) {
                    int sy = panelY + 92 + i * 38;
                    if (inside(mouseX, mouseY, panelX + 170, sy, panelW - 205, 14)) {
                        activeSlider = i;
                        updateHudSlider(i, mouseX);
                        HudRenderer.saveConfig();
                        return true;
                    }
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (editingCustomHand) {
            if (button == 1 && inside(mouseX, mouseY, panelX + panelW - 30, panelY + 8, 22, 22)) { editingCustomHand = false; return true; }
            if (button == 0) {
                for (int i = 0; i < 6; i++) {
                    int sy = panelY + 78 + i * 34;
                    if (inside(mouseX, mouseY, panelX + 170, sy, panelW - 205, 14)) { activeSlider = i; updateCustomHandSlider(i, mouseX); HudRenderer.saveConfig(); return true; }
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (editingTargetEsp || editingCrosshair) {
            if (button == 1 && inside(mouseX, mouseY, panelX + panelW - 30, panelY + 8, 22, 22)) {
                editingTargetEsp = false;
                editingCrosshair = false;
                return true;
            }
            if (button == 0) {
                int count = editingTargetEsp ? 5 : 7;
                for (int i = 0; i < count; i++) {
                    int sy = panelY + 78 + i * 34;
                    if (inside(mouseX, mouseY, panelX + 170, sy, panelW - 205, 14)) {
                        activeSlider = i;
                        updateGeneralSlider(i, mouseX);
                        HudRenderer.saveConfig();
                        return true;
                    }
                }
                if (editingCrosshair && inside(mouseX, mouseY, panelX + 140, panelY + 52, panelW - 154, 24)) {
                    Modules.crosshairCenterDot = !Modules.crosshairCenterDot;
                    HudRenderer.saveConfig();
                    return true;
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (editingBlockOverlay) {
            if (button == 1 && inside(mouseX, mouseY, panelX + panelW - 30, panelY + 8, 22, 22)) {
                editingBlockOverlay = false;
                return true;
            }
            if (button == 0) {
                int sliderY = panelY + 100;
                for (int i = 0; i < 8; i++) {
                    int sy = sliderY + i * 31;
                    if (inside(mouseX, mouseY, panelX + 170, sy, panelW - 205, 14)) {
                        activeSlider = i;
                        updateBlockSlider(i, mouseX);
                        HudRenderer.saveConfig();
                        return true;
                    }
                }
                if (inside(mouseX, mouseY, panelX + 140, panelY + 58, panelW - 154, 30)) {
                    Modules.blockOverlayEnabled = !Modules.blockOverlayEnabled;
                    HudRenderer.saveConfig();
                    return true;
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (button != 0 && button != 1) return super.mouseClicked(mouseX, mouseY, button);

        int sideX = panelX + 12;
        int sideW = 116;
        int sideY = panelY + 88;
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            int y = sideY + i * 29;
            if (inside(mouseX, mouseY, sideX, y, sideW, 25)) {
                currentCategory = categories[i];
                lastCategory = currentCategory;
                return true;
            }
        }

        int x = panelX + 140;
        int y = panelY + 67;
        int w = currentCategory == Category.VISUALS ? (panelW - 159) / 2 : panelW - 154;
        int h = currentCategory == Category.VISUALS ? 43 : 33;
        int gap = currentCategory == Category.VISUALS ? 7 : 5;
        int count = getFeatureCount();
        for (int i = 0; i < count; i++) {
            int col = currentCategory == Category.VISUALS ? i % 2 : 0;
            int row = currentCategory == Category.VISUALS ? i / 2 : i;
            int cx = x + col * (w + gap);
            int cy = y + row * (h + gap);
            if (inside(mouseX, mouseY, cx, cy, w, h)) {
                if (button == 1 && currentCategory == Category.HUD) {
                    editingHudIndex = i;
                    return true;
                }
                if (button == 1 && currentCategory == Category.VISUALS && i == 3) {
                    editingBlockOverlay = true;
                    return true;
                }
                if (button == 1 && currentCategory == Category.VISUALS && i == 2) {
                    editingCrosshair = true;
                    return true;
                }
                if (button == 1 && currentCategory == Category.VISUALS && i == 9) { editingCustomHand = true; return true; }
                if (button == 1 && currentCategory == Category.COMBAT && i == 4) {
                    editingTargetEsp = true;
                    return true;
                }
                if (button == 1) return super.mouseClicked(mouseX, mouseY, button);
                toggleFeature(i);
                HudRenderer.saveConfig();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        activeSlider = -1;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (editingBlockOverlay && activeSlider >= 0 && button == 0) {
            updateBlockSlider(activeSlider, mouseX);
            return true;
        }
        if (editingCustomHand && activeSlider >= 0 && button == 0) { updateCustomHandSlider(activeSlider, mouseX); return true; }
        if ((editingTargetEsp || editingCrosshair) && activeSlider >= 0 && button == 0) {
            updateGeneralSlider(activeSlider, mouseX);
            return true;
        }
        if (editingHudIndex >= 0 && activeSlider >= 0 && button == 0) {
            updateHudSlider(activeSlider, mouseX);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void updateHudSlider(int index, double mouseX) {
        int x = panelX + 170;
        int w = panelW - 205;
        int value = (int)Math.round(Math.max(0, Math.min(1, (mouseX - x) / (double)w)) * 800);
        switch (editingHudIndex) {
            case 0 -> { if (index == 0) Modules.watermarkX = value; else Modules.watermarkY = value; }
            case 1 -> { if (index == 0) Modules.playerInfoX = value; else Modules.playerInfoY = value; }
            case 2 -> { if (index == 0) Modules.armorX = value; else Modules.armorY = value; }
            case 3 -> { if (index == 0) Modules.potionsX = value; else Modules.potionsY = value; }
            case 4 -> { if (index == 0) Modules.cooldownsX = value; else Modules.cooldownsY = value; }
            case 5 -> { if (index == 0) Modules.keybindsX = value; else Modules.keybindsY = value; }
            case 6 -> { if (index == 0) Modules.inventoryX = value; else Modules.inventoryY = value; }
        }
        HudRenderer.saveConfig();
    }

    private void updateBlockSlider(int index, double mouseX) {
        int x = panelX + 170;
        int w = panelW - 205;
        int value = (int)Math.round(Math.max(0, Math.min(1, (mouseX - x) / (double)w)) * (index == 6 ? 255 : index == 7 ? 6 : 255));
        switch (index) {
            case 0 -> Modules.blockOverlayFillRed = value;
            case 1 -> Modules.blockOverlayFillGreen = value;
            case 2 -> Modules.blockOverlayFillBlue = value;
            case 3 -> Modules.blockOverlayOutlineRed = value;
            case 4 -> Modules.blockOverlayOutlineGreen = value;
            case 5 -> Modules.blockOverlayOutlineBlue = value;
            case 6 -> Modules.blockOverlayAlpha = value;
            case 7 -> Modules.blockOverlayLineWidth = Math.max(1, value);
        }
        HudRenderer.saveConfig();
    }

    private void updateCustomHandSlider(int index, double mouseX) {
        int x = panelX + 170;
        int w = panelW - 205;
        double fraction = Math.max(0, Math.min(1, (mouseX - x) / (double) w));
        int value = index == 5
                ? (int)Math.round(fraction * 360.0) - 180
                : (int)Math.round(fraction * (index == 3 ? 175 : index == 4 ? 360 : 100)) - (index < 3 ? 50 : 0);
        switch (index) {
            case 0 -> Modules.handOffsetX = value;
            case 1 -> Modules.handOffsetY = value;
            case 2 -> Modules.handOffsetZ = value;
            case 3 -> Modules.handScalePercent = Math.max(25, Math.min(200, value + 25));
            case 4 -> Modules.handRotation = value;
            case 5 -> Modules.handAngle = value;
        }
        HudRenderer.saveConfig();
    }

    private void updateGeneralSlider(int index, double mouseX) {
        int x = panelX + 170;
        int w = panelW - 205;
        double fraction = Math.max(0, Math.min(1, (mouseX - x) / (double) w));
        if (editingTargetEsp) {
            int value = (int) Math.round(fraction * (index == 3 ? 128 : index == 4 ? 20 : 255));
            switch (index) {
                case 0 -> Modules.targetEspRed = value;
                case 1 -> Modules.targetEspGreen = value;
                case 2 -> Modules.targetEspBlue = value;
                case 3 -> Modules.targetEspRange = Math.max(8, value);
                case 4 -> Modules.targetEspExpand = value;
            }
        } else {
            int value = (int) Math.round(fraction * (index == 3 ? 8 : index == 4 ? 20 : 255));
            switch (index) {
                case 0 -> Modules.crosshairRed = value;
                case 1 -> Modules.crosshairGreen = value;
                case 2 -> Modules.crosshairBlue = value;
                case 3 -> Modules.crosshairThickness = Math.max(1, value);
                case 4 -> Modules.crosshairGap = value;
                case 5 -> Modules.crosshairLength = Math.max(1, value);
                case 6 -> Modules.crosshairAlpha = value;
            }
        }
        HudRenderer.saveConfig();
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
            case COMBAT -> 6;
            case VISUALS -> 10;
            case HUD -> 7;
            case UTILITIES -> 4;
            case SETTINGS -> 0;
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
                    case 5 -> Modules.damageIndicators = !Modules.damageIndicators;
                }
            }
            case VISUALS -> {
                switch (i) {
                    case 0 -> Modules.fullBright = !Modules.fullBright;
                    case 1 -> Modules.particles = !Modules.particles;
                    case 2 -> Modules.customCrosshairEnabled = !Modules.customCrosshairEnabled;
                    case 3 -> Modules.blockOverlayEnabled = !Modules.blockOverlayEnabled;
                    case 4 -> Modules.timeChanger = !Modules.timeChanger;
                    case 5 -> Modules.trails = !Modules.trails;
                    case 6 -> Modules.enchantmentEffects = !Modules.enchantmentEffects;
                    case 7 -> Modules.hitBubbles = !Modules.hitBubbles;
                    case 8 -> Modules.trajectories = !Modules.trajectories;
                    case 9 -> Modules.customHand = !Modules.customHand;
                }
            }
            case UTILITIES -> {
                switch (i) {
                    case 0 -> Modules.shieldTotemCounter = !Modules.shieldTotemCounter;
                    case 1 -> Modules.waypoints = !Modules.waypoints;
                    case 2 -> Modules.damageIndicators = !Modules.damageIndicators;
                    case 3 -> Modules.trajectories = !Modules.trajectories;
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

        if (!(editingTargetEsp || editingCrosshair || editingBlockOverlay || editingCustomHand || editingHudIndex >= 0))
            c.fill(0, 0, width, height, 0xA8000010);
        if (editingHudIndex >= 0) {
            c.fill(panelX, panelY, panelX + panelW, panelY + 150, PANEL);
            c.drawBorder(panelX, panelY, panelW, 150, PURPLE);
            String[] names = {"WATERMARK", "TARGET HUD", "ARMOR HUD", "POTIONS HUD", "COOLDOWNS HUD", "KEYBINDS HUD", "INVENTORY HUD"};
            int px = switch (editingHudIndex) { case 0 -> Modules.watermarkX; case 1 -> Modules.playerInfoX; case 2 -> Modules.armorX; case 3 -> Modules.potionsX; case 4 -> Modules.cooldownsX; case 5 -> Modules.keybindsX; default -> Modules.inventoryX; };
            int py = switch (editingHudIndex) { case 0 -> Modules.watermarkY; case 1 -> Modules.playerInfoY; case 2 -> Modules.armorY; case 3 -> Modules.potionsY; case 4 -> Modules.cooldownsY; case 5 -> Modules.keybindsY; default -> Modules.inventoryY; };
            c.drawTextWithShadow(textRenderer, names[editingHudIndex] + " SETTINGS", panelX + 18, panelY + 18, TEXT);
            c.drawTextWithShadow(textRenderer, "RMB: back", panelX + panelW - 88, panelY + 18, PURPLE);
            int[] vals = {px, py}; String[] labels = {"Position X", "Position Y"};
            for (int i = 0; i < 2; i++) {
                int sy = panelY + 92 + i * 38;
                c.drawTextWithShadow(textRenderer, labels[i] + "  " + vals[i], panelX + 18, sy + 2, TEXT);
                int sx = panelX + 170, sw = panelW - 205;
                c.fill(sx, sy + 3, sx + sw, sy + 11, 0xFF393047);
                int filled = (int)(sw * (vals[i] / 800.0));
                c.fill(sx, sy + 3, sx + filled, sy + 11, PURPLE);
                c.fill(sx + filled - 2, sy, sx + filled + 2, sy + 14, TEXT);
            }
            super.render(c, mouseX, mouseY, delta);
            return;
        }
        if (editingCustomHand) {
            c.fill(panelX, panelY, panelX + panelW, panelY + panelH, PANEL);
            c.drawBorder(panelX, panelY, panelW, panelH, PURPLE);
            c.drawTextWithShadow(textRenderer, "CUSTOM HAND SETTINGS", panelX + 18, panelY + 18, TEXT);
            c.drawTextWithShadow(textRenderer, "RMB: back", panelX + panelW - 88, panelY + 18, PURPLE);
            String[] labels = {"Offset X", "Offset Y", "Offset Z", "Scale %", "Rotation Z", "Angle X (degrees)"};
            int[] values = {Modules.handOffsetX, Modules.handOffsetY, Modules.handOffsetZ, Modules.handScalePercent, Modules.handRotation, Modules.handAngle};
            for (int i = 0; i < labels.length; i++) {
                int sy = panelY + 78 + i * 34;
                c.drawTextWithShadow(textRenderer, labels[i] + "  " + values[i], panelX + 18, sy + 2, TEXT);
                int sx = panelX + 170, sw = panelW - 205;
                c.fill(sx, sy + 3, sx + sw, sy + 11, 0xFF393047);
                int min = i < 3 ? -50 : i == 3 ? 25 : i == 4 ? 0 : -180;
                int max = i < 3 ? 50 : i == 3 ? 200 : i == 4 ? 360 : 180;
                int normalized = Math.max(0, Math.min(100, (values[i] - min) * 100 / Math.max(1, max - min)));
                int filled = sw * normalized / 100;
                c.fill(sx, sy + 3, sx + filled, sy + 11, PURPLE);
                c.fill(sx + filled - 2, sy, sx + filled + 2, sy + 14, TEXT);
            }
            super.render(c, mouseX, mouseY, delta);
            return;
        }
        if (editingTargetEsp || editingCrosshair) {
            c.fill(panelX, panelY, panelX + panelW, panelY + panelH, PANEL);
            c.drawBorder(panelX, panelY, panelW, panelH, PURPLE);
            String title = editingTargetEsp ? "TARGET ESP SETTINGS" : "CROSSHAIR SETTINGS";
            c.drawTextWithShadow(textRenderer, title, panelX + 18, panelY + 18, TEXT);
            c.drawTextWithShadow(textRenderer, "RMB: back", panelX + panelW - 88, panelY + 18, PURPLE);
            String[] labels = editingTargetEsp
                    ? new String[]{"Red", "Green", "Blue", "Range", "Box expansion"}
                    : new String[]{"Red", "Green", "Blue", "Thickness", "Gap", "Length", "Alpha"};
            int[] values = editingTargetEsp
                    ? new int[]{Modules.targetEspRed, Modules.targetEspGreen, Modules.targetEspBlue, Modules.targetEspRange, Modules.targetEspExpand}
                    : new int[]{Modules.crosshairRed, Modules.crosshairGreen, Modules.crosshairBlue, Modules.crosshairThickness, Modules.crosshairGap, Modules.crosshairLength, Modules.crosshairAlpha};
            for (int i = 0; i < labels.length; i++) {
                int sy = panelY + 78 + i * 34;
                c.drawTextWithShadow(textRenderer, labels[i] + "  " + values[i], panelX + 18, sy + 2, TEXT);
                int sx = panelX + 170, sw = panelW - 205;
                c.fill(sx, sy + 3, sx + sw, sy + 11, 0xFF393047);
                int max = editingTargetEsp ? (i == 3 ? 128 : i == 4 ? 20 : 255) : (i == 3 ? 8 : i == 4 ? 20 : 255);
                int filled = (int) (sw * (values[i] / (double) Math.max(1, max)));
                c.fill(sx, sy + 3, sx + filled, sy + 11, PURPLE);
                c.fill(sx + filled - 2, sy, sx + filled + 2, sy + 14, TEXT);
            }
            if (editingCrosshair) {
                c.fill(panelX + 140, panelY + panelH - 38, panelX + panelW - 14, panelY + panelH - 12, CARD);
                c.drawTextWithShadow(textRenderer, "Center dot", panelX + 150, panelY + panelH - 30, TEXT);
                drawToggle(c, panelX + panelW - 54, panelY + panelH - 33, Modules.crosshairCenterDot);
            }
            super.render(c, mouseX, mouseY, delta);
            return;
        }
        if (editingBlockOverlay) {
            c.fill(panelX, panelY, panelX + panelW, panelY + panelH, PANEL);
            c.drawBorder(panelX, panelY, panelW, panelH, PURPLE);
            c.drawTextWithShadow(textRenderer, "BLOCK OVERLAY SETTINGS", panelX + 18, panelY + 18, TEXT);
            c.drawTextWithShadow(textRenderer, "X", panelX + panelW - 20, panelY + 16, PURPLE);
            c.fill(panelX + 140, panelY + 58, panelX + panelW - 14, panelY + 88, CARD);
            c.drawTextWithShadow(textRenderer, "Enabled", panelX + 150, panelY + 68, TEXT);
            drawToggle(c, panelX + panelW - 54, panelY + 65, Modules.blockOverlayEnabled);
            String[] labels = {"Fill red", "Fill green", "Fill blue", "Outline red", "Outline green", "Outline blue", "Fill opacity", "Line width"};
            int[] values = {Modules.blockOverlayFillRed, Modules.blockOverlayFillGreen, Modules.blockOverlayFillBlue, Modules.blockOverlayOutlineRed, Modules.blockOverlayOutlineGreen, Modules.blockOverlayOutlineBlue, Modules.blockOverlayAlpha, Modules.blockOverlayLineWidth};
            for (int i = 0; i < labels.length; i++) {
                int sy = panelY + 100 + i * 31;
                c.drawTextWithShadow(textRenderer, labels[i] + "  " + values[i], panelX + 18, sy + 2, TEXT);
                int sx = panelX + 170, sw = panelW - 205;
                c.fill(sx, sy + 3, sx + sw, sy + 11, 0xFF393047);
                int max = i == 7 ? 6 : 255;
                int filled = (int)(sw * (values[i] / (double)max));
                c.fill(sx, sy + 3, sx + filled, sy + 11, PURPLE);
                c.fill(sx + filled - 2, sy, sx + filled + 2, sy + 14, TEXT);
            }
            super.render(c, mouseX, mouseY, delta);
            return;
        }

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

        // Feature cards.
        int startY = panelY + 67;
        int cardW = currentCategory == Category.VISUALS ? (panelW - 159) / 2 : panelW - 154;
        int cardH = currentCategory == Category.VISUALS ? 43 : 33;
        int gap = currentCategory == Category.VISUALS ? 7 : 5;

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
            int col = currentCategory == Category.VISUALS ? i % 2 : 0;
            int row = currentCategory == Category.VISUALS ? i / 2 : i;
            int cardX = contentX + col * (cardW + gap);
            int y = startY + row * (cardH + gap);
            Feature feature = getFeature(i);
            boolean hover = inside(mouseX, mouseY,
                    cardX, y, cardW, cardH);

            c.fill(cardX, y, cardX + cardW,
                    y + cardH, hover ? 0xFF211533 : CARD);
            c.drawBorder(cardX, y, cardW, cardH,
                    hover ? PURPLE : BORDER);

            c.drawTextWithShadow(textRenderer, feature.name,
                    cardX + 9, y + 5, TEXT);
            c.drawTextWithShadow(textRenderer, feature.description,
                    cardX + 9, y + 19, MUTED);

            drawToggle(c, cardX + cardW - 38,
                    y + 9, feature.enabled);
        }

        super.render(c, mouseX, mouseY, delta);
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
                default -> new Feature("Target ESP", "All nearby players (RMB settings)", Modules.targetEsp);
            };
            case VISUALS -> switch (i) {
                case 0 -> new Feature("Full Bright", "Brighter world", Modules.fullBright);
                case 1 -> new Feature("Particles", "Visual particles", Modules.particles);
                case 2 -> new Feature("Crosshair", "Custom crosshair (RMB settings)", Modules.customCrosshairEnabled);
                case 3 -> new Feature("Block Overlay", "Block outline and fill settings (RMB)", Modules.blockOverlayEnabled);
                case 4 -> new Feature("Time Changer", "Freeze world time to chosen hour", Modules.timeChanger);
                case 5 -> new Feature("Trails", "Entity trail toggle", Modules.trails);
                case 6 -> new Feature("Enchantment Effects", "Glint color settings planned", Modules.enchantmentEffects);
                case 7 -> new Feature("Hit Bubbles", "Hit effect toggle", Modules.hitBubbles);
                case 8 -> new Feature("Trajectories", "Arrow / pearl / potion toggle", Modules.trajectories);
                default -> new Feature("Custom Hand", "Position, rotation and scale", Modules.customHand);
            };
            case UTILITIES -> switch (i) {
                case 0 -> new Feature("Shield & Totem Counter", "Counts inventory items", Modules.shieldTotemCounter);
                case 1 -> new Feature("Waypoints", "Coordinate marker HUD", Modules.waypoints);
                case 2 -> new Feature("Damage Indicators", "Damage number toggle", Modules.damageIndicators);
                default -> new Feature("Trajectories", "Arrow / pearl / potion toggle", Modules.trajectories);
            };
            case HUD -> switch (i) {
                case 0 -> new Feature("Watermark", "Name, FPS, ping and time", Modules.watermarkEnabled);
                case 1 -> new Feature("Target HUD", "Target name and health", Modules.playerInfo);
                case 2 -> new Feature("Armor HUD", "Armor status", Modules.armorStatusEnabled);
                case 3 -> new Feature("Potions HUD", "Active effects and timers", Modules.potionsEnabled);
                case 4 -> new Feature("Cooldown HUD", "Item cooldown indicators", Modules.cooldownsEnabled);
                case 5 -> new Feature("Keybinds HUD", "Active keybinds", Modules.keybindsEnabled);
                default -> new Feature("Inventory HUD", "Inventory preview", Modules.inventoryHudEnabled);
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
