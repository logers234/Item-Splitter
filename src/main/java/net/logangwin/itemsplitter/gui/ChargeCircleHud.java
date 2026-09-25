package net.logangwin.itemsplitter.gui;

import net.logangwin.itemsplitter.ItemSplitterClient;
import net.minecraft.client.gui.DrawContext;

public class ChargeCircleHud {

    public static void drawProgressRing(DrawContext context, int x, int y, float progress, float alpha) {
        ItemSplitterClient.LOGGER.info("draw charge circle");
        int backgroundColor = 0xF0100010;

        int startColor = ((int) (alpha * 255) << 24) | 0xFFFFFF;
        int endColor = ((int) (alpha * 255) << 24) | 0xFFFFFF;

        drawCircle(context, x, y, 2.5F, 5.5F, 1F, backgroundColor);
        drawGradientCircle(context, x, y, 3F, 5F, progress, startColor, endColor);
    }

    private static void drawCircle(DrawContext context, int x, int y, float innerRadius, float outerRadius, float progress, int color) {
        drawGradientCircle(context, x, y, innerRadius, outerRadius, progress, color, color);
    }

    private static void drawGradientCircle(DrawContext context, int x, int y, float innerRadius, float outerRadius, float progress, int startColor, int endColor) {
        if (progress <= 0.0F) {
            return;
        }

        progress = Math.min(progress, 1.0F);

        int segments = Math.max(24, (int) (outerRadius * 2.0F * Math.PI * progress));

        for (int i = 0; i < segments; i++) {
            float t1 = (float) i / segments;
            float t2 = (float) (i + 1) / segments;

            float angle1 = t1 * progress * 2.0F * (float) Math.PI - (float) (Math.PI / 2.0);
            float angle2 = t2 * progress * 2.0F * (float) Math.PI - (float) (Math.PI / 2.0);

            float midAngle = (angle1 + angle2) * 0.5F;

            float cos = (float) Math.cos(midAngle);
            float sin = (float) Math.sin(midAngle);

            float radius = (innerRadius + outerRadius) * 0.5F;
            float halfWidth = (outerRadius - innerRadius) * 0.5F;

            float centerX = x + cos * radius;
            float centerY = y + sin * radius;

            float segmentLength = radius * (angle2 - angle1);

            float left = centerX - segmentLength * 0.5F;
            float top = centerY - halfWidth;
            float right = centerX + segmentLength * 0.5F;
            float bottom = centerY + halfWidth;

            int color = interpolateColor(startColor, endColor, t1);

            context.fill(
                    (int) left,
                    (int) top,
                    (int) right + 1,
                    (int) bottom + 1,
                    color
            );
        }
    }

    private static int interpolateColor(int startColor, int endColor, float progress) {
        int a1 = (startColor >>> 24) & 0xFF;
        int r1 = (startColor >>> 16) & 0xFF;
        int g1 = (startColor >>> 8) & 0xFF;
        int b1 = startColor & 0xFF;

        int a2 = (endColor >>> 24) & 0xFF;
        int r2 = (endColor >>> 16) & 0xFF;
        int g2 = (endColor >>> 8) & 0xFF;
        int b2 = endColor & 0xFF;

        int a = (int) (a1 + (a2 - a1) * progress);
        int r = (int) (r1 + (r2 - r1) * progress);
        int g = (int) (g1 + (g2 - g1) * progress);
        int b = (int) (b1 + (b2 - b1) * progress);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}