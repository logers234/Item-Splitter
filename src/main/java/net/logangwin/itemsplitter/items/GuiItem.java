package net.logangwin.itemsplitter.items;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.logangwin.itemsplitter.ItemSplitter;
import net.logangwin.itemsplitter.ItemSplitterClient;
import net.logangwin.itemsplitter.gui.Gui;
import net.logangwin.itemsplitter.gui.ItemSplitScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class GuiItem extends Item {
    public GuiItem(Settings settings) {
        super(settings);
    }

    public static Item register(Item item, String id) {
        // Create the identifier for the item.
        Identifier itemID = Identifier.of(ItemSplitter.MOD_ID, id);

        // Register the item.
        Item registeredItem = Registry.register(Registries.ITEM, itemID, item);

        // Return the registered item!
        return registeredItem;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemSplitter.LOGGER.info("Item Used");
        if (world.isClient) {
            ItemSplitterClient.shouldOpenGui = true;
        }
        return super.use(world, user, hand);
    }

    public static final Item GuiItem = register(new GuiItem(new Item.Settings()), "gui_item");

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(net.logangwin.itemsplitter.items.GuiItem.GuiItem));
    }
}