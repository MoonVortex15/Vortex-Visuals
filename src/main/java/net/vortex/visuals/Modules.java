package net.vortex.visuals;

public class Modules {
    // Visuals tab
    public static boolean logoEnabled = true;
    public static boolean fpsEnabled = true;
    public static boolean hitColor = true;
    public static int hitColorRgb = 0xFF5555;
    public static boolean jumpCircles = true;
    public static boolean particles = true;
    public static boolean timeChanger = false;
    public static String timeMode = "Day";

    // HUD tab
    public static boolean armorStatusEnabled = true;
    public static boolean playerInfo = true;
    public static boolean potionsEnabled = true;
    public static boolean cooldownsEnabled = true;

    // Utilities tab
    public static boolean hotkeysEnabled = true;
    public static boolean autoFish = false;
    public static boolean brightness = true;

    // Координаты для перетаскивания элементов HUD (сохраняются при перемещении в чате)
    public static int logoX = 15;
    public static int logoY = 15;
    
    public static int playerInfoX = 15;
    public static int playerInfoY = 120;
    
    public static int hotkeysX = -120; // Относительно правого края, если отрицательное
    public static int hotkeysY = 120;
}
