package net.vortex.visuals;

public class Modules {
    // Visuals & Utilities
    public static boolean chinaHit = true;
    public static boolean customHand = true;
    public static boolean fullBright = true;
    public static boolean hitColor = false;
    public static int hitColorRgb = 0xFF5555;
    public static boolean hitboxCustomizer = false;
    public static boolean jumpCircles = false;
    public static boolean particles = true;
    public static boolean renderTweaks = true;

    // Time Changer
    public static boolean timeChanger = false;
    public static String timeMode = "Day";

    // HUD modules
    public static boolean playerInfo = true;
    public static boolean armorStatusEnabled = true;
    public static boolean potionsEnabled = true;
    public static boolean hotkeysEnabled = false; // По умолчанию выключено, без жестких биндов на экране
    public static boolean musicBarEnabled = true; // Плеер музыки как на референсе

    // Координаты для свободного перетаскивания элементов в чате
    public static int musicBarX = 20;
    public static int musicBarY = 20;
    
    public static int potionsX = 20;
    public static int potionsY = 130;
    
    public static int playerInfoX = 20;
    public static int playerInfoY = 220;
    
    public static int hotkeysX = -150; // Относительно правого края
    public static int hotkeysY = 150;
}
