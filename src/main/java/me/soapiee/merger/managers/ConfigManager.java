package me.soapiee.merger.managers;

import lombok.Getter;
import lombok.Setter;
import me.soapiee.merger.Merger;
import me.soapiee.merger.logic.MergePathsManager;
import me.soapiee.merger.utils.ConfigPath;
import me.soapiee.merger.utils.FileLoader;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager implements FileLoader {

    private final Merger main;
    private final MergePathsManager mergePathsManager;
    private FileConfiguration config;

    @Getter @Setter private boolean databaseEnabled;
    @Getter private boolean debugMode;
    @Getter private boolean pluginNotif;
    @Getter private int energyRegen;
    @Getter private int maxCoins;
    @Getter private String coinsHook;

    public ConfigManager(Merger main) {
        this.main = main;
        this.mergePathsManager = main.getMergePathsManager();

        load(Bukkit.getConsoleSender());
        databaseEnabled = getBoolean(ConfigPath.DATABASE_ENABLED);
    }

    public boolean load(CommandSender sender) {
        config = main.getConfig();
        read(sender);
        return true;
    }

    @Override
    public boolean reload(CommandSender sender) {
        main.reloadConfig();
        return load(sender);
    }

    @Override
    public boolean read(CommandSender sender) {
        debugMode = getBoolean(ConfigPath.DEBUG_MODE);
        pluginNotif = getBoolean(ConfigPath.PLUGIN_NOTIF);
        energyRegen = getInt(ConfigPath.ENERGYREGEN);
        maxCoins = getInt(ConfigPath.COINSMAX);
        coinsHook = getString(ConfigPath.COINSHOOK);

        for (String rawPathIdentifier : config.getConfigurationSection(ConfigPath.ITEM_PATHS.getPath()).getKeys(false)) {
            mergePathsManager.addRawPath(rawPathIdentifier, getList(ConfigPath.ITEM_PATHS.getPath() + "." + rawPathIdentifier));
        }

        return true;
    }

    @Override
    public void save() {
        main.saveConfig();
    }

    public Boolean getBoolean(ConfigPath rawPath) { return config.getBoolean(rawPath.getPath(), (Boolean) rawPath.getDefaultValue()); }

    public String getString(ConfigPath rawPath) { return config.getString(rawPath.getPath(), (String) rawPath.getDefaultValue());}

    private int getInt(ConfigPath rawPath) {
        return config.getInt(rawPath.getPath(), (int) rawPath.getDefaultValue());
    }

    private List<String> getList(String path) { return config.isList(path) ? config.getStringList(path) : new ArrayList<>(); }
}
