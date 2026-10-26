import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class FeatureSettingsScreen extends Screen {
    private final Screen parent;
    private final String featureName;

    public FeatureSettingsScreen(Screen parent, String featureName) {
        super(Text.literal("Settings: " + featureName));
        this.parent = parent;
        this.featureName = featureName;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        if (featureName.equals("Time Changer")) {
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("Mode: " + Modules.timeMode),
                    button -> {
                        if (Modules.timeMode.equals("Day")) Modules.timeMode = "Night";
                        else if (Modules.timeMode.equals("Night")) Modules.timeMode = "Noon";
                        else Modules.timeMode = "Day";
                        button.setMessage(Text.literal("Mode: " + Modules.timeMode));
                    }
            ).dimensions(centerX - 100, centerY - 20, 200, 20).build());
        } else if (featureName.equals("Hit Color")) {
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("Switch Color"),
                    button -> {
                        if (Modules.hitColorRgb == 0xFF5555) Modules.hitColorRgb = 0x55FF55;
                        else if (Modules.hitColorRgb == 0x55FF55) Modules.hitColorRgb = 0x5555FF;
                        else Modules.hitColorRgb = 0xFF5555;
                        button.setMessage(Text.literal("Color Preset Active"));
                    }
            ).dimensions(centerX - 100, centerY - 20, 200, 20).build());
        }

        // Кнопка возврата назад
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Back"),
                button -> this.client.setScreen(parent)
        ).dimensions(centerX - 100, centerY + 30, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0050505);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 60, 0x00FFCC);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}
