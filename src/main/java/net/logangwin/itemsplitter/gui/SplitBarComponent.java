package net.logangwin.itemsplitter.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;

public class SplitBarComponent implements TooltipComponent {

    @Override
    public int getHeight() {
        return 6;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 50;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        float progress = 0.5F;
        int width = 50;
        int height = 2;

        // Background
        context.fill(x, y + 2, x + width, y + 2 + height, 0xFF292929);
        // Progress
        int progressWidth = (int) (width * progress);
        context.fill(x, y + 2, x + progressWidth, y + 2 + height, 0xFFFFFFFF);
    }
}
