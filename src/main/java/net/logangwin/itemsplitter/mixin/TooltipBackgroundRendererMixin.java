package net.logangwin.itemsplitter.mixin;

import net.logangwin.itemsplitter.gui.ConfigScreen;
import net.logangwin.itemsplitter.logic.SplitScreenHandler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TooltipBackgroundRenderer.class)
public class TooltipBackgroundRendererMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private static void drawOpaqueBackground(DrawContext context, int x, int y, int width, int height, @Nullable Identifier texture, CallbackInfo ci) {
        if (SplitScreenHandler.isScreenOpen()) {
            int i = x - 3 - 9;
            int j = y - 3 - 9;
            int k = width + 3 + 3 + 18;
            int l = height + 3 + 3 + 18;

            float backgroundAlpha = 0.94F;
            float frameAlpha = 0.94F;

            if (ConfigScreen.AnimationSettings.enableAnimations) {
                float animProgress = SplitScreenHandler.getAnimationProgress();
                float fade = Math.clamp(animProgress + ConfigScreen.AnimationSettings.startingOpacity, 0.0F, 1.0F);

                backgroundAlpha *= fade;
                frameAlpha *= fade;
            }

            int backgroundAlphaBits = (int)(backgroundAlpha * 255.0f) & 0xFF;
            int backgroundColor = (backgroundAlphaBits << 24) | 0xFFFFFF;

            int frameAlphaBits = (int)(frameAlpha * 255.0f) & 0xFF;
            int frameColor = (frameAlphaBits << 24) | 0xFFFFFF;

            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, Identifier.ofVanilla("tooltip/background"), i, j, k, l, backgroundColor);
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, Identifier.ofVanilla("tooltip/frame"), i, j, k, l, frameColor);

            ci.cancel();
        }
    }
}
