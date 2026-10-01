package net.logangwin.itemsplitter.mixin;

import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface GameRendererMixin {

    @Accessor("guiState")
    GuiRenderState getGuiState();
}