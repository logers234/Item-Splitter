package net.logangwin.itemsplitter.gui;

import net.logangwin.itemsplitter.logic.RightClickHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;

public class CreativeSplitBarComponent implements TooltipComponent {

    private final double progress;
    private final int width;
    private final int height;
    private final int barHeight = 2;
    private final int heightPadding = 5;
    private final int barWidth = 50;
    private final int barPadding = 4;
    private final float textScale = 0.75F;
    private final int thumbHeight = 4;
    private final int thumbPadding = 2;
    private final int textHeight;
    private final int maxTextWidth;

    CreativeSplitBarComponent(TextRenderer textRenderer, double progress, int maxCount) {
        this.progress = progress;
        this.textHeight = (int) (textRenderer.fontHeight * textScale);

        // Calculate total tooltip width
        this.width = this.barWidth + (this.barPadding * 2);

        // Calculate total tooltip height
        this.height = barHeight + thumbPadding + textHeight;

        // Calculate max text width
        this.maxTextWidth = (int) (textRenderer.getWidth(String.valueOf(maxCount)) * textScale);
    }

    @Override
    public int getHeight() {
        return height + heightPadding;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return width;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        // Calculate offset
        int currentItems = Math.round((float) (RightClickHandler.targetSlot.getStack().getMaxCount() * progress));
        String text = String.valueOf(currentItems);
        int textWidth = (int) (textRenderer.getWidth(text) * textScale);
        int progressWidth = (currentItems * barWidth) / RightClickHandler.targetSlot.getStack().getMaxCount();

        // Render split bar
        drawSplitBar(context, textRenderer, x, y);

        // Calculate text positions
        int textAnchor = x + progressWidth + barPadding;
        int textX = textAnchor - (textWidth / 2);
        int textY = y + height - (thumbHeight / 2) - thumbPadding - textHeight;

        // Draw text
        drawPickupText(textRenderer, context, text, textX, textY);
    }

    private void drawSplitBar(DrawContext context, TextRenderer textRenderer, int x, int y) {
        // Values
        int thumbWidth = 2;

        // Calculate offsets
        int offset = (int) Math.floor(heightPadding / 2.0F);
        int center = (int) Math.floor(this.getWidth(textRenderer) / 2.0F);
        int barX = x + center - (barWidth / 2);
        int barY = y + height;

        // Background
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF292929);

        // Calculate the width of split bar based on how many items are being split
        int currentItems = Math.round((float) (RightClickHandler.targetSlot.getStack().getMaxCount() * progress));
        int progressWidth = (currentItems * barWidth) / RightClickHandler.targetSlot.getStack().getMaxCount();

        // Draw split bar
        context.fill(barX, barY, barX + progressWidth, barY + barHeight, 0xFFFFFFFF);

        // Draw split bar thumb
        context.fill(
                barX + progressWidth - (thumbWidth / 2),
                barY + (barHeight / 2) + (thumbHeight / 2),
                barX + progressWidth + (thumbWidth / 2),
                barY + (barHeight / 2) - (thumbHeight / 2),
                0xFFFFFFFF
        );
    }

    private void drawPickupText(TextRenderer textRenderer, DrawContext context, String text, int textX, int textY) {
        // Push the stack
        context.getMatrices().push();

        // Translate to text origin point
        context.getMatrices().translate(textX, textY, 0);

        // Apply the scale factor
        context.getMatrices().scale(textScale, textScale, 1.0f);

        // Draw the text
        context.drawText(textRenderer, text, 0, 0, 0xFFFFFFFF, true);

        // Reset context
        context.getMatrices().pop();
    }
}
