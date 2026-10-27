package net.vortex.visuals;

import net.vortex.visuals.hud.HudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;

public class VortexClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Регистрация рендеринга HUD
        net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            HudRenderer.render(drawContext, renderTickCounter);
        });
    }
}
