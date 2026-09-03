package net.logangwin.itemsplitter.gui;

import net.logangwin.itemsplitter.gui.widget.SplitBar;
import net.logangwin.itemsplitter.gui.widget.icon.DropIcon;
import net.logangwin.itemsplitter.gui.widget.icon.PickupIcon;
import net.logangwin.itemsplitter.gui.widget.ItemIndicator;
import net.logangwin.itemsplitter.logic.SplitScreenLogic;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;

public class SplitBarComponent implements TooltipComponent {

    private final double progress;
    private final int width;
    private final int height;
    private final int heightPadding = 5;
    private final int barPadding = 2;
    private final float textScale = 0.75F;
    private final int maxTextWidth;

    private final ItemIndicator pickupIndicator;
    private final ItemIndicator dropIndicator;
    private final SplitBar splitBar;

    SplitBarComponent (TextRenderer textRenderer, double progress, int stackSize) {
        this.progress = progress;

        // Calculate maximum text width
        int maxTextWidth = textRenderer.getWidth(String.valueOf(stackSize));
        this.maxTextWidth = (int) (maxTextWidth * textScale);

        // Initialize pickup and drop indicators
        this.pickupIndicator = new ItemIndicator(textRenderer, new PickupIcon(), maxTextWidth, textScale);
        this.dropIndicator = new ItemIndicator(textRenderer, new DropIcon(), maxTextWidth, textScale);
        this.splitBar = new SplitBar();

        // Calculate total tooltip width
        this.width = this.pickupIndicator.getWidth() + this.dropIndicator.getWidth() + this.splitBar.getWidth() + (barPadding * 2);
        this.height = Math.max(Math.max(this.pickupIndicator.getHeight(), this.dropIndicator.getHeight()), this.splitBar.getHeight());
    }

    @Override
    public int getHeight() {
        return height;
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
        int splitBarX = x + dropIndicator.getWidth();
        int splitBarY = y + (height / 2) - (splitBar.getHeight() / 2);
        splitBar.drawSplitBar(textRenderer, context, progress, splitBarX, splitBarY);

        // Draw pickup indicator
        int pickupIndicatorX = x + dropIndicator.getWidth() + splitBar.getWidth();
        pickupIndicator.drawIndicator(textRenderer, context, pickupIndicatorX, y, currentItems);
    }
}
