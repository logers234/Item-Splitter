package net.logangwin.itemsplitter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.logangwin.itemsplitter.gui.ChargeHud;
import net.minecraft.client.MinecraftClient;

public class ItemSplitterClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> tick());
        ChargeHud.initialize();
        MinecraftClient client1 = MinecraftClient.getInstance();
    }

    public void tick() {
        RightClickHandler.tick();
    }
}
