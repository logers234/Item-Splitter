package net.logangwin.itemsplitter.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;

public class ChargeCircleHud {

    public static void drawProgressRing(DrawContext context, int x, int y, float progress) {
        int backgroundColor = 0xF0100010;

        // The Trail fades from transparent/dim to bright white at the leading edge
        int startColor = 0x00FFFFFF; // Fully transparent white at origin
        int endColor = 0xFFFFFFFF;   // Fully opaque white at leading edge

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        drawCircle(context, x, y, 2.5F, 5.5F, 1F, backgroundColor);
        drawGradientCircle(context, x, y, 3, 5, progress, startColor, endColor);

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void drawCircle(DrawContext context, int x, int y, float innerRadius, float outerRadius, float progress, int color) {
        if (progress <= 0) return;
        if (progress > 1F) {
            progress = 1F;
        }

        // Extract color values (ARGB format)
        float alpha = ((color >> 24) & 0xFF) / 255f;
        float red = ((color >> 16) & 0xFF) / 255f;
        float green = ((color >> 8) & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        int segments = 60;
        int endSegment = (int) (segments * progress);

        for (int i = 0; i <= endSegment; i++) {
            double angle = Math.toRadians((i * 360.0 / segments) - 90); // Start from the top
            float dx = (float) Math.cos(angle);
            float dy = (float) Math.sin(angle);

            // Outer vertex
            bufferBuilder.vertex(matrix, x + dx * outerRadius, y + dy * outerRadius, 0)
                    .color(red, green, blue, alpha);
            // Inner vertex
            bufferBuilder.vertex(matrix, x + dx * innerRadius, y + dy * innerRadius, 0)
                    .color(red, green, blue, alpha);
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    private static void drawGradientCircle(DrawContext context, int x, int y, float innerRadius, float outerRadius, float progress, int startColor, int endColor) {
        if (progress <= 0) return;
        if (progress > 1F) {
            progress = 1F;
        }

        float startA = ((startColor >> 24) & 0xFF) / 255f;
        float startR = ((startColor >> 16) & 0xFF) / 255f;
        float startG = ((startColor >> 8) & 0xFF) / 255f;
        float startB = (startColor & 0xFF) / 255f;

        float endA = ((endColor >> 24) & 0xFF) / 255f;
        float endR = ((endColor >> 16) & 0xFF) / 255f;
        float endG = ((endColor >> 8) & 0xFF) / 255f;
        float endB = (endColor & 0xFF) / 255f;

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();

        int segments = 90;
        int endSegment = (int) (segments * progress);

        // 1. Draw the Main Arc Body
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        for (int i = 0; i <= endSegment; i++) {
            float delta = endSegment > 0 ? (float) i / endSegment : 1.0f;

            float r = startR + (endR - startR) * delta;
            float g = startG + (endG - startG) * delta;
            float b = startB + (endB - startB) * delta;
            float a = startA + (endA - startA) * delta;

            double angle = Math.toRadians((i * 360.0 / segments) - 90);
            float dx = (float) Math.cos(angle);
            float dy = (float) Math.sin(angle);

            bufferBuilder.vertex(matrix, x + dx * outerRadius, y + dy * outerRadius, 0).color(r, g, b, a);
            bufferBuilder.vertex(matrix, x + dx * innerRadius, y + dy * innerRadius, 0).color(r, g, b, a);
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        // 2. Draw Semicircle Cap on Leading Edge
        if (endSegment > 0) {
            double capAngle = Math.toRadians((endSegment * 360.0 / segments) - 90);
            float capDx = (float) Math.cos(capAngle);
            float capDy = (float) Math.sin(capAngle);

            // Center point of the cap ring section
            float capRadius = (outerRadius - innerRadius) / 2.0f;
            float midRadius = innerRadius + capRadius;
            float capCenterX = x + capDx * midRadius;
            float capCenterY = y + capDy * midRadius;

            // Tangent angle facing forward along the path
            double forwardAngle = capAngle + Math.PI / 2.0;

            BufferBuilder capBuffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

            // Fan center
            capBuffer.vertex(matrix, capCenterX, capCenterY, 0).color(endR, endG, endB, endA);

            // Sweep 180 degrees (+PI/2 to -PI/2 relative to forward vector)
            int capSegments = 16;
            for (int j = 0; j <= capSegments; j++) {
                double offset = (Math.PI / 2.0) - (j * Math.PI / capSegments);
                double arcAngle = forwardAngle + offset;

                float vx = capCenterX + (float) Math.cos(arcAngle) * capRadius;
                float vy = capCenterY + (float) Math.sin(arcAngle) * capRadius;

                capBuffer.vertex(matrix, vx, vy, 0).color(endR, endG, endB, endA);
            }
            BufferRenderer.drawWithGlobalProgram(capBuffer.end());
        }

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}
