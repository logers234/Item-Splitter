package net.logangwin.itemsplitter.mixin;


import net.logangwin.itemsplitter.ItemSplitter;
import net.logangwin.itemsplitter.RightClickHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {

    @Shadow protected int x;
    @Shadow protected int y;

    @Shadow protected abstract void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType);

    public HandledScreenMixin() {
        super(null);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        // If the mouse button that was triggered was the right mouse button, block the vanilla behavior
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            // Start the timer, get the target slot and block the right click action
            RightClickHandler.startCharging();
            RightClickHandler.targetSlot = this.getSlotUnderMouse((HandledScreen<?>) (Object) this, mouseX, mouseY);
            RightClickHandler.actionTriggered = false;
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void onMouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            ItemSplitter.LOGGER.info("Released Right Click");

            // Reset charge
            boolean releasedEarly = RightClickHandler.checkIfReleasedEarly();

            // Simulate stack split if right click was released early
            if (RightClickHandler.getChargeTime() > 0 && !RightClickHandler.actionTriggered) {
                // If it wasn't released early, simulate logic
                if (!releasedEarly) {
                    simulateStackSplit((HandledScreen<?>) (Object) this, mouseX, mouseY);
                }
            }
            else if (!RightClickHandler.actionTriggered) {
                this.onMouseClick(RightClickHandler.targetSlot, RightClickHandler.targetSlot.getIndex(), button, SlotActionType.PICKUP);
                RightClickHandler.stopCharging();
            }
        }
    }

    @Unique
    private void simulateStackSplit(HandledScreen<?> screen, double mouseX, double mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();
        ItemSplitter.LOGGER.warn("Inside simulateStackSplit function");

        if (client.player == null || client.interactionManager == null) {
            ItemSplitter.LOGGER.info("Null client or interaction manager");
            return;
        } else if (!client.player.currentScreenHandler.getCursorStack().isEmpty()) {
            ItemSplitter.LOGGER.warn("Aborted: Player is already holding an item.");
            return;
        }

        // Get the slot under the mouse and its id
        Slot slotUnderMouse = getSlotUnderMouse(screen, mouseX, mouseY);
        int slotId = getSlotIDUnderMouse(screen, mouseX, mouseY);

        // Debug
        if (slotUnderMouse != null) {
            ItemSplitter.LOGGER.warn(String.valueOf(slotUnderMouse.x));
            ItemSplitter.LOGGER.warn(String.valueOf(slotUnderMouse.y));
            ItemSplitter.LOGGER.warn(String.valueOf(slotId));
        }

        // Simulate a stack split
        if (slotUnderMouse != null && slotUnderMouse.hasStack()) {
            client.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotId, 1, SlotActionType.PICKUP, client.player);

            ItemSplitter.LOGGER.error("Click");
        }

        ItemSplitter.LOGGER.info("Slot count: {}", screen.getScreenHandler().slots.size());
    }

    @Unique
    private boolean isPointOverSlot(Slot slot, double mouseX, double mouseY) {
        // Check if a given slot is under the mouse
        int slotX = this.x + slot.x;
        int slotY = this.y + slot.y;
        return mouseX >= slotX && mouseX < slotX + 16 &&
                mouseY >= slotY && mouseY < slotY + 16;
    }

    @Unique
    private int getSlotIDUnderMouse(HandledScreen<?> screen, double mouseX, double mouseY) {
        // Find the id of slot the mouse is currently over
        for (int i = 0; i < screen.getScreenHandler().slots.size(); i++) {
            Slot slot = screen.getScreenHandler().slots.get(i);
            if (isPointOverSlot(slot, mouseX, mouseY) && slot.isEnabled()) {
                // Return index of slot
                return i;
            }
        }

        // Returns -1 if no slot found
        return -1;
    }

    @Unique
    private Slot getSlotUnderMouse(HandledScreen<?> screen, double mouseX, double mouseY) {
        // Find the slot the mouse is currently over
        for (int i = 0; i < screen.getScreenHandler().slots.size(); i++) {
            Slot slot = screen.getScreenHandler().slots.get(i);
            if (isPointOverSlot(slot, mouseX, mouseY) && slot.isEnabled()) {
                // Return the slot
                return slot;
            }
        }

        // Return null if a slot wasn't found
        return null;
    }
}