package me.soapiee.merger.gui.buttons;

import lombok.Getter;
import lombok.Setter;
import me.soapiee.merger.Merger;
import me.soapiee.merger.data.PlayerData;
import me.soapiee.merger.gui.pages.upgrade.UpgradePage;
import me.soapiee.merger.logic.items.Producer;
import me.soapiee.merger.utils.Keys;
import me.soapiee.merger.utils.Utils;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class UpgradeButton implements ButtonsHandler {

    @Getter private final Producer producer;
    @Getter private final ItemStack icon;
    @Getter private final double upgradeCost;
    private final List<String> lore;
    @Getter private final String ID;
    @Setter @Getter private int slot;
    @Getter private final int currentPage;
    @Getter private final int totalPages;

    public UpgradeButton(Producer producer, int slot, int currentPage, int totalPages) {
        this.producer = producer;
        this.slot = slot;
        lore = producer.getUpgradeIcon().getLore();
        upgradeCost = producer.getUpgradeCost();
        //TODO:
        this.ID = producer.getIdentifier();
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        icon = setIcon();
    }

    public ItemStack setIcon() {
        ItemStack newItem = producer.getUpgradeIcon().getItemStack().clone();

        ItemMeta meta = newItem.getItemMeta();
        meta.getPersistentDataContainer().set(Keys.UPGRADEKEY, PersistentDataType.STRING, ID);
        meta.setLore(getLore());

        newItem.setItemMeta(meta);

        return newItem;
    }

    //TODO: Probably make abstract
    @Override
    public void onClick(InventoryClickEvent event, Merger main, PlayerData playerData) {
        Utils.consoleMsg("&a&lUpgrade button clicked");
        event.setCancelled(true);
        if (!validClick(event)) return;

        boolean debugMode = main.getConfigManager().isDebugMode();
        double currentCoins = playerData.getCoins();

        if (currentCoins < upgradeCost) {
            Utils.consoleMsg("&cNot enough coins to unlock");
            return;
        }

        playerData.unlockProducer(main, producer);
        main.getGuiManager().openGUI(new UpgradePage(main, playerData, currentPage, totalPages), playerData.getPlayer().getPlayer());
    }

    private boolean validClick(InventoryClickEvent event) {
        ClickType click = event.getClick();
        if (click != ClickType.LEFT) {
            event.setCancelled(true);
            return false;
        }

        return true;
    }

    private String getProducerID(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().get(Keys.UPGRADEKEY, PersistentDataType.STRING);
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
