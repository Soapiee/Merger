package me.soapiee.merger.gui.buttons;

import lombok.Getter;
import lombok.Setter;
import me.soapiee.merger.Merger;
import me.soapiee.merger.data.PlayerData;
import me.soapiee.merger.events.MergeEvent;
import me.soapiee.merger.logic.items.Item;
import me.soapiee.merger.logic.items.MergableItem;
import me.soapiee.merger.utils.Keys;
import me.soapiee.merger.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemButton implements ButtonsHandler {

    @Getter private final Item item;
    @Getter private final ItemStack icon;
    private final List<String> lore;
    @Getter private final String ID;
    @Setter @Getter private int slot;

    public ItemButton(Item item, int slot) {
        this.item = item;
        this.slot = slot;
        lore = item.getIcon().getLore();
        //TODO:
        this.ID = UUID.randomUUID().toString();
        icon = setIcon();
    }

    public ItemStack setIcon() {
        ItemStack newItem = item.getIcon().getItemStack().clone();

        ItemMeta meta = newItem.getItemMeta();
        meta.getPersistentDataContainer().set(Keys.MERGEITEMKEY, PersistentDataType.STRING, ID);
        meta.setLore(getLore());

        newItem.setItemMeta(meta);

        return newItem;
    }

    @Override
    public void onClick(InventoryClickEvent event, Merger main, PlayerData playerData) {
        Utils.consoleMsg("&a&lItem button clicked");
        if (!validClick(event)) return;

        boolean debugMode = main.getConfigManager().isDebugMode();
        ItemStack slotItem = getSlotItem(event.getCurrentItem(), debugMode);
        ItemStack handItem = getHandItem(event.getCursor(), debugMode);

        // ItemButton is placed into an empty slot
        if (slotItem == null && handItem != null) processItemMove(handItem, event, debugMode);

        // Items are trying to be stacked
        if (slotItem != null && handItem != null) {
            if (validateStackAttempt(handItem, slotItem, debugMode))
                mergeItems(getNextItem(), event, playerData, debugMode);
            else event.setCancelled(true);
        }

        // Item is picked up with an empty hand
        if (slotItem != null && handItem == null) processItemPickup(slotItem, event, debugMode);
    }

    private boolean validClick(InventoryClickEvent event) {
        ClickType click = event.getClick();
        if (click != ClickType.LEFT) {
            event.setCancelled(true);
            return false;
        }

        return true;
    }

    private boolean isMergeItem(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(Keys.MERGEITEMKEY, PersistentDataType.STRING);
    }

    private boolean validateStackAttempt(ItemStack handItem, ItemStack slotItem, boolean debugMode) {
        if (!isMergeItem(handItem)) {
            if (debugMode) Utils.consoleMsg("&cHand item is not mergable");
            return false;
        }

        if (!isMergeItem(slotItem)) {
            if (debugMode) Utils.consoleMsg("&cSlot item is not mergable");
            return false;
        }

        if (!itemsAreIdentical(handItem, slotItem)) {
            if (debugMode) Utils.consoleMsg("&cItems cannot be merged together");
            return false;
        }

        return true;
    }

    private boolean itemsAreIdentical(ItemStack handItem, ItemStack slotItem) {
        String handItemName = handItem.getItemMeta().getDisplayName();
        String slotItemName = slotItem.getItemMeta().getDisplayName();

        return (handItem.getType() == slotItem.getType() && handItemName.equalsIgnoreCase(slotItemName));
    }

    private MergableItem getNextItem() {
        if (this.item instanceof MergableItem) return ((MergableItem) this.item).next();
        return null;
    }

    private void mergeItems(MergableItem nextItem, InventoryClickEvent event, PlayerData playerData, boolean debugMode) {
        if (nextItem == null) {
            if (debugMode) Utils.consoleMsg("&aItem is last in the path");
            event.setCancelled(true);
            return;
        }

        String handItemID = getMergeItemID(getHandItem(event.getCursor(), debugMode));
        String slotItemID = getMergeItemID(getSlotItem(event.getCurrentItem(), debugMode));

        Player player = (Player) event.getWhoClicked();
        player.setItemOnCursor(new ItemStack(Material.AIR));
        event.setCancelled(true);

        if (debugMode) Utils.consoleMsg("&aItems merged successfully");
        MergeEvent mergeEvent = new MergeEvent(handItemID, slotItemID, playerData, event.getRawSlot(), nextItem);
        Bukkit.getPluginManager().callEvent(mergeEvent);
    }

    private String getMergeItemID(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().get(Keys.MERGEITEMKEY, PersistentDataType.STRING);
    }

    private void deleteIllegalItem(Player player, boolean debugMode) {
        if (debugMode) Utils.debugMsg(player.getName(), "&cAn illegal item has been deleted");
        player.setItemOnCursor(new ItemStack(Material.AIR));
    }

    private void processItemMove(ItemStack handItem, InventoryClickEvent event, boolean debugMode) {
        if (debugMode) Utils.consoleMsg("&cItem placed into an empty slot");

        if (!isMergeItem(handItem)) {
            //TODO: Needs testing
            Player player = (Player) event.getWhoClicked();
            deleteIllegalItem(player, debugMode);
            return;
        }

        updateSlot(event, debugMode);
    }

    private void updateSlot(InventoryClickEvent event, boolean debugMode) {
        if (debugMode) Utils.consoleMsg("&cSlot updated: " + getID());
        int eventSlot = event.getRawSlot();
        if (eventSlot != slot) setSlot(eventSlot);
    }

    private void processItemPickup(ItemStack slotItem, InventoryClickEvent event, boolean debugMode) {
        // Cancel event if not a mergeable item
        if (!isMergeItem(slotItem)) event.setCancelled(true);
        else {
            if (debugMode) Utils.consoleMsg("&cPicked up an item");
        }
    }

    private ItemStack getSlotItem(ItemStack slotItem, boolean debugMode) {
        if (slotItem == null) return null;

//        if (debugMode) Utils.consoleMsg("&eSlot Item: " + slotItem.getType());
        return slotItem.getType() == Material.AIR ? null : slotItem;
    }

    private ItemStack getHandItem(ItemStack handItem, boolean debugMode) {
        if (handItem == null) return null;

//        if (debugMode) Utils.consoleMsg("&eHand Item: " + handItem.getType());
        return handItem.getType() == Material.AIR ? null : handItem;
    }

    private List<String> getLore() {
        ArrayList<String> updatedLore = new ArrayList<>();

        for (String line : lore) {
            String string = line;
            if (string.contains("%placeholder1%")) string = string.replace("%placeholder1%", "string1");
            if (string.contains("%placeholder2%")) string = string.replace("%placeholder2%", "string2");

            updatedLore.add(Utils.addColour(string));
        }

        return updatedLore;
    }

}
