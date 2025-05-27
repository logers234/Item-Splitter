package net.logangwin.itemsplitter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.logangwin.itemsplitter.gui.Gui;
import net.logangwin.itemsplitter.gui.ItemSplitScreen;

public class ItemSplitterClient implements ClientModInitializer {

    public static boolean shouldOpenGui = false;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (shouldOpenGui && client.currentScreen == null) {
                shouldOpenGui = false;
                client.setScreen(new ItemSplitScreen(new Gui()));
                ItemSplitter.LOGGER.info("Screen");
            }
        });
    }
}
