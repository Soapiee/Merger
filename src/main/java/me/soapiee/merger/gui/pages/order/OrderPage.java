package me.soapiee.merger.gui.pages.order;

import lombok.Getter;
import me.soapiee.merger.Merger;
import me.soapiee.merger.data.PlayerData;
import me.soapiee.merger.enums.ClickAction;
import me.soapiee.merger.enums.PageType;
import me.soapiee.merger.gui.buttons.ButtonsHandler;
import me.soapiee.merger.gui.buttons.StandardButton;
import me.soapiee.merger.gui.core.GUIManager;
import me.soapiee.merger.gui.core.Icon;
import me.soapiee.merger.gui.core.MultiPageHandler;
import me.soapiee.merger.handlers.ClickActionHandler;
import me.soapiee.merger.utils.Keys;
import me.soapiee.merger.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OrderPage extends MultiPageHandler implements ClickActionHandler {

    @Getter private final Inventory inventory;
    private final PageType pageType = PageType.ORDER_PAGE;
    private final PlayerData playerData;
    private final Player player;

    private final String title;
    @Getter public final int size;
    private final int max_icons;

    public OrderPage(Merger main, @NotNull PlayerData playerData, int currentPage, int totalPages) {
        super(main, PageType.ORDER_PAGE, currentPage, totalPages);
        this.playerData = playerData;
        this.player = playerData.getPlayer().getPlayer();

        title = pageSettings.getTitle();
        size = pageSettings.getSize();
        max_icons = ((OrderPageSettings)pageSettings).getMaxOrdersPerPlayer();
        inventory = createInventory();
    }

    private Inventory createInventory() {
        return Bukkit.createInventory(null, size, Utils.addColour(title));
    }

    @Override
    public void setButtons() {
        pageSettings.getIcons().forEach(icon -> {
            if (icon.getClickAction() == ClickAction.PREVIOUS_PAGE) {
                if (currentPage <= 1) return;
            }

            if (icon.getClickAction() == ClickAction.NEXT_PAGE) {
                if (currentPage >= totalPages) return;
            }

            addButton(createButton(icon));
        });

//        playerData.getOrders().forEach((identifier, button) -> getInventory().setItem(button.getSlot(), button.getIcon()));
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

                GUIManager guiManager = main.getGuiManager();

                switch (action) {
                    case OPEN:
                        openPage(main, icon.getPage(), pageType, playerData);
                        return;
                    case CLOSE:
                        closePage(event);
                        return;
                    case NEXT_PAGE:
                        newPage(guiManager, currentPage + 1);
                        return;
                    case PREVIOUS_PAGE:
                        newPage(guiManager, currentPage - 1);
                }
            }
        };
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        int slot = event.getRawSlot();
        ButtonsHandler button = getButton(slot);

        if (button != null) button.onClick(event, main, playerData);
    }

    @Override
    public void newPage(GUIManager guiManager, int newPageNumber) {
        guiManager.openGUI(new OrderPage(main, playerData, newPageNumber, totalPages), player);
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
                .replace("%player_name%", player.getName())
                .replace("%current_page%", String.valueOf(currentPage))
                .replace("%next_page_number%", String.valueOf(currentPage + 1))
                .replace("%prev_page_number%", String.valueOf(currentPage - 1));
        meta.setDisplayName(Utils.addColour(newName));

        if (itemStack.getType() == Material.PLAYER_HEAD) ((SkullMeta) meta).setOwningPlayer(player);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private List<String> fillPlaceholders(List<String> list) {
        ArrayList<String> updatedLore = new ArrayList<>();

        for (String line : list) {
            if (line.contains("%")) {
                updatedLore.add(Utils.addColour(line.replace("%player_name%", player.getName()))
                        .replace("%current_page%", String.valueOf(currentPage))
                        .replace("%next_page_number%", String.valueOf(currentPage + 1))
                        .replace("%prev_page_number%", String.valueOf(currentPage - 1)));
            } else updatedLore.add(Utils.addColour(line));
        }

        return updatedLore;
    }
}
