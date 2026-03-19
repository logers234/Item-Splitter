package net.logangwin.itemsplitter.gui;

import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.hud.Hud;
import net.logangwin.itemsplitter.mixin.HandledScreenAccessor;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class ChargeHud {
    private static ChargeCircleComponent circle;
    private static FlowLayout container;

    public static void initialize() {
        // Define the container layout and default position
        container = Containers.verticalFlow(Sizing.content(), Sizing.content());
        container.positioning(Positioning.absolute(0, 0));

        // Create a new charge circle component and set the size
        circle = new ChargeCircleComponent();
        circle.sizing(Sizing.fixed(32), Sizing.fixed(32));

        // Attach to container
        container.child(circle);

        Hud.add(Identifier.of("itemsplitter", "charge_indicator"), () -> container);
    }

    public static void update(float progress, HandledScreen<?> screen, Slot slot) {
        if (container == null || slot == null) return;

        circle.setProgress(progress);

        // Calculate position ontop of item
        int guiX = ((HandledScreenAccessor) screen).getX() + slot.x - 8;
        int guiY = ((HandledScreenAccessor) screen).getY() + slot.y - 8;

        container.positioning(Positioning.absolute(guiX, guiY));
    }
}