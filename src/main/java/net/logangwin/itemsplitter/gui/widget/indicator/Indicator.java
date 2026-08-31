package net.logangwin.itemsplitter.gui.widget.indicator;

import net.logangwin.itemsplitter.logic.ItemSplitterUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public abstract class Indicator {

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final Identifier TEXTURE;

    Indicator(int x, int y, Identifier texture) {
        // Assign variables
        this.x = x;
        this.y = y;
        this.TEXTURE = texture;

        // Get PNG dimensions
        this.width = ItemSplitterUtils.getPngWidth(this.TEXTURE);
        this.height = ItemSplitterUtils.getPngHeight(this.TEXTURE);
    }

    public Identifier getTexture() {
        return this.TEXTURE;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void drawItems(DrawContext context, int x, int y, int width, int height) {

    }
}
