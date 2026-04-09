package net.logangwin.itemsplitter.gui;

import net.logangwin.itemsplitter.logic.SplitScreenLogic;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Unique;

public class SplitBarComponent implements TooltipComponent {

    private final double progress;

    SplitBarComponent (double progress) {
        this.progress = progress;
    }

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
        int width = 50;
        int height = 2;

        // Background
        context.fill(x, y + 2, x + width, y + 2 + height, 0xFF292929);

        // Calculate how many items are being split
        int currentItems = Math.round((float) (SplitScreenLogic.getMaxSplit() * progress));

        // Calculate width based on the ratio of items
        int progressWidth = (currentItems * width) / SplitScreenLogic.getMaxSplit();

        // Draw bar
        context.fill(x, y + 2, x + progressWidth, y + 2 + height, 0xFFFFFFFF);
    }
}
