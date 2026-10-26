package vortex.visuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class VortexClient implements ClientModInitializer {
    private static boolean openGuiNextTick = false;

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            HudRenderer.render(drawContext);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Проверка нажатия Right Shift (GLFW.GLFW_KEY_RIGHT_SHIFT)
            if (client.window != null && GLFW.glfwGetKey(client.window.getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.PRESS) {
                if (!openGuiNextTick && client.currentScreen == null) {
                    client.setScreen(new VortexScreen());
                    openGuiNextTick = true;
                }
            } else {
                openGuiNextTick = false;
            }
        });
    }
}
