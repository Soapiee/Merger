package me.soapiee.merger.logic;

import me.soapiee.merger.Merger;
import me.soapiee.merger.factories.ProducerFactory;
import me.soapiee.merger.gui.core.Icon;
import me.soapiee.merger.factories.IconFactory;
import me.soapiee.merger.logic.items.Producer;
import me.soapiee.merger.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ProducersFileManager implements FileLoader {

    private final Merger main;
    private final CustomLogger customLogger;
    private final MessageManager messageManager;
    private final ProducerFactory producerFactory;
    private final IconFactory iconFactory;
    private final ItemsManager itemsManager;

    private final File file;
    private final YamlConfiguration config;

    public ProducersFileManager(Merger main) {
        this.main = main;
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();
        itemsManager = main.getItemsManager();
        producerFactory = main.getProducerFactory();
        iconFactory = main.getIconFactory();

        file = new File(main.getDataFolder(), "producers.yml");
        config = new YamlConfiguration();

        load(Bukkit.getConsoleSender());
    }

    @Override
    public boolean load(CommandSender sender) {
        if (!file.exists()) {
            main.saveResource("producers.yml", false);
        }

        try {
            config.load(file);
            producerFactory.setConfig(config);
        } catch (Exception ex) {
            customLogger.logToPlayer(sender, ex, messageManager.get(Message.DATAERROR));
            return false;
        }

        return read(sender);
    }

    @Override
    public boolean read(CommandSender sender) {
        for (String producerID : config.getConfigurationSection(ConfigPath.PRODUCER_LIST.getPath()).getKeys(false)) {
            if (itemsManager.isProducer(producerID)) continue;

            Icon icon = iconFactory.createIcon(config, getProducerPath(producerID), sender);
            Icon upgradeIcon = iconFactory.createIcon(config, getUpgradePath(producerID), sender);
            Producer producer = producerFactory.createProducer(producerID, icon, upgradeIcon, sender, itemsManager);
            if (producer == null) continue;

            //TODO Make Message enum
            customLogger.logToPlayer(sender, null, "&aSuccessfully created producer with ID: " + producerID);
            itemsManager.registerProducer(producer);
        }

        return true;
    }

    private String getProducerPath(String producerID) {
        return ConfigPath.PRODUCER_ICON.getPath().replace("{producer}", producerID);
    }
    private String getUpgradePath(String producerID) {
        return ConfigPath.PRODUCER_UPGRADE_ICON.getPath().replace("{producer}", producerID);
    }

    @Override
    public void save() {

    }
}
