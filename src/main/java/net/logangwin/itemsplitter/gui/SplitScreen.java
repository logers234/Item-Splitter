package net.logangwin.itemsplitter.gui;

import net.logangwin.itemsplitter.ItemSplitter;
import net.logangwin.itemsplitter.logic.ItemSplitterUtils;
import net.logangwin.itemsplitter.logic.RightClickHandler;
import net.logangwin.itemsplitter.mixin.DrawContextInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.screen.slot.Slot;

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

            boolean creativeSlot = ItemSplitterUtils.isCreativeSlot(ItemSplitterUtils.getCurrentScreen(), RightClickHandler.targetSlot);
            MinecraftClient client = MinecraftClient.getInstance();
            assert client.player != null;

            if (client.player.isCreative() && creativeSlot) {
                // Add creative mode split bar to the component list
                int maxCount = RightClickHandler.targetSlot.getStack().getMaxCount();
                components.add(new CreativeSplitBarComponent(textRenderer, progress, maxCount));
            }
            else {
                // Add standard split bar to the component list
                int stackSize = RightClickHandler.targetSlot.getStack().getCount();
                components.add(new SplitBarComponent(textRenderer, SplitScreen.progress, stackSize));
            }
            // Cancel out tooltip offsets
            slotX -= 12;

            // Center screen above target slot
            slotX -= (components.getFirst().getWidth(textRenderer) / 2);
            slotY -= 10 + (components.getFirst().getHeight() / 2);

            // Render the tooltip at that specific spot
            ((DrawContextInvoker) context).itemsplitter$invokeComponentTooltip(textRenderer, components, slotX, slotY, positioner);

            // Clear the list
            components.clear();
    }

    public static void updateSplitSlider(double progress) {
        // Ensure progress percentage is within bounds
        if (progress > 1.0F) {
            SplitScreen.progress = 1.0F;
        }
        else if (progress < 0.0F) {
            SplitScreen.progress = 0.0F;
        }
        else {
            SplitScreen.progress = progress;
        }
    }
}
