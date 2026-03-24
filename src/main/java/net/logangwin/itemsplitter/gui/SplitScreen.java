package net.logangwin.itemsplitter.gui;

import net.logangwin.itemsplitter.mixin.DrawContextInvoker;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;

public class SplitScreen {
    private static List<TooltipComponent> components;
    private static final TooltipPositioner positioner = HoveredTooltipPositioner.INSTANCE;

    public static void initialize() {
        // Create space for slider to go
        components = new ArrayList<>();
    }

    public static void drawTooltip(DrawContext context, TextRenderer textRenderer, int slotX, int slotY, Slot targetSlot) {
            // Add split bar to the component list
            components.add(new SplitBarComponent(targetSlot));

            // Render the tooltip at that specific spot
            ((DrawContextInvoker) context).itemsplitter$invokeComponentTooltip(textRenderer, components, slotX, slotY, positioner);

            // Clear the list
            components.clear();
    }
}
