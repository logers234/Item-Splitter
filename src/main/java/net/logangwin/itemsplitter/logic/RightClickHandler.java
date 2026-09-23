package net.logangwin.itemsplitter.logic;

import net.logangwin.itemsplitter.gui.ConfigScreen;
import net.minecraft.screen.slot.Slot;

public class RightClickHandler
{
    private static boolean isCharging = false;
    private static boolean wasFullyCharged = true;
    private static long chargeStart = 0;
    private static long chargeStop = 0;
    private static Slot targetSlot = null;

    public static void startCharging() {
        // Begin charge when right click is held
        isCharging = true;
        wasFullyCharged = false;
        chargeStart = System.currentTimeMillis();
    }

    public static void stopCharging() {
        // Reset charge when right click is released
        isCharging = false;
        chargeStop = System.currentTimeMillis();

        if (getChargePercent() >= 1.0f) {
            wasFullyCharged = true;
        }
    }

    public static void setTargetSlot(Slot slot) {
        targetSlot = slot;
    }

    public static Slot getTargetSlot() {
        return targetSlot;
    }

    public static int getTargetSlotIndex() {
        return targetSlot.getIndex();
    }

    public static int getTargetSlotID() {
        return targetSlot.id;
    }

    public static boolean validTargetSlot() {
        return (targetSlot != null);
    }

    public static boolean isFadingOut() {
        if (!(wasFullyCharged && ConfigScreen.AnimationSettings.enableAnimations)) { return false; }
        return (System.currentTimeMillis() - chargeStop) < ConfigScreen.AnimationSettings.animationTime;
    }

    public static float getFadeOutPercent() {
        if (ConfigScreen.AnimationSettings.animationTime <= 0) return 1.0f;

        float elapsed = (float) (System.currentTimeMillis() - chargeStop);
        float percent = elapsed / ConfigScreen.AnimationSettings.animationTime;
        return Math.min(Math.max(percent, 0.0f), 1.0f);
    }

    public static float getChargePercent() {
        // Get the percentage of how far the charge is to the threshold
        return (float) (System.currentTimeMillis() - chargeStart) / ConfigScreen.GeneralSettings.timeDelay;
    }

    public static boolean isCharging() {
        // Is the right click being held?
        return isCharging;
    }

    public static long getChargeTime() {
        // Return the current time of the charge
        return System.currentTimeMillis() - chargeStart;
    }

    public static boolean checkIfReleasedEarly() {
        // If the mouse button is released before 1 second has passed, reset the timer and return true
        if (isCharging && System.currentTimeMillis() - chargeStart < ConfigScreen.GeneralSettings.timeDelay) {
            stopCharging();
            return true;
        }
        else {
            return false;
        }
    }

    public static void tick()
    {
        if (isCharging && System.currentTimeMillis() - chargeStart > ConfigScreen.GeneralSettings.timeDelay) {

            // If the charge threshold is reached, open the split screen
            try {
                SplitScreenHandler.onScreenOpen(targetSlot);
            } catch (Exception e) {
                throw new NullPointerException("Target slot is null");
            }

            stopCharging();
        }
    }
}

