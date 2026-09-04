package me.soapiee.merger.gui.core;

import me.soapiee.merger.Merger;
import me.soapiee.merger.enums.PageType;
import me.soapiee.merger.gui.buttons.ButtonsHandler;
import me.soapiee.merger.gui.pages.PageSettings;
import org.bukkit.Material;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class SinglePageHandler implements InventoryHandler {

    protected final Merger main;
    protected final PageSettings pageSettings;
    private final Map<String, ButtonsHandler> buttonMap = new HashMap<>();

    public SinglePageHandler(Merger main, PageType pageType) {
        this.main = main;
        pageSettings = main.getGuiSettingsManager().getPageSettings(pageType);
    }

    protected void addButton(ButtonsHandler button) {
        int slot = button.getSlot();
        if (slot == -1 || slot > getSize()) return;
        buttonMap.put(button.getID(), button);
    }

    protected ButtonsHandler getButton(String id) {
        return buttonMap.get(id);
    }

    public abstract void onClick(InventoryClickEvent event);

    @Override
    public void onOpen(InventoryOpenEvent event) {
        displayButtons();
    }

    private void displayButtons() {
        setButtons();
        buttonMap.forEach((slot, button) -> getInventory().setItem(button.getSlot(), button.getIcon()));
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        event.getPlayer().setItemOnCursor(new ItemStack(Material.AIR));
    }
}
