package net.logangwin.itemsplitter.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record CircleGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, float[] vertices, int[] colors, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SimpleGuiElementRenderState {

    public CircleGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, float[] vertices, int[] colors, @Nullable ScreenRect scissorArea) {
        this(pipeline, textureSetup, vertices, colors, scissorArea, createBounds(vertices, scissorArea));
    }

    @Override
    public void setupVertices(VertexConsumer vertexConsumer) {
        for (int i = 0; i < vertices.length / 2; i++) {
            vertexConsumer
                    .vertex(vertices[i * 2], vertices[i * 2 + 1], 0.0F)
                    .color(colors[i]);
        }
    }

    @Nullable
    private static ScreenRect createBounds(float[] vertices, @Nullable ScreenRect scissorArea) {
        if (vertices.length < 2) {
            return null;
        }

        float minX = vertices[0];
        float minY = vertices[1];
        float maxX = vertices[0];
        float maxY = vertices[1];

        for (int i = 2; i < vertices.length; i += 2) {
            minX = Math.min(minX, vertices[i]);
            minY = Math.min(minY, vertices[i + 1]);
            maxX = Math.max(maxX, vertices[i]);
            maxY = Math.max(maxY, vertices[i + 1]);
        }

        ScreenRect rect = new ScreenRect((int) Math.floor(minX), (int) Math.floor(minY), (int) Math.ceil(maxX - minX), (int) Math.ceil(maxY - minY));

        return scissorArea != null ? scissorArea.intersection(rect) : rect;
    }
}
