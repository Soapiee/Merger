package me.soapiee.merger.gui.buttons;

import lombok.Getter;
import me.soapiee.merger.Merger;
import me.soapiee.merger.data.PlayerData;
import me.soapiee.merger.gui.core.Icon;
import me.soapiee.merger.utils.Utils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class StandardButton implements ButtonsHandler {

    @Getter final private String ID;
    @Getter private final ItemStack icon;
    @Getter private final int slot;

    public StandardButton(Icon icon) {
        this.slot = icon.getSlot();
        ID = icon.getItemStack().getType() + "_" + slot;
        this.icon = setIcon();
    }

    public abstract ItemStack setIcon();

    @Override
    public void onClick(InventoryClickEvent event, Merger main, PlayerData playerData) {
        if (main.getConfigManager().isDebugMode()) Utils.consoleMsg("&a&lStandard button clicked");
        event.setCancelled(true);
    }

}
