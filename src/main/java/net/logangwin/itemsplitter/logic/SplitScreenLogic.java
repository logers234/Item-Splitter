package net.logangwin.itemsplitter.logic;

import net.minecraft.screen.slot.Slot;

public class SplitScreenLogic {

    private static boolean isOpen = false;
    private static int splitAmount;
    private static final int minSplit = 0;
    private static int maxSplit = 0;
    private static int mouseX;
    private static int mouseY;

    public static void onScreenOpen(Slot targetSlot) {
        if (targetSlot != null) {
            // Set initial split amount to half of target stack
            int itemCount = targetSlot.getStack().getCount();
            splitAmount = Math.round((float) itemCount / 2);

            // Set max split to the number of items in the stack
            maxSplit = itemCount;
        }
        isOpen = true;
    }

    public static void onScreenClose() {
        maxSplit = 0;
        isOpen = false;
    }

    public static boolean isScreenOpen() {
        return isOpen;
    }

    public static int getSplitAmount() {
        return splitAmount;
    }

    public static void setSplitAmount(int itemCount) {
        if (itemCount >= minSplit || itemCount <= maxSplit) {
            splitAmount = itemCount;
        }
    }

    public static void saveMouseCoordinates(int x, int y) {
        mouseX = x;
        mouseY = y;
    }
}
