package me.soapiee.merger;

import lombok.Getter;
import me.soapiee.merger.commands.AdminCmd;
import me.soapiee.merger.commands.PlayerCmd;
import me.soapiee.merger.data.DataManager;
import me.soapiee.merger.factories.OrderFactory;
import me.soapiee.merger.factories.ProducerFactory;
import me.soapiee.merger.gui.core.GUIManager;
import me.soapiee.merger.gui.core.GUISettingsManager;
import me.soapiee.merger.factories.IconFactory;
import me.soapiee.merger.hooks.PlaceHolderAPIHook;
import me.soapiee.merger.listeners.ConnectListener;
import me.soapiee.merger.listeners.GUIListener;
import me.soapiee.merger.logic.ItemsFileManager;
import me.soapiee.merger.logic.ItemsManager;
import me.soapiee.merger.logic.MergePathsManager;
import me.soapiee.merger.logic.ProducersFileManager;
import me.soapiee.merger.managers.*;
import me.soapiee.merger.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class Merger extends JavaPlugin {

    @Getter private DataManager dataManager;
    @Getter private MessageManager messageManager;
    @Getter private PlayerCache playerCache;
    @Getter private CustomLogger customLogger;
    @Getter private ConfigManager configManager;
    @Getter private IconFactory iconFactory;
    @Getter private OrderFactory orderFactory;
    @Getter private ProducerFactory producerFactory;
    @Getter private GUISettingsManager guiSettingsManager;
    @Getter private GUIManager guiManager;
    @Getter private ItemsManager itemsManager;
    @Getter private ItemsFileManager itemsFileManager;
    @Getter private ProducersFileManager producersFileManager;
    @Getter private MergePathsManager mergePathsManager;
//    @Getter private UpdateManager updateManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        playerCache = new PlayerCache(Bukkit.getServer().getOfflinePlayers());
        messageManager = new MessageManager(this);
        customLogger = new CustomLogger(this);
        initiateFactories();
        initiateManagers();

        //Data setup
        dataManager = new DataManager(this);

        try {
            dataManager.initialise(this);
        } catch (IOException e) {
            customLogger.logToFile(e, messageManager.get(Message.MAJORDATAERROR));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        registerHooks();
        registerListeners();
        registerCommands();

        // TODO:
//        updateManager = new UpdateManager(this, 125077);
//        updateManager.updateAlert(Bukkit.getConsoleSender());
//        new Metrics(this, 28832);
    }

    @Override
    public void onDisable() {
        guiManager.closeAll();

        if (dataManager == null) return;

        //Save player data
        dataManager.saveAll();

        if (dataManager.getDatabase() != null) dataManager.getDatabase().disconnect();
    }

    private void registerHooks() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceHolderAPIHook(this).register();
            Utils.consoleMsg(messageManager.get(Message.HOOKEDPLACEHOLDERAPI));
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ConnectListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
    }

    private void initiateFactories() {
        iconFactory = new IconFactory(this);
        producerFactory = new ProducerFactory(this);
        orderFactory = new OrderFactory();
    }

    private void initiateManagers() {
        mergePathsManager = new MergePathsManager(this);
        configManager = new ConfigManager(this);
        itemsManager = new ItemsManager();
        itemsFileManager = new ItemsFileManager(this);
        producersFileManager = new ProducersFileManager(this);
        mergePathsManager.validateMergePaths(this, Bukkit.getConsoleSender());
        itemsManager.setStarterProducer(configManager.getString(ConfigPath.STARTERPRODUCER));
        guiSettingsManager = new GUISettingsManager(this);
        guiManager = new GUIManager();
    }

    private void registerCommands() {
        getCommand("amerge").setExecutor(new AdminCmd(this));
        getCommand("merge").setExecutor(new PlayerCmd(this));
    }
}
