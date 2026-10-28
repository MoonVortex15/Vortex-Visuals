
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
    public static int handOffsetX = 0;
    public static int handOffsetY = 0;
    public static int handOffsetZ = 0;
    public static int handScalePercent = 100;
    public static int handRotation = 0;
    public static int handAngle = 0; // Main-hand rotation X (legacy field)
    public static int handRotationY = 0;
    // Independent off-hand controls
    public static int offHandOffsetX = 0;
    public static int offHandOffsetY = 0;
    public static int offHandOffsetZ = 0;
    public static int offHandScalePercent = 100;
    public static int offHandRotationX = 0;
    public static int offHandRotationY = 0;
    public static int offHandRotationZ = 0;
    // 0 = normal, 1 = smooth, 2 = static
    public static int handAnimationStyle = 0;
    public static boolean fullBright = false;
    public static boolean hitColor = false;
    public static int hitColorRgb = 0xFF5555;
    public static boolean hitboxCustomizer = false;
    public static boolean jumpCircles = false;
    public static boolean particles = false;
    public static boolean renderTweaks = false;

    // Новые визуальные модули
    public static boolean timeChanger = false;
    public static int customTimeOfDay = 6000;
    public static boolean trails = false;
    public static int trailLifetimeTicks = 30;
    public static int trailRed = 145;
    public static int trailGreen = 60;
    public static int trailBlue = 255;
    public static boolean enchantmentEffects = false;
    public static int enchantmentColor = 0x913CFF;
    public static boolean damageIndicators = false;
    public static int damageIndicatorSize = 12;
    public static int damageIndicatorRise = 10;
    public static int damageIndicatorRed = 255;
    public static int damageIndicatorGreen = 90;
    public static int damageIndicatorBlue = 90;
    public static boolean trajectories = false;
    public static boolean shieldTotemCounter = false;
    public static boolean hitBubbles = false;
    public static int hitBubbleSize = 2;
    public static boolean waypoints = false;
    public static String waypointName = "Home";
    public static int waypointX = 0;
    public static int waypointY = 80;
    public static int waypointZ = 0;

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
