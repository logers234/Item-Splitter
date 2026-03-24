package net.logangwin.itemsplitter.logic;

import net.minecraft.screen.slot.Slot;

public class RightClickHandler
{
    private static boolean isCharging = false;
    private static long chargeStart = 0;
    public static Slot targetSlot = null;
    public static boolean actionTriggered = false;
    public static int maxCharge = 1000;

    public static void startCharging() {
        // Begin charge when right click is held
        isCharging = true;
        chargeStart = System.currentTimeMillis();
    }

    public static void stopCharging() {
        // Reset charge when right click is released
        isCharging = false;
        chargeStart = 0;
    }

    public static float getChargePercent() {
        // Get the percentage of how far the charge is to the threshold
        return (float) (System.currentTimeMillis() - chargeStart) / maxCharge;
    }

    public static boolean isCharging() {
        // Is right click being held?
        return isCharging;
    }

    public static long getChargeTime() {
        // Return the current time of the charge
        return System.currentTimeMillis() - chargeStart;
    }

    public static boolean checkIfReleasedEarly() {
        // If the mouse button is released before 1 second has passed, reset the timer and return true
        if (isCharging && System.currentTimeMillis() - chargeStart < maxCharge) {
            stopCharging();
            return true;
        }
        else {
            return false;
        }
    }

    public static void tick()
    {
        if (isCharging && System.currentTimeMillis() - chargeStart > maxCharge) {
            // TODO: Add owo GUI here
            stopCharging();
        }
    }
}

