package me.soapiee.merger.data;

import com.zaxxer.hikari.pool.HikariPool;
import lombok.Getter;
import me.soapiee.merger.Merger;
import me.soapiee.merger.managers.ConfigManager;
import me.soapiee.merger.utils.MessageManager;
import me.soapiee.merger.utils.CustomLogger;
import me.soapiee.merger.utils.Message;
import me.soapiee.merger.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

public class DataManager {

    @Getter private final PlayerDataManager playerDataManager;
    private final ConfigManager configManager;
    private final CustomLogger customLogger;
    private final MessageManager messageManager;

    @Getter private HikariCPConnection database;

    public DataManager(Merger main) {
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();

        playerDataManager = new PlayerDataManager(main);
        configManager = main.getConfigManager();
    }

    public void initialise(Merger main) throws IOException {
        if (!configManager.isDatabaseEnabled()) {
            initialiseFiles(main);
            return;
        }

        try {
            initialiseDatabase(main.getConfig());
            Utils.consoleMsg(messageManager.get(Message.DATABASECONNECTED));
        } catch (SQLException | HikariPool.PoolInitializationException e) {
            customLogger.logToFile(e, messageManager.get(Message.DATABASEFAILED));
            initialiseFiles(main);
        }
    }

    private void initialiseDatabase(FileConfiguration config) throws SQLException, IOException {
        database = new HikariCPConnection(config);
        database.connect();
    }

    public void initialiseFiles(Merger main) {
        configManager.setDatabaseEnabled(false);
        database = null;

        checkDirectory(main);
    }

    private void checkDirectory(Merger main) {
        if (Files.isDirectory(Paths.get(main.getDataFolder() + File.separator + "data"))) return;

        try {
            Files.createDirectories(Paths.get(main.getDataFolder() + File.separator + "data"));
        } catch (IOException e) {
            customLogger.logToFile(e, messageManager.get(Message.FILEFOLDERERROR));
            return;
        }

        Utils.consoleMsg(messageManager.get(Message.FILESYSTEMACTIVATED));
    }

    public void saveAll() {
        playerDataManager.saveAll(false);
    }
}
