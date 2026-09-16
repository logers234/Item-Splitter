package net.logangwin.itemsplitter.gui;

import net.logangwin.itemsplitter.logic.ItemSplitterUtils;
import net.logangwin.itemsplitter.logic.SplitScreenLogic;
import net.logangwin.itemsplitter.mixin.DrawContextInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

public class SplitScreen {
    private static List<TooltipComponent> components;
    private static final TooltipPositioner positioner = HoveredTooltipPositioner.INSTANCE;
    private static double progress = 0.5F;

    public static void initialize() {
        // Create space for slider to go
        components = new ArrayList<>();
    }

    public static void drawTooltip(DrawContext context, TextRenderer textRenderer, int slotX, int slotY, Slot targetSlot) {

            boolean creativeSlot = ItemSplitterUtils.isCreativeSlot(ItemSplitterUtils.getCurrentScreen(), targetSlot);
            MinecraftClient client = MinecraftClient.getInstance();
            assert client.player != null;

            if (client.player.isCreative() && creativeSlot) {
                // Add creative mode split bar to the component list
                int maxCount = targetSlot.getStack().getMaxCount();
                components.add(new CreativeSplitScreenComponent(textRenderer, progress, maxCount));
            }
            else {
                // Add standard split bar to the component list
                int stackSize = targetSlot.getStack().getCount();
                components.add(new SplitScreenComponent(textRenderer, SplitScreen.progress, stackSize));
            }
            // Cancel out tooltip offsets and center above target slot
            slotX -= 12;
            slotX -= (components.getFirst().getWidth(textRenderer) / 2);
            slotY -= 10 + (components.getFirst().getHeight() / 2);

            if (ConfigScreen.INSTANCE.enableAnimations) {
                int animationOffsetY = 20;
                slotY -= animationOffsetY;
                int currentAnimationOffsetY = (int) (animationOffsetY * easeOutExpo(SplitScreenLogic.getAnimationProgress()));
                slotY += currentAnimationOffsetY;
            }

            // Render the tooltip at that specific spot
            ((DrawContextInvoker) context).itemsplitter$invokeComponentTooltip(textRenderer, components, slotX, slotY, positioner);

            // Clear the list
            components.clear();
    }

    public static void updateSplitSlider(double progress) {
        // Ensure progress percentage is within bounds
        if (progress > 1.0F) {
            SplitScreen.progress = 1.0F;
        } else if (progress < 0.0F) {
            SplitScreen.progress = 0.0F;
        } else {
            SplitScreen.progress = progress;
        }
    }

    private static float easeOutExpo(float x) {
        return x == 1 ? 1 : 1 - (float) (Math.pow(2, -10 * x));
    }
}
