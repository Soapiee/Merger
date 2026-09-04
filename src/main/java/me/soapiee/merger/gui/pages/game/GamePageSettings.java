package me.soapiee.merger.gui.pages.game;

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

public class GamePageSettings implements PageSettings {

    private final Merger main;
    private FileConfiguration config;
    private final IconFactory iconFactory;

    @Getter private String title;
    @Getter private final int size = 54;

    @Getter private final Set<Icon> icons = new HashSet<>();

    public GamePageSettings(Merger main, IconFactory iconFactory) {
        this.main = main;
        config = main.getConfig();
        this.iconFactory = iconFactory;

        load(Bukkit.getConsoleSender());
    }

    private void load(CommandSender sender) {
        title = readTitle(ConfigPath.GAME_TITLE);

        String path = ConfigPath.GAME_ICONS.getPath();
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
}
