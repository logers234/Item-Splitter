package net.logangwin.itemsplitter.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.logangwin.itemsplitter.ItemSplitterClient;
import net.logangwin.itemsplitter.logic.ItemSplitterUtils;
import net.logangwin.itemsplitter.logic.SplitScreenHandler;
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
        ItemSplitterClient.LOGGER.info("draw tooltip");
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
        int renderY = slotY - 10 - (firstComponent.getHeight(textRenderer) / 2);

        // Handle animations and opacity
        float renderOffsetY = 0;
        float alpha = 1.0f;

        if (ConfigScreen.AnimationSettings.enableAnimations) {
            float animProgress = SplitScreenHandler.getAnimationProgress();
            renderOffsetY = 10 * (1.0f - ItemSplitterUtils.easeOutExpo(animProgress));
            alpha = Math.min(2.0f * animProgress + ConfigScreen.AnimationSettings.startingOpacity, 1.0f);
        }

        // Render the tooltip
        context.getMatrices().pushMatrix();
        context.getMatrices().translate(renderX, renderY - renderOffsetY);

        ((DrawContextInvoker) context).itemsplitter$invokeComponentTooltip(textRenderer, components, 0, 0, positioner, null, true);

        // Restore state
        context.getMatrices().popMatrix();
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
}
