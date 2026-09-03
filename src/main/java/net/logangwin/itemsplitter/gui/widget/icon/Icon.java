package net.logangwin.itemsplitter.gui.widget.icon;

import net.logangwin.itemsplitter.logic.ItemSplitterUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public abstract class Icon {

    private final int width;
    private final int height;
    private final Identifier TEXTURE;

    Icon(Identifier texture) {
        // Assign variables
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

    public void drawIcon(DrawContext context, int x, int y) {
        context.drawTexture(this.TEXTURE, x, y, 0, 0, this.width, this.height, this.width, this.height);
    }
}
