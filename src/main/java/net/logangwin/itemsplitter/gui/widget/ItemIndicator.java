package net.logangwin.itemsplitter.gui.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class ItemIndicator {

    private final Icon icon;
    private final int indicatorWidth;
    private final int indicatorHeight;
    private final float indicatorScale;
    private final float alpha;
    private final int TEXT_COLOR;

    public ItemIndicator(TextRenderer textRenderer, Icon icon, int maxTextWidth, float indicatorScale, float alpha) {
        // Assign variables
        this.icon = icon;
        this.indicatorScale = indicatorScale;
        this.alpha = alpha;

        // Determine the width of indicator
        this.indicatorWidth = (int) (Math.max(this.icon.getWidth(), maxTextWidth) * this.indicatorScale);

        // Determine the height of the indicator
        this.indicatorHeight = (int) (Math.max(icon.getHeight(), textRenderer.fontHeight) * this.indicatorScale);

        float textAlpha = Math.clamp(alpha, 0.0F, 1.0F);
        int textAlphaBits = ((int) (textAlpha * 255.0F)) << 24;
        this.TEXT_COLOR = textAlphaBits | 0xFFFFFF;
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
        context.getMatrices().pushMatrix();
        context.getMatrices().translate(x, y);
        context.getMatrices().scale(this.indicatorScale, this.indicatorScale);

        // Draw icon and text
        icon.drawIcon(context, 0, 0, alpha);
        context.drawText(textRenderer, text, textX, textY, TEXT_COLOR, false);

        // Reset stack
        context.getMatrices().popMatrix();

    }
}

