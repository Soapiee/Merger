package me.soapiee.merger.listeners;

import me.soapiee.merger.Merger;
import me.soapiee.merger.data.PlayerData;
import me.soapiee.merger.events.MergeEvent;
import me.soapiee.merger.gui.buttons.ItemButton;
import me.soapiee.merger.gui.core.GUIManager;
import me.soapiee.merger.gui.pages.game.GamePage;
import me.soapiee.merger.logic.items.MergableItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class GUIListener implements Listener {

    private final Merger main;
    private final GUIManager guiManager;

    public GUIListener(Merger main) {
        this.main = main;
        this.guiManager = main.getGuiManager();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        guiManager.handleClick(event);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        guiManager.handleOpen(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        guiManager.handleClose(event);
    }

    @EventHandler
    public void onMerge(MergeEvent event) {
        PlayerData playerData = event.getPlayerData();
        MergableItem nextItem = event.getNextItem();

        playerData.getGameInventory().removeItemButton(event.getHandItem());
        playerData.getGameInventory().removeItemButton(event.getSlotItem());
        playerData.getGameInventory().addItemButton(new ItemButton(nextItem, event.getSlot()));

        guiManager.openGUI(new GamePage(main, playerData), playerData.getPlayer().getPlayer());
    }
}
