package net.logangwin.itemsplitter.logic;

import net.logangwin.itemsplitter.gui.SplitScreen;
import net.logangwin.itemsplitter.mixin.HandledScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;

public class SplitScreenLogic {

    private static boolean isOpen = false;
    private static int splitAmount;
    private static final int minSplit = 0;
    private static int maxSplit = 0;
    private static int mouseX;
    private static int mouseY;
    private static final MinecraftClient client = MinecraftClient.getInstance();

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

    public static int getMaxSplit() {
        return maxSplit;
    }
    public static void setSplitAmount(int itemCount) {
        if (itemCount >= minSplit || itemCount <= maxSplit) {
            splitAmount = itemCount;
        }
    }

    public static void saveMouseCoordinates(double x, double y) {
        // Get client
        MinecraftClient client = MinecraftClient.getInstance();

        // Store scaled coordinates
        mouseX = (int) (x / client.getWindow().getScaleFactor());
        mouseY = (int) (y / client.getWindow().getScaleFactor());
    }

    public static void updateSplitSlider() {
        // Get client
        MinecraftClient client = MinecraftClient.getInstance();

        // Calculate the difference between the mouseX
        // when screen was created and current mouseX
        double deltaX = mouseX - (client.mouse.getX() / client.getWindow().getScaleFactor());

        // Get current screen
        HandledScreen<?> currentScreen = getCurrentScreen();
        HandledScreenAccessor accessor = (HandledScreenAccessor) currentScreen;

        // Get the left and right edge of inventory
        if (accessor != null) {
            // Upper and lower bound
            int width = accessor.getBackgroundWidth();
            double upperBound = (double) width / 2;
            double lowerBound = (double) (width * -1) / 2;
            double offset;
            double ratio;

            if (deltaX > 0) {
                // If offset greater than upperBound, cap to upperBound
                offset = Math.min(deltaX, upperBound);

                // Calculate how far into the upper bound the offset is
                ratio = offset / upperBound;

                // Since offset is in the upper half, multiply by half and add half
                ratio *= 0.5F;
                ratio += 0.5F;

                // Update split slider
                SplitScreen.updateSplitSlider(ratio);
            }
            else if (deltaX < 0) {
                // If offset less than lowerBound, cap to lowerBound
                offset = Math.max(deltaX, lowerBound);

                // Calculate how far into the lower bound the offset is
                ratio = offset / lowerBound;
                ratio = 1.0F - ratio;
                ratio *= 0.5F;

                // Update split slider
                SplitScreen.updateSplitSlider(ratio);
            }
            else {
                // If the offset is 0, then the slider should be at half
                SplitScreen.updateSplitSlider(0.5F);
            }

        }
    }

    public static HandledScreen<?> getCurrentScreen() {
        MinecraftClient client = MinecraftClient.getInstance();

        // Check if the current screen is an instance of HandledScreen
        if (client.currentScreen instanceof HandledScreen<?> handledScreen) {
            return handledScreen;
        }

        return null; // No inventory is currently open
    }
}
