package net.logangwin.itemsplitter;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.logangwin.itemsplitter.gui.Gui;
import net.logangwin.itemsplitter.gui.ItemSplitScreen;
import net.logangwin.itemsplitter.items.GuiItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemSplitter implements ModInitializer {

	public static final String MOD_ID = "item-splitter";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		GuiItem.initialize();

		LOGGER.info("Hello Fabric world!");
	}
}