package me.soapiee.merger.gui.pages.order;

import lombok.Getter;
import me.soapiee.merger.Merger;
import me.soapiee.merger.factories.IconFactory;
import me.soapiee.merger.gui.core.Icon;
import me.soapiee.merger.gui.pages.PageSettings;
import me.soapiee.merger.utils.ConfigPath;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Set;

public class OrderPageSettings implements PageSettings {

    private final Merger main;
    private FileConfiguration config;
    private final IconFactory iconFactory;

    @Getter private String title;
    @Getter private int size;
    @Getter private int maxOrdersPerPlayer;
    @Getter public int totalPages;

    @Getter private final Set<Icon> icons = new HashSet<>();

    public OrderPageSettings(Merger main, IconFactory iconFactory) {
        this.main = main;
        config = main.getConfig();
        this.iconFactory = iconFactory;

        load(Bukkit.getConsoleSender());
    }

    private void load(CommandSender sender) {
        title = readTitle(ConfigPath.ORDERS_TITLE);
        size = readSize(ConfigPath.ORDERS_SIZE);
        maxOrdersPerPlayer = config.getInt(ConfigPath.ORDERS_MAX_PLAYER.getPath(), (int) ConfigPath.ORDERS_MAX_PLAYER.getDefaultValue());
        totalPages = calcTotalPages();

        String path = ConfigPath.ORDERS_ICONS.getPath();
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

    private int calcTotalPages() {
        int maxOrdersPerPage = config.getInt(ConfigPath.ORDERS_MAX_PAGE.getPath(), (int) ConfigPath.ORDERS_MAX_PAGE.getDefaultValue());
        if (maxOrdersPerPage >= maxOrdersPerPlayer) return 1;

        int value = maxOrdersPerPlayer / maxOrdersPerPage;
        return maxOrdersPerPlayer % maxOrdersPerPage == 0 ? value : value + 1 ;
    }

}
