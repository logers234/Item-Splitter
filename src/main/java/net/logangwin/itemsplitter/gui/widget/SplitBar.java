package net.logangwin.itemsplitter.gui.widget;

import net.logangwin.itemsplitter.logic.SplitScreenLogic;
import net.minecraft.client.gui.DrawContext;

public class SplitBar {

    private final int barHeight = 2;
    private final int barWidth = 50;
    private final int thumbWidth;
    private final int thumbHeight;
    private final int borderColorTop = 0x505000FF; // 31% opacity
    private final int borderColorBottom = 0x5028007F; // 31% opacity
    private final int borderThickness = 1;

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
        // Calculate the width of split bar based on how many items are being split
        int progressWidth = (currentItems * barWidth) / maxSplit;

        // Draw border
        drawSplitBarBorder(context, x, y, progressWidth);

        // Background
        context.fill(x, y, x + barWidth, y + barHeight, 0xFF292929);

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

    private void drawSplitBarBorder(DrawContext context, int x, int y, int progressWidth) {
        // ---- Vertical border shared variables ----
        int verticalBorderStartY = y - borderThickness;
        int verticalBorderEndY = y + barHeight + borderThickness;

        // Left border
        int leftBorderStartX = x - borderThickness;

        // If the slider is at its minimum, push the left border over to fill the gap
        if (progressWidth == 0) {
            context.fillGradient(leftBorderStartX - 1, verticalBorderStartY, x - 1, verticalBorderEndY, borderColorTop, borderColorBottom);
        }
        else {
            context.fillGradient(leftBorderStartX, verticalBorderStartY, x, verticalBorderEndY, borderColorTop, borderColorBottom);
        }

        // Right border
        int rightBorderStartX = x + barWidth;
        int rightBorderEndX = rightBorderStartX + borderThickness;

        // If the slider is at its maximum, push the right border over to fill the gap
        if (progressWidth == barWidth) {
            context.fillGradient(rightBorderStartX + 1, verticalBorderStartY, rightBorderEndX + 1, verticalBorderEndY, borderColorTop, borderColorBottom);
        }
        else {
            context.fillGradient(rightBorderStartX, verticalBorderStartY, rightBorderEndX, verticalBorderEndY, borderColorTop, borderColorBottom);
        }

        // ---- Horizontal border shared variables ----
        int topHorizontalBorderStartY = y - borderThickness;
        int bottomHorizontalBorderStartY = y + barHeight;
        int horizontalLeftBorderEndX = x + progressWidth - (thumbWidth / 2);
        int horizontalRightBorderEnd = x + barWidth;

        // Top border
        context.fill(x, topHorizontalBorderStartY, horizontalLeftBorderEndX, y, borderColorTop);
        context.fill(horizontalLeftBorderEndX + thumbWidth, topHorizontalBorderStartY, horizontalRightBorderEnd, y, borderColorTop);

        // Bottom border
        int bottomLeftBorderEndY = y + barHeight + borderThickness;
        context.fill(x, bottomHorizontalBorderStartY, horizontalLeftBorderEndX, bottomLeftBorderEndY, borderColorBottom);
        context.fill(horizontalLeftBorderEndX + thumbWidth, bottomHorizontalBorderStartY, horizontalRightBorderEnd, bottomHorizontalBorderStartY + borderThickness, borderColorBottom);

        // ---- Thumb border shared variables ----
        int thumbBorderStartX = x + progressWidth - (thumbWidth / 2) - borderThickness;
        int thumbBorderEndX = x + progressWidth + (thumbWidth / 2) + borderThickness;

        // Thumb top
        int thumbTopBorderStartY = y - (thumbHeight / 2);
        context.fill(thumbBorderStartX, thumbTopBorderStartY, thumbBorderEndX, thumbTopBorderStartY + borderThickness, borderColorTop);

        // Thumb bottom
        int thumbBottomBorderStartY = y + barHeight + (thumbHeight / 2);
        context.fill(thumbBorderStartX, thumbBottomBorderStartY, thumbBorderEndX, thumbBottomBorderStartY - borderThickness, borderColorBottom);
    }
}
