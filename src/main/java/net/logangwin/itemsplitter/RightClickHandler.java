package net.logangwin.itemsplitter;

import net.logangwin.itemsplitter.gui.Gui;
import net.logangwin.itemsplitter.gui.ItemSplitScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.lwjgl.glfw.GLFW;

public class RightClickHandler
{
    public static boolean isCharging = false;
    public static long chargeStart = 0;

    public static void startCharging() {
        isCharging = true;
        chargeStart = System.currentTimeMillis();
    }

    public static void stopCharging() {
        isCharging = false;
        chargeStart = 0;
    }

    public static boolean checkIfReleasedEarly() {
        if (isCharging && System.currentTimeMillis() - chargeStart < 1000) {
            stopCharging();
            return true;
        }
        else {
            return false;
        }
    }
    public static void tick()
    {
        if (isCharging && System.currentTimeMillis() - chargeStart > 1000) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (!(client.currentScreen instanceof ItemSplitScreen)) {
                client.setScreen(new ItemSplitScreen(new Gui()));
                stopCharging();
            }
        }
    }
}

