
package net.vortex.visuals;

public class Modules {
    // Бой
    public static boolean deathEffect = false;
    public static boolean killEffect = false;
    public static boolean hitSound = false;
    public static boolean deathSound = false;
    public static boolean targetEsp = false;
    // Target ESP settings
    public static int targetEspRed = 145;
    public static int targetEspGreen = 60;
    public static int targetEspBlue = 255;
    public static int targetEspRange = 64;
    public static int targetEspExpand = 4; // hundredths of a block
    public static boolean autoShift = false;

    // Визуалы
    public static boolean chinaHit = false;
    public static boolean customHand = false;
    public static boolean fullBright = false;
    public static boolean hitColor = false;
    public static int hitColorRgb = 0xFF5555;
    public static boolean hitboxCustomizer = false;
    public static boolean jumpCircles = false;
    public static boolean particles = false;
    public static boolean renderTweaks = false;

    // Block Overlay customization
    public static boolean blockOverlayEnabled = false;
    public static int blockOverlayFillRed = 145;
    public static int blockOverlayFillGreen = 60;
    public static int blockOverlayFillBlue = 255;
    public static int blockOverlayOutlineRed = 210;
    public static int blockOverlayOutlineGreen = 160;
    public static int blockOverlayOutlineBlue = 255;
    public static int blockOverlayAlpha = 70;
    public static int blockOverlayLineWidth = 2;

    // Custom crosshair
    public static boolean customCrosshairEnabled = false;
    public static int crosshairThickness = 2;
    public static int crosshairGap = 4;
    public static int crosshairRed = 145;
    public static int crosshairGreen = 60;
    public static int crosshairBlue = 255;
    public static boolean crosshairCenterDot = true;
    public static int crosshairAlpha = 255;
    public static int crosshairLength = 5;
    public static boolean crosshairDynamic = false;

    // Per-widget HUD appearance (base controls, applied by HUD renderers where supported)
    public static int hudFillAlpha = 150;
    public static int hudTextRed = 245;
    public static int hudTextGreen = 238;
    public static int hudTextBlue = 255;
    public static int hudScalePercent = 100;

    // HUD
    public static boolean watermarkEnabled = false;
    public static boolean playerInfo = false;
    public static boolean armorStatusEnabled = false;
    public static boolean potionsEnabled = false;
    public static boolean cooldownsEnabled = false;
    public static boolean keybindsEnabled = false;
    public static boolean inventoryHudEnabled = false;

    // Позиции HUD
    public static int watermarkX = 10;
    public static int watermarkY = 10;

    public static int playerInfoX = 30;
    public static int playerInfoY = 140;

    public static int potionsX = 30;
    public static int potionsY = 220;

    public static int cooldownsX = 220;
    public static int cooldownsY = 220;

    public static int keybindsX = 410;
    public static int keybindsY = 220;


    public static int inventoryX = 380;
    public static int inventoryY = 520;
    public static int armorX = 30;
    public static int armorY = 300;
}
