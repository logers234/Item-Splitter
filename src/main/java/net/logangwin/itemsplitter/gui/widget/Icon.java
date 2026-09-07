package net.logangwin.itemsplitter.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.logangwin.itemsplitter.logic.ItemSplitterUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class Icon {

    private int width = -1;
    private int height = -1;
    private final Identifier TEXTURE;

    public Icon(Identifier texture) {
        // Assign variables
        this.TEXTURE = texture;
    }

    public int getWidth() {
        // Calculate and set width when method is first called
        if (this.width == -1) {
            this.width = ItemSplitterUtils.getPngWidth(this.TEXTURE);
        }
        return this.width;
    }

    public int getHeight() {
        // Calculate and set height when method is first called
        if (this.height == -1) {
            this.height = ItemSplitterUtils.getPngHeight(this.TEXTURE);
        }
        return this.height;
    }

    public void drawIcon(DrawContext context, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        context.drawTexture(this.TEXTURE, x, y, 0, 0, this.width, this.height, this.width, this.height);
        RenderSystem.disableBlend();
    }
}
