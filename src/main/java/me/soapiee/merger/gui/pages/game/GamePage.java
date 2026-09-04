package me.soapiee.merger.gui.pages.game;

import lombok.Getter;
import me.soapiee.merger.Merger;
import me.soapiee.merger.data.PlayerData;
import me.soapiee.merger.enums.ClickAction;
import me.soapiee.merger.enums.PageType;
import me.soapiee.merger.gui.buttons.ButtonsHandler;
import me.soapiee.merger.gui.buttons.StandardButton;
import me.soapiee.merger.gui.core.Icon;
import me.soapiee.merger.gui.core.SinglePageHandler;
import me.soapiee.merger.handlers.ClickActionHandler;
import me.soapiee.merger.utils.Keys;
import me.soapiee.merger.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GamePage extends SinglePageHandler implements ClickActionHandler {

    @Getter private final Inventory inventory;
    private final PageType pageType = PageType.GAME_PAGE;
    private final PlayerData playerData;
    private final Player player;

    private final String title;
    @Getter public final int size;

    public GamePage(Merger main, @NotNull PlayerData playerData) {
        super(main, PageType.GAME_PAGE);
        this.playerData = playerData;
        this.player = playerData.getPlayer().getPlayer();

        title = pageSettings.getTitle();
        size = pageSettings.getSize();
        inventory = createInventory();
    }

    private Inventory createInventory() {
        return Bukkit.createInventory(null, size, Utils.addColour(title));
    }

    @Override
    public void setButtons() {
        playerData.getGameInventory().getItemButtons().forEach((identifier, button) -> addButton(button));
        playerData.getGameInventory().getProducerButtons().forEach((identifier, button) -> addButton(button));
        pageSettings.getIcons().forEach(icon -> addButton(createButton(icon)));
    }

    private StandardButton createButton(Icon icon) {
        return new StandardButton(icon) {
            @Override public ItemStack setIcon() {
                return formatIcon(icon, getID());
            }

            @Override
            public void onClick(InventoryClickEvent event, Merger main, PlayerData playerData) {
                if (main.getConfigManager().isDebugMode()) Utils.consoleMsg("&a&lStandard button clicked");
                event.setCancelled(true);

                ClickAction action = icon.getClickAction();
                if (icon.getClickAction() == ClickAction.NULL) return;

                ItemStack handItem = event.getCursor();
                if (handItem != null && handItem.getType() != Material.AIR) return;

                switch (action) {
                    case OPEN:
                        openPage(main, icon.getPage(), pageType, playerData);
                        return;
                    case CLOSE:
                        closePage(event);
                        return;
                    case NEXT_PAGE, PREVIOUS_PAGE:
                }
            }
        };
    }

    private ItemStack formatIcon(Icon icon, String buttonID) {
        ItemStack itemStack = fillPlaceholders(icon.getItemStack().clone());
        ItemMeta meta = itemStack.getItemMeta();

        meta.getPersistentDataContainer().set(Keys.STANDARDBUTTONKEY, PersistentDataType.STRING, buttonID);

        meta.setLore(fillPlaceholders(icon.getLore()));
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    private ItemStack fillPlaceholders(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        String newName = meta.getDisplayName()
                .replace("%player_name%", player.getName());
        meta.setDisplayName(Utils.addColour(newName));

        if (itemStack.getType() == Material.PLAYER_HEAD) ((SkullMeta) meta).setOwningPlayer(player);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private List<String> fillPlaceholders(List<String> list) {
        ArrayList<String> updatedLore = new ArrayList<>();

        for (String line : list) {
            if (line.contains("%")) {
                if (line.contains("%player_name%")) line.replace("%player_name%", player.getName());
                if (line.contains("%energy%")) line.replace("%energy%", String.valueOf(playerData.getEnergy()));
                if (line.contains("%coins%")) line.replace("%coins%", String.valueOf(playerData.getCoins()));
            }

            updatedLore.add(Utils.addColour(line));
        }

        return updatedLore;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!validClick(event)) return;
        if (!validSlot(event)) return;

        ButtonsHandler button = getButton(getButtonID(event));

        if (button != null) {
            button.onClick(event, main, playerData);
            return;
        }

        //Called when an empty slot gets clicked with an empty hand, or an illegal item
        if (main.getConfigManager().isDebugMode()) Utils.consoleMsg("&a&lEmpty slot, Empty/Non button in hand clicked");
        event.setCancelled(true);
    }

    private boolean validClick(InventoryClickEvent event) {
        ClickType click = event.getClick();
        if (click == ClickType.LEFT || click == ClickType.RIGHT) return true;

        event.setCancelled(true);
        return false;
    }

    private boolean validSlot(InventoryClickEvent event) {
        int rawSlot = event.getRawSlot();
        if (rawSlot >= size) {
            event.setCancelled(true);
            return false;
        }

        return true;
    }

    private String getButtonID(InventoryClickEvent event) {
        String id = extractID(getItem(event.getCurrentItem()));
        if (id == null) id = extractID(getItem(event.getCursor()));

        return id;
    }

    private String extractID(ItemStack itemStack) {
        List<NamespacedKey> keys = List.of(Keys.MERGEITEMKEY, Keys.PRODUCERKEY, Keys.STANDARDBUTTONKEY);
        String id;

        if (itemStack != null) {
            PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();

            for (NamespacedKey key : keys) {
                if (container.has(key)) {
                    id = container.get(key, PersistentDataType.STRING);
                    if (id != null) return id;
                }
            }
        }

        return null;
    }

    private ItemStack getItem(ItemStack item) {
        if (item == null) return null;
        return item.getType() == Material.AIR ? null : item;
    }
}

