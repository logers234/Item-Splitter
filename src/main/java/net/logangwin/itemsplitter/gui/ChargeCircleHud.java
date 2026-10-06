package net.logangwin.itemsplitter.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureSetup;

public class ChargeCircleHud {

    private static float finalCos2 = 0;
    private static float finalSin2 = 0;
    private static final RenderPipeline CIRCLE_PIPELINE =
            RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
                    .withLocation("pipeline/item_splitter_circle")
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS)
                    .withCull(false)
                    .build();

    public static void drawProgressRing(GuiRenderState guiRenderState, int x, int y, float progress, float alpha) {
        alpha = Math.clamp(alpha, 0.0F, 1.0F);
        progress = Math.clamp(progress, 0.0F, 1.0F);
        float backgroundAlpha = Math.clamp(alpha, 0.0F, 0.94F);

        int alphaBits = ((int) (alpha * 255.0F)) << 24;
        int backgroundAlphaBits = ((int) (backgroundAlpha * 255.0F)) << 24;

        int backgroundColor = backgroundAlphaBits | 0x100010;

        // Background Ring
        drawCircle(guiRenderState, x, y, 2.5F, 5.5F, backgroundColor);

        // Progress Ring
        int startColor = 0x00FFFFFF;
        int endColor = ((int) (alpha * 255.0F) << 24) | 0xFFFFFF;
        float gradCircleInnerRad = 3.0F;
        float gradCircleOuterRad = 5.0F;
        drawGradientCircle(guiRenderState, x, y, 3.0F, 5.0F, progress, startColor, endColor);

        if (progress > 0.0F) {
            drawLeadingCap(guiRenderState, x, y, 3.0F, 5.0F, progress, endColor);
        }
    }

    private static void drawCircle(GuiRenderState guiRenderState, int x, int y, float innerRadius, float outerRadius, int color) {
        drawGradientCircle(guiRenderState, x, y, innerRadius, outerRadius, 1.0F, color, color);
    }

    private static void drawLeadingCap(GuiRenderState guiRenderState, int x, int y, float innerRadius, float outerRadius, float progress, int color) {
        if (progress <= 0.0F || progress >= 1.0F) {
            return;
        }

        float centerRadius = (innerRadius + outerRadius) / 2.0F;
        float capRadius = (outerRadius - innerRadius) / 2.0F;

        float endAngle = -(float) Math.PI / 2.0F + progress * (float) (Math.PI * 2.0F);

        float centerX = x + (float) Math.cos(endAngle) * centerRadius;
        float centerY = y + (float) Math.sin(endAngle) * centerRadius;

        int segments = 16;

        float[] vertices = new float[segments * 4 * 2];
        int[] colors = new int[segments * 4];

        int vertexIndex = 0;

        for (int i = 0; i < segments; i++) {
            float t1 = (float) i / segments;
            float t2 = (float) (i + 1) / segments;

            float angle1 = endAngle - (float) Math.PI / 2.0F + t1 * (float) Math.PI;
            float angle2 = endAngle - (float) Math.PI / 2.0F + t2 * (float) Math.PI;

            float x1 = centerX + (float) Math.cos(angle1) * capRadius;
            float y1 = centerY + (float) Math.sin(angle1) * capRadius;

            float x2 = centerX + (float) Math.cos(angle2) * capRadius;
            float y2 = centerY + (float) Math.sin(angle2) * capRadius;

            vertices[vertexIndex * 2] = centerX;
            vertices[vertexIndex * 2 + 1] = centerY;
            colors[vertexIndex++] = color;

            vertices[vertexIndex * 2] = x1;
            vertices[vertexIndex * 2 + 1] = y1;
            colors[vertexIndex++] = color;
        }

        guiRenderState.addSimpleElement(new CircleGuiElementRenderState(CIRCLE_PIPELINE, TextureSetup.empty(), vertices, colors, null));
    }

    private static void drawGradientCircle(GuiRenderState guiRenderState, int x, int y, float innerRadius, float outerRadius, float progress, int startColor, int endColor) {
        if (progress <= 0.0F) {
            return;
        }

        progress = Math.clamp(progress, 0.0F, 1.0F);

        int segments = 64;
        int activeSegments = (int) Math.ceil(segments * progress);
        float finalCos2 = 0;
        float finalSin2 = 0;

        float[] vertices = new float[activeSegments * 8];
        int[] colors = new int[activeSegments * 4];

        float fullAngle = (float) (Math.PI * 2.0);
        float startAngle = -(float) Math.PI / 2.0F;

        int vertexIndex = 0;
        int colorIndex = 0;

        for (int i = 0; i < activeSegments; i++) {
            float t1 = (float) i / segments;
            float t2 = Math.min((float) (i + 1) / segments, progress);

            float angle1 = startAngle + t1 * fullAngle;
            float angle2 = startAngle + t2 * fullAngle;

            float cos1 = (float) Math.cos(angle1);
            float sin1 = (float) Math.sin(angle1);
            float cos2 = (float) Math.cos(angle2);
            float sin2 = (float) Math.sin(angle2);

            float outerX1 = x + cos1 * outerRadius;
            float outerY1 = y + sin1 * outerRadius;
            float outerX2 = x + cos2 * outerRadius;
            float outerY2 = y + sin2 * outerRadius;

            float innerX1 = x + cos1 * innerRadius;
            float innerY1 = y + sin1 * innerRadius;
            float innerX2 = x + cos2 * innerRadius;
            float innerY2 = y + sin2 * innerRadius;

            int color1 = interpolateColor(startColor, endColor, t1);
            int color2 = interpolateColor(startColor, endColor, t2);

            vertices[vertexIndex++] = outerX1;
            vertices[vertexIndex++] = outerY1;

            vertices[vertexIndex++] = outerX2;
            vertices[vertexIndex++] = outerY2;

            vertices[vertexIndex++] = innerX2;
            vertices[vertexIndex++] = innerY2;

            vertices[vertexIndex++] = innerX1;
            vertices[vertexIndex++] = innerY1;

            colors[colorIndex++] = color1;
            colors[colorIndex++] = color2;
            colors[colorIndex++] = color2;
            colors[colorIndex++] = color1;

            if (i == activeSegments - 1) {
                finalCos2 = cos2;
                finalSin2 = sin2;
            }
        }

        guiRenderState.addSimpleElement(new CircleGuiElementRenderState(CIRCLE_PIPELINE, TextureSetup.empty(), vertices, colors, null));
    }

    private static int interpolateColor(int startColor, int endColor, float progress) {
        progress = Math.clamp(progress, 0.0F, 1.0F);

        int startA = (startColor >>> 24) & 0xFF;
        int startR = (startColor >>> 16) & 0xFF;
        int startG = (startColor >>> 8) & 0xFF;
        int startB = startColor & 0xFF;

        int endA = (endColor >>> 24) & 0xFF;
        int endR = (endColor >>> 16) & 0xFF;
        int endG = (endColor >>> 8) & 0xFF;
        int endB = endColor & 0xFF;

        int a = (int) (startA + (endA - startA) * progress);
        int r = (int) (startR + (endR - startR) * progress);
        int g = (int) (startG + (endG - startG) * progress);
        int b = (int) (startB + (endB - startB) * progress);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}