package net.logangwin.itemsplitter.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.logangwin.itemsplitter.ItemSplitterClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.render.state.ColoredQuadGuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureSetup;
import org.joml.Matrix3x2f;

public class ChargeCircleHud {

    private static final RenderPipeline CIRCLE_PIPELINE =
            RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
            .withLocation("pipeline/item_splitter_circle")
            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLES).withCull(false)
            .build();

    public static void drawProgressRing(GuiRenderState guiRenderState, int x, int y, float progress, float alpha) {

        int startColor = ((int) (alpha * 255) << 24) | 0xFFFFFF;
        int endColor = ((int) (alpha * 255) << 24) | 0xFFFFFF;
        int backgroundColor = ((int) (alpha * 255) << 24) | 0x100010;

        Matrix3x2f pose = new Matrix3x2f();
        pose.translate(x, y);
        guiRenderState.addSimpleElement(new ColoredQuadGuiElementRenderState(RenderPipelines.GUI, TextureSetup.empty(), pose, 1, 2, 3, 4, startColor, endColor, null));

        drawCircle(guiRenderState, x, y, 2.5F, 5.5F, 1.0F, backgroundColor);
        drawGradientCircle(guiRenderState, x, y, 3.0F, 5.0F, progress, startColor, endColor);
    }

    private static void drawCircle(GuiRenderState guiRenderState, int x, int y, float innerRadius, float outerRadius, float progress, int color) {
        drawGradientCircle(guiRenderState, x, y, innerRadius, outerRadius, progress, color, color);
    }

    private static void drawGradientCircle(GuiRenderState guiRenderState, int x, int y, float innerRadius, float outerRadius, float progress, int startColor, int endColor) {
        if (progress <= 0.0F) {
            return;
        }

        progress = Math.min(progress, 1.0F);

        int segments = Math.max(16, (int) (outerRadius * 2.0F));

        float[] vertices = new float[segments * 6 * 2];
        int[] colors = new int[segments * 6];

        int vertexIndex = 0;

        for (int i = 0; i < segments; i++) {
            float t1 = (float) i / segments;
            float t2 = (float) (i + 1) / segments;

            float angle1 = -((float) Math.PI / 2.0F) + t1 * progress * (float) (Math.PI * 2.0F);
            float angle2 = -((float) Math.PI / 2.0F) + t2 * progress * (float) (Math.PI * 2.0F);

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

            // Triangle 1
            vertexIndex = addVertex(vertices, colors, vertexIndex, outerX1, outerY1, color1);
            vertexIndex = addVertex(vertices, colors, vertexIndex, outerX2, outerY2, color2);
            vertexIndex = addVertex(vertices, colors, vertexIndex, innerX1, innerY1, color1);

            // Triangle 2
            vertexIndex = addVertex(vertices, colors, vertexIndex, outerX2, outerY2, color2);
            vertexIndex = addVertex(vertices, colors, vertexIndex, innerX2, innerY2, color2);
            vertexIndex = addVertex(vertices, colors, vertexIndex, innerX1, innerY1, color1);
        }

        guiRenderState.addSimpleElement(new CircleGuiElementRenderState(CIRCLE_PIPELINE, TextureSetup.empty(), vertices, colors, null));
    }

    private static int addVertex(float[] vertices, int[] colors, int index, float x, float y, int color) {
        vertices[index * 2] = x;
        vertices[index * 2 + 1] = y;
        colors[index] = color;

        return index + 1;
    }

    private static int interpolateColor(int startColor, int endColor, float progress) {
        progress = Math.max(0.0F, Math.min(1.0F, progress));

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