package net.logangwin.itemsplitter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ItemSplitterClient implements ClientModInitializer {

    private static boolean callbackRegistered;
    public static boolean windowOpen = false;


    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> tick());
    }

    public void tick() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!callbackRegistered && MinecraftClient.getInstance().getWindow() != null) {
            long window = MinecraftClient.getInstance().getWindow().getHandle();

            if (MinecraftClient.getInstance().currentScreen != null && GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS) {
                RightClickHandler.startCharging();
            } else {
                RightClickHandler.stopCharging();
            }

            callbackRegistered = true;
        }

        RightClickHandler.tick();
    }
}
