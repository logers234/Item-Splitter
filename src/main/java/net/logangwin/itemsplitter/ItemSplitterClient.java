package net.logangwin.itemsplitter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.logangwin.itemsplitter.gui.ConfigScreen;
import net.logangwin.itemsplitter.gui.SplitScreen;
import net.logangwin.itemsplitter.gui.widget.Icon;
import net.logangwin.itemsplitter.logic.RightClickHandler;
import net.minecraft.util.Identifier;

public class ItemSplitterClient implements ClientModInitializer {

    private static Icon pickupIconMinimal;
    private static Icon pickupIconColor;
    private static Icon dropIconMinimal;
    private static Icon dropIconColor;

    @Override
    public void onInitializeClient() {
        // Register tick method to update RightClickHandler
        ClientTickEvents.END_CLIENT_TICK.register(client -> tick());

        // Initialize GUI elements
        SplitScreen.initialize();

        // Load icons
        pickupIconMinimal = new Icon(Identifier.of("item-splitter", "textures/gui/icon/pickup_icon_minimal.png"));
        pickupIconColor = new Icon(Identifier.of("item-splitter", "textures/gui/icon/pickup_icon_color.png"));
        dropIconMinimal = new Icon(Identifier.of("item-splitter", "textures/gui/icon/drop_icon_minimal.png"));
        dropIconColor = new Icon(Identifier.of("item-splitter", "textures/gui/icon/drop_icon_color.png"));

        // Initialize config screen
        ConfigScreen.init();
    }

    public void tick() {
        RightClickHandler.tick();
    }

    @SuppressWarnings("unused")
    public static Icon getPickupIconMinimal() {
        return pickupIconMinimal;
    }

    public static Icon getPickupIconColor() {
        return pickupIconColor;
    }

    @SuppressWarnings("unused")
    public static Icon getDropIconMinimal() {
        return dropIconMinimal;
    }

    public static Icon getDropIconColor() {
        return dropIconColor;
    }
}
