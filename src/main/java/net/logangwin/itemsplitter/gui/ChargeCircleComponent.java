package net.logangwin.itemsplitter.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import net.logangwin.itemsplitter.RightClickHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;

public class ChargeCircleComponent extends BaseComponent {
    protected float progress = 0f; // 0.0 to 1.0

    public void setProgress(float progress) {
        this.progress = Math.max(0, Math.min(1, progress));
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        // Center of the component
        int centerX = this.x + this.width / 2;
        int centerY = this.y + this.height / 2;

        if (RightClickHandler.isCharging()) {
            drawProgressRing(context, centerX, centerY, 4, 2, this.progress, 0xFFFFFFFF);
        } else {
            drawProgressRing(context, centerX, centerY, 4, 2, this.progress, 0X00000000);
        }
    }

    public static void drawProgressRing(DrawContext context, int x, int y, float radius, float thickness, float progress, int color) {
        if (progress <= 0) return;

        // Extract color values
        float alpha = ((color >> 24) & 0xFF) / 255f;
        float red = ((color >> 16) & 0xFF) / 255f;
        float green = ((color >> 8) & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        // Calculate points for the inner and outer edge of the ring
        int segments = 60;
        int endSegment = (int) (segments * progress);

        for (int i = 0; i <= endSegment; i++) {
            double angle = Math.toRadians((i * 360.0 / segments) - 90); // Start from the top
            float dx = (float) Math.cos(angle);
            float dy = (float) Math.sin(angle);

            // Outer vertex
            bufferBuilder.vertex(matrix, x + dx * radius, y + dy * radius, 0)
                    .color(red, green, blue, alpha);
            // Inner vertex
            bufferBuilder.vertex(matrix, x + dx * (radius - thickness), y + dy * (radius - thickness), 0)
                    .color(red, green, blue, alpha);
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }
}
