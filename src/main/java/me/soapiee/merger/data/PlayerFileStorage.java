package me.soapiee.merger.data;

import me.soapiee.merger.Merger;
import me.soapiee.merger.utils.ConfigPath;
import me.soapiee.merger.utils.CustomLogger;
import me.soapiee.merger.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class PlayerFileStorage implements PlayerStorageHandler {

    private final Merger main;
    private final PlayerData playerData;
    private final CustomLogger customLogger;
    private final Object fileLock = new Object();

    private final File file;
    private final OfflinePlayer player;

    private final ConfigPath ENERGYPATH = ConfigPath.ENERGY;
    private final ConfigPath COINSPATH = ConfigPath.COINS;
    private final ConfigPath GAMEINVPATH = ConfigPath.GAMEINV;
    private final ConfigPath STORAGEINVPATH = ConfigPath.STORAGEINV;
    private final ConfigPath PRODUCERSPATH = ConfigPath.UNLOCKED_PRODUCERS;
    private final ConfigPath ORDERSPATH = ConfigPath.ORDERS;

    public PlayerFileStorage(Merger main, PlayerData playerData) {
        this.main = main;
        this.playerData = playerData;
        customLogger = main.getCustomLogger();
        player = playerData.getPlayer();

        UUID uuid = playerData.getPlayer().getUniqueId();
        file = new File(main.getDataFolder() + File.separator + "data", uuid + ".yml");
    }

    @Override
    public CompletableFuture<PlayerData> readData() {
        CompletableFuture<AsyncData> completableFuture =
                (file.exists() ? CompletableFuture.supplyAsync(this::getAsync) : CompletableFuture.supplyAsync(this::createFile));

        return completableFuture
                .thenAcceptAsync(this::setData, BukkitExecutor.sync(main))
                .thenApplyAsync(ignored -> playerData);
    }

    private AsyncData getAsync() {
        AsyncData loadedData;

//        try {
        YamlConfiguration contents = YamlConfiguration.loadConfiguration(file);
//            boolean updated = false;

        synchronized (fileLock) {
            int energy = contents.getInt(ENERGYPATH.getPath(), (int) ENERGYPATH.getDefaultValue());
            double coins = contents.getDouble(COINSPATH.getPath(), (double) COINSPATH.getDefaultValue());
//            List<String> itemButtons = contents.getStringList(ITEMS.getPath());
            List<String> gameButtons = contents.getStringList(GAMEINVPATH.getPath());
            List<String> storageButtons = contents.getStringList(STORAGEINVPATH.getPath());
            List<String> unlockedProducers = contents.getStringList(PRODUCERSPATH.getPath());
            List<String> orders = contents.getStringList(ORDERSPATH.getPath());
            loadedData = new AsyncData(energy, coins, gameButtons, storageButtons, unlockedProducers, orders);

//                if (updated) contents.save(file);
        }
//        } catch (IOException error) {
//            throw new CompletionException(error);
//        }

        return loadedData;
    }

    private AsyncData createFile() {
        synchronized (fileLock) {
            try {
                YamlConfiguration localCopy = new YamlConfiguration();

                localCopy.set(ENERGYPATH.getPath(), (int) ENERGYPATH.getDefaultValue());
                localCopy.set(COINSPATH.getPath(), (double) COINSPATH.getDefaultValue());
//                localCopy.set(ITEMSPATH.getPath(), new ArrayList<>());
                localCopy.set(GAMEINVPATH.getPath(), new ArrayList<>());
                localCopy.set(STORAGEINVPATH.getPath(), new ArrayList<>());
                localCopy.set(PRODUCERSPATH.getPath(), new ArrayList<>());

                localCopy.save(file);
            } catch (IOException error) {
                throw new CompletionException(error);
            }
        }

        return new AsyncData();
    }
//
//    private void registerUnlockedProducers(List<String> unlockedProducers, ItemsManager itemsManager) {
//        unlockedProducers.forEach(string -> {
//            if (!itemsManager.isProducer(string)) return;
//            playerData.getUnlockedProducers().add(string);
//        });
//    }
//
//    private void registerGameInventory(List<String> rawItems, ItemsManager itemsManager) {
//        GameInventory gameInventory = playerData.getGameInventory();
//        rawItems.forEach(string -> {
//            if (itemsManager.isProducer(string)) {
//                ProducerButton button = createProducerButton(main, string);
//                if (button != null) gameInventory.addProducerButton(button);
//                return;
//            }
//
//            if (itemsManager.isMergableItem(string)) {
//                ItemButton button = createButton(main, string);
//                if (button != null) gameInventory.addItemButton(button);
//                return;
//            }
//        });
//    }
//
//    private void registerStorageInventory(List<String> rawItems, ItemsManager itemsManager) {
//        StorageInventory storageInventory = playerData.getStorageInventory();
//        rawItems.forEach(string -> {
//            if (itemsManager.isProducer(string)) {
//                ProducerButton button = createProducerButton(main, string);
//                if (button != null) storageInventory.addProducerButton(button);
//                return;
//            }
//
//            if (itemsManager.isMergableItem(string)) {
//                ItemButton button = createButton(main, string);
//                if (button != null) storageInventory.addItemButton(button);
//                return;
//            }
//        });
//    }
//
//    private void registerOrders(List<String> rawOrders) {
//
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

        //Set Item Buttons
//        loadedData.getItemButtons().forEach(itemString -> {
//            ItemButton button = createButton(main, itemString);
//            if (button != null) playerData.addItemButton(button);
//        });
//
        //Set Producers
//        loadedData.getUnlockedProducers().forEach(producerString -> {
//            ProducerButton button = createProducerButton(main, producerString);
//            if (button != null) playerData.addProducerButton(button);
//        });

        //Set Orders
//        loadedData.getOrders().forEach(producerString -> {
//            Order order = createOrder(producerString);
//            if (order != null) playerData.addOrder(order);
//        });
    }

    @Override
    public void saveData(boolean async) {
        if (async) saveAsync(player.getName());
        else saveSync();
    }

    private void saveSync() {
        YamlConfiguration localCopy = YamlConfiguration.loadConfiguration(file);
        GameInventory gameInventory = playerData.getGameInventory();

        synchronized (fileLock) {
            localCopy.set(ENERGYPATH.getPath(), playerData.getEnergy());
            localCopy.set(COINSPATH.getPath(), playerData.getCoins());
//            localCopy.set(ITEMSPATH.getPath(), gameInventory.getItemButtonsAsList());
            localCopy.set(GAMEINVPATH.getPath(), gameInventory.getButtonsAsList());
            localCopy.set(STORAGEINVPATH.getPath(), gameInventory.getButtonsAsList());
            localCopy.set(PRODUCERSPATH.getPath(), playerData.getUnlockedProducers().stream().toList());
            localCopy.set(ORDERSPATH.getPath(), new ArrayList<>());
        }

        try {
            localCopy.save(file);
        } catch (IOException e) {
            customLogger.logToFile(e, main.getMessageManager().getWithPlaceholder(Message.DATAERROR, player.getName()));
        }
    }

    private void saveAsync(final String playerName) {
        GameInventory gameInventory = playerData.getGameInventory();
        StorageInventory storageInventory = playerData.getStorageInventory();

        AsyncData loadedData = new AsyncData(playerData.getEnergy(),
                playerData.getCoins(),
                gameInventory.getButtonsAsList(),
                storageInventory.getButtonsAsList(),
                playerData.getUnlockedProducers().stream().toList(),
                new ArrayList<>());

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            YamlConfiguration contents = YamlConfiguration.loadConfiguration(file);

            synchronized (fileLock) {
                contents.set(ENERGYPATH.getPath(), loadedData.getEnergy());
                contents.set(COINSPATH.getPath(), loadedData.getCoins());
//                contents.set(ITEMSPATH.getPath(), loadedData.getItemButtons());
                contents.set(GAMEINVPATH.getPath(), loadedData.getGameItems());
                contents.set(STORAGEINVPATH.getPath(), loadedData.getStorageItems());
                contents.set(PRODUCERSPATH.getPath(), loadedData.getUnlockedProducers());
                contents.set(ORDERSPATH.getPath(), loadedData.getOrders());
            }

            try {
                contents.save(file);
            } catch (IOException e) {
                customLogger.logToFile(e, main.getMessageManager().getWithPlaceholder(Message.DATAERROR, playerName));
            }
        });
    }

}
