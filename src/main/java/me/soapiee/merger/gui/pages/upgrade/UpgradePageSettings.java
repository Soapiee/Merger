package me.soapiee.merger.gui.pages.upgrade;

import lombok.Getter;
import me.soapiee.merger.Merger;
import me.soapiee.merger.factories.IconFactory;
import me.soapiee.merger.gui.core.Icon;
import me.soapiee.merger.gui.pages.PageSettings;
import me.soapiee.merger.logic.ItemsManager;
import me.soapiee.merger.utils.ConfigPath;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Set;

public class UpgradePageSettings implements PageSettings {

    private final Merger main;
    private final ItemsManager itemsManager;
    private FileConfiguration config;
    private final IconFactory iconFactory;

    @Getter private String title;
    @Getter private int size;
    @Getter public int maxUpgrades;
    @Getter public int totalPages;

    @Getter private final Set<Icon> icons = new HashSet<>();

    public UpgradePageSettings(Merger main, IconFactory iconFactory) {
        this.main = main;
        itemsManager = main.getItemsManager();
        config = main.getConfig();
        this.iconFactory = iconFactory;

        load(Bukkit.getConsoleSender());
    }

    private void load(CommandSender sender) {
        title = readTitle(ConfigPath.UPGRADES_TITLE);
        size = readSize(ConfigPath.UPGRADES_SIZE);
        maxUpgrades = config.getInt(ConfigPath.UPGRADES_MAX.getPath(), (int) ConfigPath.UPGRADES_MAX.getDefaultValue());
        totalPages = calcTotalPages();

        String path = ConfigPath.UPGRADES_ICONS.getPath();
        if (!config.isConfigurationSection(path)) return;
        for (String key : config.getConfigurationSection(path).getKeys(false)) {
            icons.add(iconFactory.createIcon(config, path + "." + key, sender));
        }
    }

    public void reload(CommandSender sender) {
        config = main.getConfig();
        icons.clear();
        load(sender);
    }

    private String readTitle(ConfigPath rawPath) {
        String path = rawPath.getPath();
        return config.getString(path, (String) rawPath.getDefaultValue());
    }

    private int readSize(ConfigPath rawPath) {
        String path = rawPath.getPath();
        int configSize = config.getInt(path, (int) rawPath.getDefaultValue());
        return configSize % 9 == 0 ? configSize : 54;
    }

    //TODO: Test
    private int calcTotalPages() {
        int totalProducers = itemsManager.getProducerCount();
        int value = totalProducers / maxUpgrades;

        return totalProducers % maxUpgrades == 0 ? value : value + 1 ;
    }

}
