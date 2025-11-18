package net.logangwin.itemsplitter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.logangwin.itemsplitter.gui.Gui;
import net.logangwin.itemsplitter.gui.ItemSplitScreen;
import net.logangwin.itemsplitter.mixin.HandledScreenAccessor;
import net.logangwin.itemsplitter.mixin.HandledScreenMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.spongepowered.asm.mixin.Unique;

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
