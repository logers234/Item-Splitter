package net.logangwin.itemsplitter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.logangwin.itemsplitter.gui.ChargeHud;
import net.logangwin.itemsplitter.gui.SplitScreen;
import net.logangwin.itemsplitter.logic.RightClickHandler;

public class ItemSplitterClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register tick method to update RightClickHandler
        ClientTickEvents.END_CLIENT_TICK.register(client -> tick());

        // Initialize GUI elements
        ChargeHud.initialize();
        SplitScreen.initialize();
    }

    public void tick() {
        RightClickHandler.tick();
    }
}
