package vortex.visuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class VortexClient implements ClientModInitializer {
    private static KeyBinding guiKeyBinding;

    @Override
    public void onInitializeClient() {
        guiKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Vortex Menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "Vortex Visuals"
        ));

        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            HudRenderer.render(drawContext);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (guiKeyBinding.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new VortexScreen());
                }
            }
        });
    }
}
