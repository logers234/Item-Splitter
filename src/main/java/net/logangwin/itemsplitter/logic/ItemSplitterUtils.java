package net.logangwin.itemsplitter.logic;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.screen.slot.Slot;

public class ItemSplitterUtils {

    public static boolean isCreativeSlot(HandledScreen<?> screen, Slot slot) {
        if (screen instanceof CreativeInventoryScreen creativeInventoryScreen) {
            // Check if the slot belongs to the catalog grid (not PlayerInventory)
            boolean isPickerGridSlot = !(slot.inventory instanceof PlayerInventory);

            // Check if the current selected tab isn't the Survival Inventory tab
            ItemGroup selectedTab = creativeInventoryScreen.getSelectedItemGroup();
            boolean isNotSurvivalTab = (
                    selectedTab != ItemGroups.getSearchGroup()
                    && selectedTab.getType() != ItemGroup.Type.INVENTORY
            );

            return isPickerGridSlot && isNotSurvivalTab;
        }

        return false;
    }
}
