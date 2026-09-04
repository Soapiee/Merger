package me.soapiee.merger.gui.core;

import me.soapiee.merger.Merger;
import me.soapiee.merger.enums.PageType;
import me.soapiee.merger.gui.buttons.ButtonsHandler;
import me.soapiee.merger.gui.pages.PageSettings;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.HashMap;
import java.util.Map;

public abstract class MultiPageHandler implements InventoryHandler {

    protected final Merger main;
//    protected final GUISettingsManager guiSettingsManager;
    protected final PageSettings pageSettings;
    private final Map<Integer, ButtonsHandler> buttonMap = new HashMap<>();

    protected final int currentPage;
    protected final int totalPages;

    public MultiPageHandler(Merger main, PageType pageType, int currentPage, int totalPages) {
        this.main = main;
//        this.guiSettingsManager = main.getGuiSettingsManager();
        pageSettings = main.getGuiSettingsManager().getPageSettings(pageType);
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }

    protected void addButton(ButtonsHandler button) {
        int slot = button.getSlot();
        if (slot == -1 || slot > getSize()) return;
        buttonMap.put(slot, button);
    }

    protected ButtonsHandler getButton(int slot) {
        return buttonMap.get(slot);
    }

    public abstract void onClick(InventoryClickEvent event);

//    @Override
//    public void onClick(InventoryClickEvent event) {
//        event.setCancelled(true);
//        int slot = event.getRawSlot();
//        ButtonsHandler button = buttonMap.get(slot);
//
//        if (button != null) button.onClick(event, main);
//    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        displayButtons();
    }

    private void displayButtons() {
        setButtons();
        buttonMap.forEach((slot, button) -> getInventory().setItem(slot, button.getIcon()));
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }
}
