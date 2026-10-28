
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import org.lwjgl.glfw.GLFW;

public class VortexClient implements ClientModInitializer {
    private static KeyBinding openMenuKey;
    private static int effectTick;

    @Override
    public void onInitializeClient() {
        HudRenderer.loadConfig();
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.vortex_visuals.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.vortex_visuals"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            effectTick++;
            if (client.world != null && client.player != null && Modules.trails && effectTick % 3 == 0) {
                for (PlayerEntity other : client.world.getPlayers()) {
                    if (other == client.player || !other.isAlive() || client.player.squaredDistanceTo(other) > 48.0 * 48.0) continue;
                    double x = other.getX();
                    double y = other.getY() + 0.8;
                    double z = other.getZ();
                    client.world.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0, 0.015, 0.0);
                }
            }
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
                if (Modules.hitBubbles && world instanceof net.minecraft.client.world.ClientWorld clientWorld) {
                    for (int i = 0; i < 8; i++) {
                        double ox = (clientWorld.random.nextDouble() - 0.5) * 0.8;
                        double oy = clientWorld.random.nextDouble() * Math.max(0.5, target.getHeight());
                        double oz = (clientWorld.random.nextDouble() - 0.5) * 0.8;
                        clientWorld.addParticle(ParticleTypes.END_ROD, target.getX() + ox, target.getY() + oy, target.getZ() + oz, 0.0, 0.025, 0.0);
                    }
                }
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
