package me.soapiee.merger.gui.core;

import me.soapiee.merger.Merger;
import me.soapiee.merger.enums.PageType;
import me.soapiee.merger.factories.IconFactory;
import me.soapiee.merger.gui.pages.PageSettings;
import me.soapiee.merger.gui.pages.game.GamePageSettings;
import me.soapiee.merger.gui.pages.order.OrderPageSettings;
import me.soapiee.merger.gui.pages.upgrade.UpgradePageSettings;

import java.util.HashMap;
import java.util.Map;

public class GUISettingsManager {

    private final Map<PageType, PageSettings> pageSettings = new HashMap<>();

    public GUISettingsManager(Merger main){
        IconFactory iconFactory = main.getIconFactory();
        addSettings(PageType.GAME_PAGE, new GamePageSettings(main, iconFactory));
        addSettings(PageType.UPGRADE_PAGE, new UpgradePageSettings(main, iconFactory));

        //TODO: Move to orders module
        addSettings(PageType.ORDER_PAGE, new OrderPageSettings(main, iconFactory));
    }

    public PageSettings getPageSettings(PageType page){
        return pageSettings.get(page);
    }

    public void addSettings(PageType page, PageSettings settings){
        pageSettings.put(page, settings);
    }
}
