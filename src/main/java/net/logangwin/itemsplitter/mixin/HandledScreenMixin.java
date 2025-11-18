package net.logangwin.itemsplitter.mixin;


import net.logangwin.itemsplitter.ItemSplitter;
import net.logangwin.itemsplitter.RightClickHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
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

    public HandledScreenMixin() {
        super(null);
    }

    @Unique
    private void logRightClickStatus() {
        String log = "Right Click Charging Value: ";
        String value;
        if (RightClickHandler.isCharging) {
            value = "true";
        } else {
            value = "false";
        }
        log += value;
        ItemSplitter.LOGGER.info(log);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        ItemSplitter.LOGGER.error("Mouse Click");

        if (client.player == null) return;

        if (mouseReleased) {
            ItemSplitter.LOGGER.error("Mouse Released");
            cir.setReturnValue(false);
            cir.cancel();
            mouseReleased = false;
            return;
        }

        if (client.player.currentScreenHandler.getCursorStack().isEmpty()) {

            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                logRightClickStatus();
                RightClickHandler.startCharging(); // Start 1s timer

                if (RightClickHandler.isCharging) {
                    ItemSplitter.LOGGER.info("Blocking default right-click behavior.");
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        } else {
            ItemSplitter.LOGGER.warn("Aborted: Player is already holding an item.");
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void onMouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            mouseReleased = true;
            ItemSplitter.LOGGER.info("Released Right Click");

            // Reset charge
            boolean releasedEarly = RightClickHandler.checkIfReleasedEarly();

            // Simulate stack split if right click was released early
            if (releasedEarly) {
                simulateStackSplit((HandledScreen<?>) (Object) this, mouseX, mouseY);
            }
        }
    }
    @Unique
    private static boolean mouseReleased = false;

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

        // Get the hovered slot
        Slot hoveredSlot = null;
        int slotId = -1;

        // Find the slot the mouse is currently over
        for (int i = 0; i < screen.getScreenHandler().slots.size(); i++) {
            Slot slot = screen.getScreenHandler().slots.get(i);
            if (isPointOverSlot(slot, mouseX, mouseY)) {
                hoveredSlot = slot;
                slotId = i;
                break;
            }
        }

        // Debug
        if (hoveredSlot != null) {
            ItemSplitter.LOGGER.warn(String.valueOf(hoveredSlot.x));
            ItemSplitter.LOGGER.warn(String.valueOf(hoveredSlot.y));
            ItemSplitter.LOGGER.warn(String.valueOf(slotId));
        }

        // Simulate a stack split
        if (hoveredSlot != null && hoveredSlot.hasStack()) {
            client.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotId, 1, SlotActionType.PICKUP, client.player);

            ItemSplitter.LOGGER.error("Click");
        }

        ItemSplitter.LOGGER.info("Slot count: {}", screen.getScreenHandler().slots.size());
    }

    @Unique
    private boolean isPointOverSlot(Slot slot, double mouseX, double mouseY) {
        int slotX = this.x + slot.x;
        int slotY = this.y + slot.y;
        return mouseX >= slotX && mouseX < slotX + 16 &&
                mouseY >= slotY && mouseY < slotY + 16;
    }
}