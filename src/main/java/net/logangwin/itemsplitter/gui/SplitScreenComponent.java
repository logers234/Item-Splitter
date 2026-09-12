package net.logangwin.itemsplitter.gui;

import net.logangwin.itemsplitter.ItemSplitterClient;
import net.logangwin.itemsplitter.gui.widget.SplitBar;
import net.logangwin.itemsplitter.gui.widget.ItemIndicator;
import net.logangwin.itemsplitter.logic.SplitScreenLogic;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;

public class SplitScreenComponent implements TooltipComponent {

    private final double progress;
    private final int width;
    private final int height;
    private final int barPadding = 8;

    private final ItemIndicator pickupIndicator;
    private final ItemIndicator dropIndicator;
    private final SplitBar splitBar;

    SplitScreenComponent(TextRenderer textRenderer, double progress, int stackSize) {
        this.progress = progress;
        float indicatorScale = 1F;

        // Calculate maximum text width
        int maxTextWidth = textRenderer.getWidth(String.valueOf(stackSize));

        // Initialize widgets
        this.pickupIndicator = new ItemIndicator(textRenderer, ItemSplitterClient.getCurrentPickupIcon(), maxTextWidth, indicatorScale);
        this.dropIndicator = new ItemIndicator(textRenderer, ItemSplitterClient.getCurrentDropIcon(), maxTextWidth, indicatorScale);
        this.splitBar = new SplitBar();

        // Calculate tooltip dimensions
        this.width = this.pickupIndicator.getWidth() + this.dropIndicator.getWidth() + this.splitBar.getWidth() + barPadding;
        this.height = Math.max(Math.max(this.pickupIndicator.getHeight(), this.dropIndicator.getHeight()), this.splitBar.getHeight());
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
        int currentItems = Math.round((float) (SplitScreenLogic.getMaxSplit() * progress));

        // Draw drop indicator
        dropIndicator.drawIndicator(textRenderer, context, x, y, SplitScreenLogic.getMaxSplit() - currentItems);

        // Draw split bar
        int splitBarX = x + dropIndicator.getWidth() + (barPadding / 2);
        int splitBarY = y + (height / 2) - (splitBar.getHeight() / 2);
        splitBar.drawSplitBar(context, progress, splitBarX, splitBarY);

        // Draw pickup indicator
        int pickupIndicatorX = x + dropIndicator.getWidth() + splitBar.getWidth() + barPadding;
        pickupIndicator.drawIndicator(textRenderer, context, pickupIndicatorX, y, currentItems);
    }
}
