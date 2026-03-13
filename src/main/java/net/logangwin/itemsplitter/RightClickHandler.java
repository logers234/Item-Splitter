package net.logangwin.itemsplitter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.slot.Slot;

public class RightClickHandler
{
    public static boolean isCharging = false;
    private static long chargeStart = 0;
    public static Slot targetSlot = null;
    public static boolean actionTriggered = false;

    public static void startCharging() {
        isCharging = true;
        chargeStart = System.currentTimeMillis();
    }

    public static void stopCharging() {
        isCharging = false;
        chargeStart = 0;
    }

    public static long getChargeTime() {
        return chargeStart;
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
            // TODO: Add owo GUI here
            MinecraftClient client = MinecraftClient.getInstance();
            stopCharging();
        }
    }
}

