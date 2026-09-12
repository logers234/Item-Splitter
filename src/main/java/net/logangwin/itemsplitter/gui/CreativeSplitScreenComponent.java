package net.logangwin.itemsplitter.gui;

import net.logangwin.itemsplitter.ItemSplitterClient;
import net.logangwin.itemsplitter.gui.widget.ItemIndicator;
import net.logangwin.itemsplitter.gui.widget.SplitBar;
import net.logangwin.itemsplitter.logic.RightClickHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;

public class CreativeSplitScreenComponent implements TooltipComponent {

    private final double progress;
    private final int width;
    private final int height;
    private final int barPadding = 8;

    private final ItemIndicator pickupIndicator;
    private final SplitBar splitBar;

    CreativeSplitScreenComponent(TextRenderer textRenderer, double progress, int maxCount) {
        this.progress = progress;
        float indicatorScale = 1F;

        // Calculate maximum text width
        int maxTextWidth = textRenderer.getWidth(String.valueOf(maxCount));

        // Initialize widgets
        this.pickupIndicator = new ItemIndicator(textRenderer, ItemSplitterClient.getCurrentPickupIcon(), maxTextWidth, indicatorScale);
        this.splitBar = new SplitBar();

        // Calculate tooltip dimensions
        this.width = this.pickupIndicator.getWidth() + this.splitBar.getWidth() + barPadding;
        this.height = Math.max(this.pickupIndicator.getHeight(), this.splitBar.getHeight());
    }

    @Override
    public int getHeight() {
        int bottomPadding = 2;
        return height + bottomPadding;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return width;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        // Draw split bar
        int splitBarX = x + (barPadding / 2);
        int splitBarY = y + (height / 2) - (splitBar.getHeight() / 2);
        int currentItems = Math.round((float) (RightClickHandler.targetSlot.getStack().getMaxCount() * progress));
        splitBar.drawSplitBar(context, currentItems, RightClickHandler.targetSlot.getStack().getMaxCount(), splitBarX, splitBarY);

        // Draw pickup indicator
        int indicatorX = x + splitBar.getWidth() + barPadding;
        pickupIndicator.drawIndicator(textRenderer, context, indicatorX, y, currentItems);
    }
}
