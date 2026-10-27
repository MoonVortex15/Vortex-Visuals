
package net.vortex.visuals;

import net.vortex.visuals.hud.HudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import org.lwjgl.glfw.GLFW;

public class VortexClient implements ClientModInitializer {
    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.vortex_visuals.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.vortex_visuals"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen instanceof VortexScreen) {
                    client.setScreen(null);
                } else if (client.player != null) {
                    client.setScreen(new VortexScreen());
                }
            }
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient && entity instanceof PlayerEntity target && target != player) {
                HudRenderer.setTarget(target);
            }
            return ActionResult.PASS;
        });

        HudRenderCallback.EVENT.register(
                (drawContext, renderTickCounter) ->
                        HudRenderer.render(drawContext, renderTickCounter)
        );

        // Регистрация Block Overlay
        BlockOverlayRenderer.register();
        TargetEspRenderer.register();
    }
}
