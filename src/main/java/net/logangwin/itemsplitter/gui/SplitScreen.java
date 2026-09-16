package net.logangwin.itemsplitter.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.logangwin.itemsplitter.logic.ItemSplitterUtils;
import net.logangwin.itemsplitter.logic.SplitScreenLogic;
import net.logangwin.itemsplitter.mixin.DrawContextInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;

public class SplitScreen {
    private static List<TooltipComponent> components;
    private static final TooltipPositioner positioner = HoveredTooltipPositioner.INSTANCE;
    private static double progress = 0.5F;

    public static void initialize() {
        // Create space for slider to go
        components = new ArrayList<>();
    }

    public static void drawTooltip(DrawContext context, TextRenderer textRenderer, int slotX, int slotY, Slot targetSlot) {
        // Safety Checks
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || targetSlot == null || !targetSlot.hasStack()) {
            return;
        }

        // Determine which component to use
        boolean isCreative = client.player.isCreative() && ItemSplitterUtils.isCreativeSlot(ItemSplitterUtils.getCurrentScreen(), targetSlot);
        ItemStack stack = targetSlot.getStack();

        if (isCreative) {
            components.add(new CreativeSplitScreenComponent(textRenderer, progress, stack.getMaxCount()));
        } else {
            components.add(new SplitScreenComponent(textRenderer, progress, stack.getCount()));
        }

        // Offset the standard tooltip positioner and center it above the target slot
        TooltipComponent firstComponent = components.getFirst();
        int renderX = slotX - 12 - (firstComponent.getWidth(textRenderer) / 2);
        int renderY = slotY - 10 - (firstComponent.getHeight() / 2);

        // Handle animations and opacity
        float renderOffsetY = 0;
        float alpha = 1.0f;

        if (ConfigScreen.INSTANCE.enableAnimations) {
            float animProgress = SplitScreenLogic.getAnimationProgress();
            renderOffsetY = 10 * (1.0f - easeOutExpo(animProgress));
            alpha = Math.min(2.0f * animProgress + ConfigScreen.INSTANCE.startingOpacity, 1.0f);
        }

        // Render the tooltip
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        context.getMatrices().push();
        context.getMatrices().translate(renderX, renderY - renderOffsetY, 0);

        ((DrawContextInvoker) context).itemsplitter$invokeComponentTooltip(textRenderer, components, 0, 0, positioner);

        // Restore state
        context.getMatrices().pop();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        components.clear();
    }

    public static void updateSplitSlider(double progress) {
        // Ensure progress percentage is within bounds
        if (progress > 1.0F) {
            SplitScreen.progress = 1.0F;
        } else if (progress < 0.0F) {
            SplitScreen.progress = 0.0F;
        } else {
            SplitScreen.progress = progress;
        }
    }

    private static float easeOutExpo(float x) {
        return x == 1 ? 1 : 1 - (float) (Math.pow(2, -10 * x));
    }
}
