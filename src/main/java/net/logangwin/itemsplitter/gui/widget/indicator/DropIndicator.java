package net.logangwin.itemsplitter.gui.widget.indicator;


import net.logangwin.itemsplitter.gui.widget.icon.DropIcon;
import net.logangwin.itemsplitter.gui.widget.icon.Icon;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class DropIndicator {

    private final Icon icon;
    private final int indicatorWidth;
    private final int indicatorHeight;
    private final int maxTextWidth;
    private final float indicatorScale;

    DropIndicator(TextRenderer textRenderer, int maxTextWidth, float indicatorScale) {
        // Assign variables
        this.icon = new DropIcon();
        this.maxTextWidth = maxTextWidth;
        this.indicatorScale = indicatorScale;

        // Determine the width of indicator
        this.indicatorWidth = (int) (Math.max(this.icon.getWidth(), maxTextWidth) * indicatorScale);

        // Determine the height of the indicator
        this.indicatorHeight = (int) ((icon.getHeight() + textRenderer.fontHeight) * indicatorScale);

    }

    public int getHeight() {
        return indicatorHeight;
    }

    public int getWidth() {
        return indicatorWidth;
    }

    public void drawIndicator(TextRenderer textRenderer, DrawContext context, int x, int y, int currentItems) {
        // Variables
        String text = String.valueOf(currentItems);
        int textWidth = textRenderer.getWidth(text);

        // Unscaled center axis
        int unscaledWidth = Math.max(this.icon.getWidth(), maxTextWidth);
        int unscaledCenterX = unscaledWidth / 2;

        // Position components relative to the unscaled center
        int iconX = unscaledCenterX - (icon.getWidth() / 2);
        int textX = unscaledCenterX - (textWidth / 2);
        int textY = icon.getHeight();

        // Push the stack
        context.getMatrices().push();

        // Translate to indicator origin point
        context.getMatrices().translate(x, y, 0);

        // Apply the scale factor
        context.getMatrices().scale(indicatorScale, indicatorScale, 1.0f);

        // Draw icon
        icon.drawIcon(context, iconX, 0);

        // Draw the text
        context.drawText(textRenderer, text, textX, textY, 0xFFFFFFFF, true);

        // Reset context
        context.getMatrices().pop();
    }
}

