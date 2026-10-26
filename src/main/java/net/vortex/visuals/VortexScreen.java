package net.vortex.visuals;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class VortexScreen extends Screen {

    public VortexScreen() {
        super(Text.literal("Vortex Visuals Menu"));
    }

    @Override
    protected void init() {
        int startY = this.height / 4;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int centerX = this.width / 2 - buttonWidth / 2;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Logo: " + getStatus(Modules.logoEnabled)),
                button -> {
                    Modules.logoEnabled = !Modules.logoEnabled;
                    button.setMessage(Text.literal("Logo: " + getStatus(Modules.logoEnabled)));
                }
        ).dimensions(centerX, startY, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("FPS Counter: " + getStatus(Modules.fpsEnabled)),
                button -> {
                    Modules.fpsEnabled = !Modules.fpsEnabled;
                    button.setMessage(Text.literal("FPS Counter: " + getStatus(Modules.fpsEnabled)));
                }
        ).dimensions(centerX, startY + 24, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("CPS Counter: " + getStatus(Modules.cpsEnabled)),
                button -> {
                    Modules.cpsEnabled = !Modules.cpsEnabled;
                    button.setMessage(Text.literal("CPS Counter: " + getStatus(Modules.cpsEnabled)));
                }
        ).dimensions(centerX, startY + 48, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Armor Status: " + getStatus(Modules.armorStatusEnabled)),
                button -> {
                    Modules.armorStatusEnabled = !Modules.armorStatusEnabled;
                    button.setMessage(Text.literal("Armor Status: " + getStatus(Modules.armorStatusEnabled)));
                }
        ).dimensions(centerX, startY + 72, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Hotkeys: " + getStatus(Modules.hotkeysEnabled)),
                button -> {
                    Modules.hotkeysEnabled = !Modules.hotkeysEnabled;
                    button.setMessage(Text.literal("Hotkeys: " + getStatus(Modules.hotkeysEnabled)));
                }
        ).dimensions(centerX, startY + 96, buttonWidth, buttonHeight).build());
    }

    private String getStatus(boolean enabled) {
        return enabled ? "ON" : "OFF";
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
