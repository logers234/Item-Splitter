package net.logangwin.itemsplitter.logic;

import net.logangwin.itemsplitter.ItemSplitter;
import net.logangwin.itemsplitter.gui.SplitScreen;
import net.logangwin.itemsplitter.mixin.HandledScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

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

    private static void setSplitAmount(int itemCount) {
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

        // Get current screen
        HandledScreen<?> currentScreen = getCurrentScreen();
        HandledScreenAccessor accessor = (HandledScreenAccessor) currentScreen;

        // Get the left and right edge of inventory
        if (accessor != null && RightClickHandler.targetSlot != null) {

            double ratio = getRatio((HandledScreenAccessor) currentScreen, client, accessor);

            // Update the slider and the current number of items to be picked up
            SplitScreen.updateSplitSlider(ratio);
            setSplitAmount(Math.round((float) (maxSplit * ratio)));
        }
    }

    private static double getRatio(HandledScreenAccessor currentScreen, MinecraftClient client, HandledScreenAccessor accessor) {
        // Calculate delta between target slot and mouse
        int guiLeft = currentScreen.getX();
        int slotX = guiLeft + RightClickHandler.targetSlot.x + 8;
        double deltaX = (client.mouse.getX() / client.getWindow().getScaleFactor()) - (double) slotX;

        // Calculate bounds
        int width = accessor.getBackgroundWidth() / 4;
        double halfWidth = width / 2.0;

        // Clamp deltaX between -halfWidth and +halfWidth
        double offset = Math.max(-halfWidth, Math.min(deltaX, halfWidth));

        // Ratio from 0.0 to 1.0
        return (offset / (double) width) + 0.5;
    }

    public static HandledScreen<?> getCurrentScreen() {
        MinecraftClient client = MinecraftClient.getInstance();

        // Check if the current screen is an instance of HandledScreen
        if (client.currentScreen instanceof HandledScreen<?> handledScreen) {
            return handledScreen;
        }

        return null; // No inventory is currently open
    }

    public static void splitStack(Slot targetSlot) {
        MinecraftClient client = MinecraftClient.getInstance();
        HandledScreen<?> screen = getCurrentScreen();

        if (screen == null || client.interactionManager == null || targetSlot == null) return;

        int slotId = RightClickHandler.targetSlotID;
        int itemsInStack = targetSlot.getStack().getCount();
        int halfStack = (int) Math.ceil((double) itemsInStack / 2);

        ItemSplitter.LOGGER.info("Split amount = {}", splitAmount);
        // Safety checks
        if (splitAmount <= 0 || splitAmount > itemsInStack) return;

        // Case 1: Grab the full stack
        if (splitAmount == itemsInStack) {
            client.interactionManager.clickSlot(
                    screen.getScreenHandler().syncId,
                    slotId,
                    GLFW.GLFW_MOUSE_BUTTON_LEFT,
                    SlotActionType.PICKUP,
                    client.player
            );
            return;
        }

        // Case 2: Grab exactly half (Vanilla default behavior)
        if (splitAmount == halfStack) {
            client.interactionManager.clickSlot(
                    screen.getScreenHandler().syncId,
                    slotId,
                    GLFW.GLFW_MOUSE_BUTTON_RIGHT,
                    SlotActionType.PICKUP,
                    client.player
            );
            return;
        }

        // Case 3: Number of items left behind is greater than the number picked up
        if (splitAmount < halfStack) {
            // Pickup half of the stack
            client.interactionManager.clickSlot(
                    screen.getScreenHandler().syncId,
                    slotId,
                    GLFW.GLFW_MOUSE_BUTTON_RIGHT,
                    SlotActionType.PICKUP,
                    client.player
            );

            // Calculate the number of items to drop back into stack
            int dropItems = halfStack - splitAmount;

            ItemSplitter.LOGGER.info("itemsToDropBack = {}", dropItems);

            // Queue the clicks
            for (int i = 0; i < dropItems; i++) {
                if (!screen.getScreenHandler().getCursorStack().isEmpty()) {
                    client.interactionManager.clickSlot(
                            screen.getScreenHandler().syncId,
                            slotId,
                            GLFW.GLFW_MOUSE_BUTTON_RIGHT,
                            SlotActionType.PICKUP,
                            client.player
                    );
                }
                else {
                    ItemSplitter.LOGGER.warn("Cursor is empty!");
                }
            }
        }
        // Case 4: Number of items picked up is greater than those left behind
        else {
            // Pickup the stack
            client.interactionManager.clickSlot(
                    screen.getScreenHandler().syncId,
                    slotId,
                    GLFW.GLFW_MOUSE_BUTTON_LEFT,
                    SlotActionType.PICKUP,
                    client.player
            );

            // Calculate the number of items to drop back into the inventory
            int dropItems = itemsInStack - splitAmount;

            // Queue the actions
            for (int i = 0; i < dropItems; i++) {
                client.interactionManager.clickSlot(
                        screen.getScreenHandler().syncId,
                        slotId,
                        GLFW.GLFW_MOUSE_BUTTON_RIGHT,
                        SlotActionType.PICKUP,
                        client.player
                );
            }
        }
    }
}
