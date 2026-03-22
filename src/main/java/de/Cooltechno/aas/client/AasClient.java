package de.Cooltechno.aas.client;

import de.Cooltechno.aas.client.gui.AimAssistScreen;
import de.Cooltechno.aas.modules.AimAssist;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AasClient implements ClientModInitializer {
    public static final AimAssist AIM_ASSIST = new AimAssist();

    private boolean isKeyDown = false;

    @Override
    public void onInitializeClient() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.getWindow() == null) return;

            boolean isPressed = InputUtil.isKeyPressed(client.getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);

            if (isPressed && !isKeyDown) {
                if (client.currentScreen instanceof AimAssistScreen) {
                    client.setScreen(null);
                } else {
                    client.setScreen(new AimAssistScreen());
                }
                isKeyDown = true;
            } else if (!isPressed) {
                isKeyDown = false;
            }
        });

        WorldRenderEvents.END_MAIN.register(context -> {
            if (MinecraftClient.getInstance().player != null) {
                AIM_ASSIST.onRenderTick();
            }
        });
    }
}