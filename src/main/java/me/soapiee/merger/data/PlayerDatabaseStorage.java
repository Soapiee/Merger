package me.soapiee.merger.data;

import me.soapiee.merger.Merger;
import me.soapiee.merger.utils.ConfigPath;
import me.soapiee.merger.utils.CustomLogger;
import me.soapiee.merger.utils.Message;
import me.soapiee.merger.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class PlayerDatabaseStorage implements PlayerStorageHandler {

    private final Merger main;
    private final DataManager dataManager;
    private final PlayerData playerData;
    private final CustomLogger customLogger;

    private final UUID uuid;
    private final OfflinePlayer player;

    public PlayerDatabaseStorage(Merger main, PlayerData playerData) {
        this.main = main;
        dataManager = main.getDataManager();
        this.playerData = playerData;
        customLogger = main.getCustomLogger();
        uuid = playerData.getPlayer().getUniqueId();
        player = playerData.getPlayer();
    }

    @Override
    public CompletableFuture<PlayerData> readData() {
        CompletableFuture<AsyncData> completableFuture = CompletableFuture.supplyAsync(this::getAsync);

        return completableFuture
                .thenAcceptAsync(this::setData, BukkitExecutor.sync(main))
                .thenApplyAsync(ignored -> playerData);
    }

    private AsyncData getAsync() {
        AsyncData loadedData;

        try (Connection connection = dataManager.getDatabase().getDatabase().getConnection()) {
            //Check it exists, and if not, create entry
//            try (PreparedStatement existsStatement = connection.prepareStatement("SELECT ENERGY, COINS FROM " + table + " WHERE UUID=?")) {
            try (PreparedStatement existsStatement = connection.prepareStatement("SELECT * FROM DATA WHERE PLAYER=?")) {
                existsStatement.setString(1, uuid.toString());
                ResultSet results = existsStatement.executeQuery();

                if (!results.next()) {
                    // Create new
                    try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO DATA (PLAYER, ENERGY, COINS) VALUES (?,?,?);")) {
                        insertStatement.setString(1, uuid.toString());
                        insertStatement.setInt(2, (int) ConfigPath.ENERGY.getDefaultValue());
                        insertStatement.setDouble(3, (double) ConfigPath.COINS.getDefaultValue());
                        insertStatement.executeUpdate();
                    }
                    return new AsyncData();

                } else {
                    int energy = results.getInt("ENERGY");
                    double coins = results.getDouble("COINS");
//                    List<String> itemButtons = convertToList(results.getString("ITEMS"));
//                    Utils.consoleMsg("&cItems: " + itemButtons.size());
//                    List<String> unlockedProducers = convertToList(results.getString("PRODUCERS"));
//                    Utils.consoleMsg("&cProducers: " + unlockedProducers.size());
                    List<String> gameInv = convertToList(results.getString("GAMEINV"));
                    Utils.consoleMsg("&cGameInv: " + gameInv.size());
                    List<String> storageInv = convertToList(results.getString("STORAGEINV"));
                    Utils.consoleMsg("&cStorageInv: " + storageInv.size());
                    List<String> unlockedProducers = convertToList(results.getString("PRODUCERS"));
                    Utils.consoleMsg("&cProducers: " + unlockedProducers.size());

                    List<String> orders = convertToList(results.getString("ORDERS"));
                    loadedData = new AsyncData(energy, coins, gameInv, storageInv, unlockedProducers, orders);
                }
            }
        } catch (SQLException e) {
            throw new CompletionException(e);
        }

        return loadedData;
    }

    private List<String> convertToList(String rawString) {
        if (rawString == null) return new ArrayList<>();
        return Arrays.asList(rawString.split(","));
    }

//    private void setData(AsyncData loadedData) {
//        playerData.setEnergy(loadedData.getEnergy());
//        playerData.setCoins(loadedData.getCoins());
//
//        //Set Item Buttons
//        loadedData.getItemButtons().forEach(itemString -> {
//            ItemButton button = createButton(main, itemString);
//            if (button != null) playerData.addItemButton(button);
//        });
//
//        //Set Producers
//        loadedData.getUnlockedProducers().forEach(producerString -> {
//            ProducerButton button = createProducerButton(main, producerString);
//            if (button != null) playerData.addProducerButton(button);
//        });
//
//        //Set Orders

    /// /        loadedData.getOrders().forEach(producerString -> {
    /// /            Order order = createOrder(producerString);
    /// /            if (order != null) playerData.addOrder(order);
    /// /        });
//    }
    private void setData(AsyncData loadedData) {
        playerData.setEnergy(loadedData.getEnergy());
        playerData.setCoins(loadedData.getCoins());

        // Set Unlocked Producers
        registerUnlockedProducers(playerData, loadedData.getUnlockedProducers(), main.getItemsManager());

        // Set Game Inventory Items
        registerGameInventory(playerData, loadedData.getGameItems(), main);

        // Set Storage Inventory Items
        registerStorageInventory(playerData, loadedData.getStorageItems(), main);

        // Set Orders
        registerOrders(playerData, loadedData.getOrders());
    }

    @Override
    public void saveData(boolean async) {
        if (async) saveAsync(player.getName());
        else saveSync();
    }

    private void saveSync() {
        try (Connection connection = dataManager.getDatabase().getDatabase().getConnection()) {
//
//                try (PreparedStatement saveStatement = connection.prepareStatement("UPDATE DATA SET LEVEL=?, PROGRESS=? WHERE UUID=?;")) {
//            try (PreparedStatement saveStatement = connection.prepareStatement("UPDATE DATA SET ENERGY=?, COINS=?, ITEMS=?, PRODUCERS=?, ORDERS=? WHERE PLAYER=?;")) {
            try (PreparedStatement saveStatement = connection.prepareStatement("UPDATE DATA SET ENERGY=?, COINS=?, GAMEINV=?, STORAGEINV=?, PRODUCERS=?, ORDERS=? WHERE PLAYER=?;")) {
                saveStatement.setInt(1, playerData.getEnergy());
                saveStatement.setDouble(2, playerData.getCoins());

                // GameItems
                List<String> gameInvButtons = playerData.getGameInventory().getButtonsAsList();
                if (gameInvButtons == null || gameInvButtons.isEmpty()) saveStatement.setNull(3, Types.VARCHAR);
                else saveStatement.setString(3, gameInvButtons.toString());

                // StorageItems
                List<String> storageInvButtons = playerData.getStorageInventory().getButtonsAsList();
                if (storageInvButtons == null || storageInvButtons.isEmpty()) {
                    saveStatement.setNull(4, Types.VARCHAR);
                } else saveStatement.setString(4, storageInvButtons.toString());

                // Producers
                List<String> unlockedProducers = playerData.getUnlockedProducers().stream().toList();
                if (storageInvButtons == null || storageInvButtons.isEmpty()) {
                    saveStatement.setNull(5, Types.VARCHAR);
                } else saveStatement.setString(5, storageInvButtons.toString());

                // Orders
//                List<String> orders = playerData.getOrdersAsList();
//                if (orders == null || orders.isEmpty()) {
//                    saveStatement.setNull(6, Types.VARCHAR);
//                } else saveStatement.setString(6, orders.toString());
                saveStatement.setNull(6, Types.VARCHAR);

                saveStatement.setString(7, uuid.toString());
                saveStatement.executeUpdate();
            }

        } catch (SQLException error) {
            customLogger.logToFile(error, ChatColor.RED + player.getName() + "'s data could not be saved");
        }
    }

    private void saveAsync(final String playerName) {
        AsyncData loadedData = new AsyncData(playerData.getEnergy(),
                playerData.getCoins(),
//                playerData.getItemButtonsAsList(),
//                playerData.getUnlockedProducersAsList(),
                playerData.getGameInventory().getButtonsAsList(),
                playerData.getStorageInventory().getButtonsAsList(),
                playerData.getUnlockedProducers().stream().toList(),
                new ArrayList<>());

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            try (Connection connection = dataManager.getDatabase().getDatabase().getConnection()) {

                try (PreparedStatement saveStatement = connection.prepareStatement("UPDATE DATA SET ENERGY=?, COINS=?, GAMEINV=?, STORAGEINV=?, PRODUCERS=?, ORDERS=? WHERE PLAYER=?;")) {
                    saveStatement.setInt(1, loadedData.getEnergy());
                    saveStatement.setDouble(2, loadedData.getCoins());

//                    // Items
//                    List<String> itemButtons = loadedData.getItemButtons();
//                    if (itemButtons == null || itemButtons.isEmpty())
//                        saveStatement.setNull(3, Types.VARCHAR);
//                    else
//                        saveStatement.setString(3, itemButtons.toString());

                    // Game Items
                    List<String> gameInvButtons = loadedData.getGameItems();
                    if (gameInvButtons == null || gameInvButtons.isEmpty())
                        saveStatement.setNull(3, Types.VARCHAR);
                    else
                        saveStatement.setString(3, gameInvButtons.toString());

                    // Storage Items
                    List<String> storageInvButtons = loadedData.getGameItems();
                    if (storageInvButtons == null || storageInvButtons.isEmpty())
                        saveStatement.setNull(4, Types.VARCHAR);
                    else
                        saveStatement.setString(4, storageInvButtons.toString());

                    // Producers
                    List<String> producers = loadedData.getUnlockedProducers();
                    if (producers == null || producers.isEmpty())
                        saveStatement.setNull(5, Types.VARCHAR);
                    else
                        saveStatement.setString(5, producers.toString());

                    // Orders
//                List<String> orders = loadedData.getOrders();
//                if (orders == null || orders.isEmpty())
//                    saveStatement.setNull(5, Types.VARCHAR);
//                else saveStatement.setString(6, orders.toString());
                    saveStatement.setNull(6, Types.VARCHAR);

                    saveStatement.setString(7, uuid.toString());
                    saveStatement.executeUpdate();
                }

            } catch (SQLException error) {
                customLogger.logToFile(error, main.getMessageManager().getWithPlaceholder(Message.DATAERROR, playerName));
            }
        });
    }

}
