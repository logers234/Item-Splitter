package net.logangwin.itemsplitter.gui.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class ItemIndicator {

    private final Icon icon;
    private final int indicatorWidth;
    private final int indicatorHeight;
    private final float indicatorScale;

    public ItemIndicator(TextRenderer textRenderer, Icon icon, int maxTextWidth, float indicatorScale) {
        // Assign variables
        this.icon = icon;
        this.indicatorScale = indicatorScale;


        // Determine the width of indicator
        this.indicatorWidth = (int) (Math.max(this.icon.getWidth(), maxTextWidth) * this.indicatorScale);

        // Determine the height of the indicator
        this.indicatorHeight = (int) (Math.max(icon.getHeight(), textRenderer.fontHeight) * this.indicatorScale);

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

        // Icon center x and y
        int iconCenterX = (icon.getWidth() / 2);
        int iconCenterY = (icon.getHeight() / 2);

        // Centered text coordinates
        int textX = iconCenterX - (textWidth / 2) + 1;
        int textY = iconCenterY - (textRenderer.fontHeight / 2);

        // Setup scale
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(this.indicatorScale, this.indicatorScale, 1.0f);

        // Draw icon and text
        icon.drawIcon(context, 0, 0);
        context.drawText(textRenderer, text, textX, textY, 0xFFFFFFFF, false);

        // Reset stack
        context.getMatrices().pop();

    }
}

