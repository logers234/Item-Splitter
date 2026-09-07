package net.logangwin.itemsplitter.gui.widget;

import net.logangwin.itemsplitter.logic.SplitScreenLogic;
import net.minecraft.client.gui.DrawContext;

public class SplitBar {

    private final int barHeight = 2;
    private final int barWidth = 50;
    private final int thumbWidth;
    private final int thumbHeight;

    public SplitBar() {
        thumbWidth = 2;
        thumbHeight = barHeight * 2;
    }

    public int getHeight() {
        return barHeight;
    }

    public int getWidth() {
        return barWidth;
    }

    public void drawSplitBar(DrawContext context, double progress, int x, int y) {
        int currentItems = Math.round((float) (SplitScreenLogic.getMaxSplit() * progress));
        this.drawSplitBar(context, currentItems, SplitScreenLogic.getMaxSplit(), x, y);
    }

    public void drawSplitBar(DrawContext context, int currentItems, int maxSplit, int x, int y) {
        // Background
        context.fill(x, y, x + barWidth, y + barHeight, 0xFF292929);

        // Calculate the width of split bar based on how many items are being split
        int progressWidth = (currentItems * barWidth) / maxSplit;

        // Draw split bar
        context.fill(x, y, x + progressWidth, y + barHeight, 0xFFFFFFFF);

        // Draw split bar thumb
        context.fill(
                x + progressWidth - (thumbWidth / 2),
                y + (barHeight / 2) + (thumbHeight / 2),
                x + progressWidth + (thumbWidth / 2),
                y + (barHeight / 2) - (thumbHeight / 2),
                0xFFFFFFFF
        );
    }
}
