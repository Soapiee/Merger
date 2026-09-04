package me.soapiee.merger.handlers;

import me.soapiee.merger.Merger;
import me.soapiee.merger.data.PlayerData;
import me.soapiee.merger.enums.PageType;
import me.soapiee.merger.gui.core.GUIManager;
import me.soapiee.merger.gui.core.GUISettingsManager;
import me.soapiee.merger.gui.pages.game.GamePage;
import me.soapiee.merger.gui.pages.order.OrderPage;
import me.soapiee.merger.gui.pages.order.OrderPageSettings;
import me.soapiee.merger.gui.pages.upgrade.UpgradePage;
import me.soapiee.merger.gui.pages.upgrade.UpgradePageSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface ClickActionHandler {

    default void newPage(GUIManager guiManager, int newPageNumber) {
    }

    default void closePage(InventoryClickEvent event) {
        event.getWhoClicked().closeInventory();
    }

    default void openPage(Merger main, PageType newPage, PageType currentPage, PlayerData playerData) {
        if (newPage == PageType.NONE) return;

        GUIManager guiManager = main.getGuiManager();
        GUISettingsManager guiSettingsManager = main.getGuiSettingsManager();
        Player player = playerData.getPlayer().getPlayer();

        switch (newPage) {
            case UPGRADE_PAGE:
                if (currentPage == PageType.UPGRADE_PAGE) return;
                UpgradePageSettings pageSettings = (UpgradePageSettings) guiSettingsManager.getPageSettings(PageType.UPGRADE_PAGE);
                int maxPages = pageSettings.getTotalPages();

                guiManager.openGUI(new UpgradePage(main, playerData, 1, maxPages), player);
                return;
            case ORDER_PAGE:
                if (currentPage == PageType.ORDER_PAGE) return;
                OrderPageSettings pageSettingsTwo = (OrderPageSettings) guiSettingsManager.getPageSettings(PageType.ORDER_PAGE);
                int totalPages = pageSettingsTwo.getTotalPages();

                guiManager.openGUI(new OrderPage(main, playerData, 1, totalPages), player);
                return;
            case GAME_PAGE:
                if (currentPage == PageType.GAME_PAGE) return;

                guiManager.openGUI(new GamePage(main, playerData), player);
        }
    }
}
