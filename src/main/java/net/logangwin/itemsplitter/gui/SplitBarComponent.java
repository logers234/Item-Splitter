package net.logangwin.itemsplitter.gui;

import net.logangwin.itemsplitter.logic.SplitScreenLogic;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Unique;

public class SplitBarComponent implements TooltipComponent {

    private final double progress;
    private final int width;
    private final int height = 2;
    private final int heightPadding = 5;
    private final int barWidth = 50;
    private final int barPadding = 3;
    private final float textScale = 0.75F;
    private final int maxTextWidth;

    SplitBarComponent (double progress, TextRenderer textRenderer) {
        this.progress = progress;

        // Calculate maximum text width
        String temp = "00";
        int textWidth = (int) (textRenderer.getWidth(temp) * textScale);
        this.maxTextWidth = textWidth;
        
        // Calculate total tooltip width
        this.width = this.barWidth + (this.barPadding * 2) + (textWidth * 2);
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
        int center = (int) Math.floor(this.getWidth(textRenderer) / 2.0F);

        // Render split bar
        drawSplitBar(context, textRenderer, x, y);

        int currentItems = Math.round((float) (SplitScreenLogic.getMaxSplit() * progress));


        // Calculate the static X positions where the text bounding boxes should start/end
        int leftTextAnchor = x + center - (barWidth / 2) - barPadding;
        int rightTextAnchor = x + center + (barWidth / 2) + barPadding;

        // Left side text (Display number of items that will remain in inventory stack)
        String leftText = String.valueOf(SplitScreenLogic.getMaxSplit() - currentItems);
        int leftWidth = (int) (textRenderer.getWidth(leftText) * textScale);

        // Standardize the box boundary.
        int leftBoxX = leftTextAnchor - maxTextWidth;
        int leftTextX = leftBoxX + ((maxTextWidth - leftWidth) / 2);

        drawSplitText(textRenderer, context, leftText, leftTextX, y);

        // Right side text (Display number of items that will be picked up by cursor)
        String rightText = String.valueOf(currentItems);
        int rightWidth = (int) (textRenderer.getWidth(rightText) * textScale);

        // Left-align the box first, then shift inward by half the unused space to center it.
        int rightTextX = rightTextAnchor + ((maxTextWidth - rightWidth) / 2);

        drawSplitText(textRenderer, context, rightText, rightTextX, y);
    }

    @Unique
    private void drawSplitBar(DrawContext context, TextRenderer textRenderer, int x, int y) {
        // Values
        int thumbWidth = 2;
        int thumbHeight = 4;

        // Calculate offsets
        int offset = (int) Math.floor(heightPadding / 2.0F);
        int center = (int) Math.floor(this.getWidth(textRenderer) / 2.0F);
        int barX = x + center - (barWidth / 2);

        // Background
        context.fill(barX, y + offset, barX + barWidth, y + offset + height, 0xFF292929);

        // Calculate the width of split bar based on how many items are being split
        int currentItems = Math.round((float) (SplitScreenLogic.getMaxSplit() * progress));
        int progressWidth = (currentItems * barWidth) / SplitScreenLogic.getMaxSplit();

        // Draw split bar
        context.fill(barX, y + offset, barX + progressWidth, y + offset + height, 0xFFFFFFFF);

        // Draw split bar thumb
        context.fill(barX + progressWidth - (thumbWidth / 2),
                     y + offset + (height / 2) + (thumbHeight / 2),
                     barX + progressWidth + (thumbWidth / 2),
                     y + offset + (height / 2) - (thumbHeight / 2),
                    0xFFFFFFFF);
    }

    @Unique
    private void drawSplitText(TextRenderer textRenderer, DrawContext context, String text, int textX, int textY) {
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
