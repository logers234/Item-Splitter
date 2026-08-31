package net.logangwin.itemsplitter.logic;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.resource.Resource;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.util.Optional;

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

    public static HandledScreen<?> getCurrentScreen() {
        MinecraftClient client = MinecraftClient.getInstance();

        // Check if the current screen is an instance of HandledScreen
        if (client.currentScreen instanceof HandledScreen<?> handledScreen) {
            return handledScreen;
        }

        return null; // No inventory is currently open
    }

    public static int getPngWidth(Identifier id) {
        Optional<Resource> resource = MinecraftClient.getInstance()
                .getResourceManager()
                .getResource(id);

        if (resource.isPresent()) {
            try (InputStream stream = resource.get().getInputStream();
                 NativeImage image = NativeImage.read(stream)) {

                return image.getWidth();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0; // Fallback if the file not found
    }

    public static int getPngHeight(Identifier id) {
        Optional<Resource> resource = MinecraftClient.getInstance()
                .getResourceManager()
                .getResource(id);

        if (resource.isPresent()) {
            try (InputStream stream = resource.get().getInputStream();
                 NativeImage image = NativeImage.read(stream)) {

                return image.getHeight();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0; // Fallback if the file not found
    }
}
