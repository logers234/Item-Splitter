package net.logangwin.itemsplitter.mixin;


import com.mojang.blaze3d.systems.RenderSystem;
import net.logangwin.itemsplitter.ItemSplitter;
import net.logangwin.itemsplitter.logic.RightClickHandler;
import net.logangwin.itemsplitter.gui.ChargeCircleComponent;
import net.logangwin.itemsplitter.gui.SplitScreen;
import net.logangwin.itemsplitter.logic.SplitScreenLogic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {

    @Shadow protected int x;
    @Shadow protected int y;

    @Shadow protected abstract void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType);

    @SuppressWarnings("unused")
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
            cir.cancel();
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void onMouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {

            // If the long-press ALREADY triggered (in the tick/render method)
            if (RightClickHandler.actionTriggered) {
                // Cancel action if it has already been triggered, then reset
                RightClickHandler.stopCharging();
                cir.setReturnValue(true);
                cir.cancel();
                return;
            }

            // Check if it's too early for the custom split
            boolean releasedEarly = RightClickHandler.checkIfReleasedEarly();

            if (!releasedEarly) {
                // Right click hold passed 1 second threshold, do custom splitting logic here
                // TODO: Implement custom tooltip UI and logic
                SplitScreenLogic.onScreenOpen(RightClickHandler.targetSlot);
                SplitScreenLogic.saveMouseCoordinates(mouseX, mouseY);

                ItemSplitter.LOGGER.info("Performing Custom Split");
                if (RightClickHandler.targetSlot != null) {
                    this.onMouseClick(RightClickHandler.targetSlot, RightClickHandler.targetSlot.getIndex(), button, SlotActionType.PICKUP);
                }
            }
            else {
                // User released too quickly - Perform Vanilla Right Click
                ItemSplitter.LOGGER.info("Released early, performing vanilla pickup");
                if (RightClickHandler.targetSlot != null) {
                    this.onMouseClick(RightClickHandler.targetSlot, RightClickHandler.targetSlot.getIndex(), button, SlotActionType.PICKUP);
                }
            }

            // Block vanilla action (we already manually sent the packet above)
            RightClickHandler.stopCharging();
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @SuppressWarnings("unused")
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

    @Unique
    public int getItemSlotX(Slot slot) {
        return ((HandledScreenAccessor) this).getX() + slot.x + 8;
    }

    @Unique
    private int getItemSlotY(Slot slot) {
        return ((HandledScreenAccessor) this).getY() + slot.y + 8;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (RightClickHandler.isCharging() && RightClickHandler.targetSlot != null && RightClickHandler.getChargeTime() > 100 && RightClickHandler.targetSlot.hasStack()) {
            // Get the charge percentage
            float progress = RightClickHandler.getChargePercent();

            // Disable depth testing and push the charge circle to the front
            context.getMatrices().push();
            context.getMatrices().translate(0, 0, 500);
            RenderSystem.disableDepthTest();

            // Get slot coordinates and draw the circles
            int slotX = getItemSlotX(RightClickHandler.targetSlot);
            int slotY = getItemSlotY(RightClickHandler.targetSlot);
            ChargeCircleComponent.drawProgressRing(context, slotX, slotY, 4, 2, progress, 0xFFFFFFFF);

            // Reset the offset
            RenderSystem.enableDepthTest();
            context.getMatrices().pop();
        } else {
            // Hide when not splitting
            ChargeCircleComponent.drawProgressRing(context, 0, 0, 6, 3, 0, 0x00000000);
        }

        if (SplitScreenLogic.isScreenOpen() && RightClickHandler.targetSlot != null) {
            // ---- Render Split Screen Tooltip ----
            SplitScreenLogic.updateSplitSlider();

            // Disable depth testing and push the slider to the front
            context.getMatrices().push();
            context.getMatrices().translate(0, 0, 550);

            // Get slot coordinates for tooltip
            int slotX = getItemSlotX(RightClickHandler.targetSlot);
            int slotY = getItemSlotY(RightClickHandler.targetSlot);

            // Draw tooltip
            SplitScreen.drawTooltip(context, this.textRenderer, slotX, slotY, RightClickHandler.targetSlot);

            // Reset the offset and re-enable depth testing
            RenderSystem.enableDepthTest();
            context.getMatrices().pop();
        }
    }
}