package de.Cooltechno.aas.client.gui;

import de.Cooltechno.aas.client.AasClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class AimAssistScreen extends Screen {
    public AimAssistScreen() {
        super(Text.literal("AimAssist Settings"));
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 100;
        int y = this.height / 2 - 50;

        this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("Enabled: " + AasClient.AIM_ASSIST.enabled),
                        button -> {
                            AasClient.AIM_ASSIST.enabled = !AasClient.AIM_ASSIST.enabled;
                            button.setMessage(Text.literal("Enabled: " + AasClient.AIM_ASSIST.enabled));
                        })
                .dimensions(x, y, 200, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("Smoothness: " + AasClient.AIM_ASSIST.smoothness),
                        button -> {
                            AasClient.AIM_ASSIST.smoothness += 5.0f;
                            if (AasClient.AIM_ASSIST.smoothness > 100) AasClient.AIM_ASSIST.smoothness = 5.0f;
                            button.setMessage(Text.literal("Smoothness: " + AasClient.AIM_ASSIST.smoothness));
                        })
                .dimensions(x, y + 24, 200, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("Range: " + AasClient.AIM_ASSIST.range),
                        button -> {
                            AasClient.AIM_ASSIST.range = (AasClient.AIM_ASSIST.range >= 6.0) ? 3.0 : AasClient.AIM_ASSIST.range + 0.5;
                            button.setMessage(Text.literal("Range: " + AasClient.AIM_ASSIST.range));
                        })
                .dimensions(x, y + 48, 200, 20)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x80000000);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}