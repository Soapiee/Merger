package me.soapiee.merger.gui.buttons;

import me.soapiee.merger.Merger;
import me.soapiee.merger.data.PlayerData;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface ButtonsHandler {

    String getID();
    ItemStack getIcon();

    int getSlot();

    void onClick(InventoryClickEvent event, Merger main, PlayerData playerData);
}
