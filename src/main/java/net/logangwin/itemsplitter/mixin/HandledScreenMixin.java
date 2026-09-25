package net.logangwin.itemsplitter.mixin;


import net.logangwin.itemsplitter.ItemSplitterClient;
import net.logangwin.itemsplitter.gui.ConfigScreen;
import net.logangwin.itemsplitter.logic.ItemSplitterUtils;
import net.logangwin.itemsplitter.logic.RightClickHandler;
import net.logangwin.itemsplitter.gui.ChargeCircleHud;
import net.logangwin.itemsplitter.gui.SplitScreen;
import net.logangwin.itemsplitter.logic.SplitScreenHandler;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {

    @Shadow protected int x;
    @Shadow protected int y;

    @Shadow
    protected abstract void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType);

    @Shadow @Nullable protected Slot focusedSlot;

    @SuppressWarnings("unused")
    public HandledScreenMixin() {
        super(null);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        boolean validScreen = client != null && client.player != null && ItemSplitterUtils.getCurrentScreen() != null;
        Slot slot = this.focusedSlot;
        // If the mouse button that was triggered was the right mouse button, block the vanilla behavior
        if (click.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && validScreen && !ItemSplitterUtils.isOutputSlot(slot)) {
            if (ItemSplitterUtils.cursorStackEmpty()) {
                // Start the timer, get the target slot and block the right click action
                RightClickHandler.startCharging();
                RightClickHandler.setTargetSlot(slot);
                cir.setReturnValue(true);
                cir.cancel();
            }
        }

        // Block player from picking up stacks while in the split screen
        if (click.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT && SplitScreenHandler.isScreenOpen()) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void onMouseReleased(Click click, CallbackInfoReturnable<Boolean> cir) {
        if (click.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && client != null && client.player != null && ItemSplitterUtils.cursorStackEmpty()) {

            // Check if it's too early for the custom split
            boolean releasedEarly = RightClickHandler.checkIfReleasedEarly();

            if (!releasedEarly && RightClickHandler.getTargetSlot() != null) {
                // Right click hold passed 1 second threshold, do custom splitting logic here

                boolean creativeSlot = ItemSplitterUtils.isCreativeSlot((HandledScreen<?>) (Object) this, RightClickHandler.getTargetSlot());

                if (client.player.isCreative() && creativeSlot) {
                    SplitScreenHandler.creativePickupStack(RightClickHandler.getTargetSlot());
                }
                else {
                    SplitScreenHandler.splitStack(RightClickHandler.getTargetSlot());
                }
            }
            else {
                // User released too quickly - Perform Vanilla Right Click
                if (RightClickHandler.validTargetSlot()) {
                    this.onMouseClick(RightClickHandler.getTargetSlot(), RightClickHandler.getTargetSlotIndex(), click.button(), SlotActionType.PICKUP);
                }
            }

            // Split screen should close if it was open
            if (SplitScreenHandler.isScreenOpen()) {
                SplitScreenHandler.onScreenClose();
                RightClickHandler.setTargetSlot(null);
            }

            // Block vanilla action (we already manually sent the packet above)
            RightClickHandler.stopCharging();
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"), cancellable = true)
    private void hideTooltipWhenCharging(DrawContext context, int x, int y, CallbackInfo ci) {
        // If right click is charging or the split screen is open, hide the current tooltip
        if (RightClickHandler.isCharging() || SplitScreenHandler.isScreenOpen()) {
            ci.cancel();
        }
    }

    @Unique
    public int getItemSlotX(Slot slot) {
        return ((HandledScreenAccessor) this).getX() + slot.x + 8;
    }

    @Unique
    private int getItemSlotY(Slot slot) {
        return ((HandledScreenAccessor) this).getY() + slot.y + 8;
    }

    @Inject(method = "renderMain", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        boolean hasValidTarget = RightClickHandler.validTargetSlot() && RightClickHandler.getTargetSlot().hasStack();
        boolean currentlyCharging = RightClickHandler.getChargeTime() > ConfigScreen.GeneralSettings.splitCircleStartDelay && RightClickHandler.isCharging();
        boolean showCircle = ConfigScreen.GeneralSettings.enableChargeCircle && (currentlyCharging || RightClickHandler.isFadingOut());

        if (hasValidTarget && showCircle) {
            // Get the charge percentage
            float progress = RightClickHandler.getChargePercent();
            float alpha = getAlpha(progress);

            // Disable depth testing and push the charge circle to the front
            context.getMatrices().pushMatrix();

            int slotX = getItemSlotX(RightClickHandler.getTargetSlot());
            int slotY = getItemSlotY(RightClickHandler.getTargetSlot());
            ChargeCircleHud.drawProgressRing(context, slotX, slotY, progress, alpha);

            // Reset the offset
            context.getMatrices().popMatrix();
        }

        boolean hasValidAmount = RightClickHandler.validTargetSlot() && RightClickHandler.getTargetSlot().getStack().getCount() > 0;

        if (SplitScreenHandler.isScreenOpen() && hasValidAmount) {
            // ---- Render Split Screen Tooltip ----
            SplitScreenHandler.updateSplitSlider();

            // Disable depth testing and push the slider to the front
            context.getMatrices().pushMatrix();

            // Draw tooltip
            int slotX = getItemSlotX(RightClickHandler.getTargetSlot());
            int slotY = getItemSlotY(RightClickHandler.getTargetSlot());
            SplitScreen.drawTooltip(context, this.textRenderer, slotX, slotY, RightClickHandler.getTargetSlot());

            // Reset the offset and re-enable depth testing
            context.getMatrices().popMatrix();
        }
    }

    @Unique
    private static float getAlpha(float progress) {
        float alpha = 1.0f;

        if (ConfigScreen.AnimationSettings.enableAnimations) {
            if (RightClickHandler.isCharging()) {
                // Fade IN: 0.0 -> 1.0
                float fadeInPercent = Math.min(progress, 1.0f);
                alpha = ItemSplitterUtils.easeOutQuart(fadeInPercent);
            } else if (RightClickHandler.isFadingOut()) {
                // Fade OUT: 1.0 -> 0.0
                float fadeOutPercent = RightClickHandler.getFadeOutPercent();
                alpha = 1.0f - ItemSplitterUtils.easeOutQuart(fadeOutPercent);
            } else {
                // Hide when fade out is complete
                alpha = 0.0f;
            }
        }
        return alpha;
    }
}