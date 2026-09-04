package me.soapiee.merger.logic;

import me.soapiee.merger.Merger;
import me.soapiee.merger.gui.core.Icon;
import me.soapiee.merger.factories.IconFactory;
import me.soapiee.merger.logic.items.MergableItem;
import me.soapiee.merger.logic.items.StaticItem;
import me.soapiee.merger.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ItemsFileManager implements FileLoader {

    private final Merger main;
    private final CustomLogger customLogger;
    private final MessageManager messageManager;
    private final MergePathsManager mergePathsManager;
    private final IconFactory iconFactory;
    private final ItemsManager itemsManager;

    private final File file;
    private final YamlConfiguration config;

    public ItemsFileManager(Merger main) {
        this.main = main;
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();
        mergePathsManager = main.getMergePathsManager();
        itemsManager = main.getItemsManager();
        iconFactory = main.getIconFactory();

        file = new File(main.getDataFolder(), "items.yml");
        config = new YamlConfiguration();

        load(Bukkit.getConsoleSender());
    }

    @Override
    public boolean load(CommandSender sender) {
        if (!file.exists()) {
            main.saveResource("items.yml", false);
        }

        try {
            config.load(file);
        } catch (Exception ex) {
            customLogger.logToPlayer(sender, ex, messageManager.get(Message.DATAERROR));
            return false;
        }

        return read(sender);
    }

    @Override
    public boolean read(CommandSender sender) {
        for (String itemID : config.getConfigurationSection(ConfigPath.ITEM_LIST.getPath()).getKeys(false)) {
            Icon icon = iconFactory.createIcon(config, getItemPath(itemID), sender);

            if (mergePathsManager.existsRaw(itemID)) {
                if (itemsManager.isMergableItem(itemID)) continue;
                itemsManager.registerMergableItem(new MergableItem(itemID, icon));
            } else {
                if (itemsManager.isStaticItem(itemID)) continue;
                itemsManager.registerStaticItem(new StaticItem(itemID, icon));
            }
        }

        return true;
    }

    private String getItemPath(String itemID) {
        return ConfigPath.ITEM_ICON.getPath().replace("{item}", itemID);
    }

    @Override
    public void save() {
    }
}
