package me.soapiee.merger.data;

import lombok.Getter;
import lombok.Setter;
import me.soapiee.merger.Merger;
import me.soapiee.merger.enums.EnergyChangeResult;
import me.soapiee.merger.gui.buttons.ProducerButton;
import me.soapiee.merger.logic.ItemsManager;
import me.soapiee.merger.logic.items.Producer;
import me.soapiee.merger.managers.ConfigManager;
import me.soapiee.merger.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PlayerData {

    private final PlayerStorageHandler storageHandler;
    @Getter private final UUID uuid;
    @Getter private final OfflinePlayer player;

    @Setter @Getter private int energy;
    @Setter @Getter private double coins;
    @Getter private final GameInventory gameInventory;
    @Getter private final StorageInventory storageInventory;
    //    @Getter private final Map<String, ItemButton> itemButtons = new ConcurrentHashMap<>();
//    @Getter private final Map<String, ProducerButton> producerButtons = new ConcurrentHashMap<>();
    @Getter private final Set<String> unlockedProducers = new HashSet<>();
    //    private final HashSet<Order> orders;

    public PlayerData(Merger main, @NotNull OfflinePlayer player) {
        this.player = player;
        uuid = player.getUniqueId();
        gameInventory = new GameInventory();
        storageInventory = new StorageInventory();

        ConfigManager configManager = main.getConfigManager();
        storageHandler = (configManager.isDatabaseEnabled() ? new PlayerDatabaseStorage(main, this) : new PlayerFileStorage(main, this));
    }

    public CompletableFuture<PlayerData> load() {
        return storageHandler.readData();
    }

    public void saveData(boolean async) {
        storageHandler.saveData(async);
    }

    public EnergyChangeResult addEnergy(int amount) {
        if (amount <= 0) return EnergyChangeResult.ENERGY_ADDED_FAIL;

        energy += amount;

        //TODO: Check new amount isnt above max value

        return EnergyChangeResult.ENERGY_ADDED_SUCCESS;
    }

    public EnergyChangeResult removeEnergy(int amount) {
        if (amount <= 0) return EnergyChangeResult.ENERGY_REMOVED_FAIL;

        energy -= amount;

        //TODO: Check new amount isnt below 0

        return EnergyChangeResult.ENERGY_REMOVED_SUCCESS;
    }

//    public List<String> getUnlockedProducersAsList() {
//        ArrayList<String> list = new ArrayList<>();
//
//        producerButtons.forEach((identifier, producerButton) -> {
//            String string = producerButton.getSlot()
//                    + ";" + producerButton.getProducer().getIdentifier()
//                    + ";" + producerButton.getMaxUses()
//                    + ";" + parseOverHeatExpiry(producerButton.getOverHeatExpiry())
//                    + ";" + producerButton.getOverHeatCount();
//            list.add(string);
//        });
//
//        return list;
//    }

    public List<Producer> getLockedProducers(ItemsManager itemsManager) {
        Set<Producer> allProducers = itemsManager.getProducers();

        // Collect unlocked producer identifiers
        Set<String> unlockedIds = gameInventory.getProducerButtons().values().stream()
                .map(button -> button.getProducer().getIdentifier().toLowerCase())
                .collect(Collectors.toSet());

        return allProducers.stream()
                .filter(producer ->
                        !unlockedIds.contains(producer.getIdentifier().toLowerCase()))
                .sorted(Comparator.comparingDouble(Producer::getUpgradeCost))
                .toList();
    }

    //TODO:
    public void unlockProducer(Merger main, Producer producer) {
        //TODO: Try to add it to main GUI, if full, add it to storage
        //TODO: remove coins

        if (producer == null) return;
        if (!gameInventory.hasSpace() && !storageInventory.hasSpace()) return;

        //Check if its the starter producer, if not then take coins away
        if (main.getItemsManager().getStarterProducer() != producer) {
            Utils.consoleMsg("&6Is Starter producer, so no coins deducted");
            double upgradeCost = producer.getUpgradeCost();
            if (upgradeCost > coins) return;
            coins -= upgradeCost;
        }

        unlockedProducers.add(producer.getIdentifier().toLowerCase());

        if (gameInventory.hasSpace()) {
            gameInventory.addProducerButton(new ProducerButton(main, producer, gameInventory.getEmptySlot()));
            //Needs to find next available slot
        } else {
            storageInventory.addProducerButton(new ProducerButton(main, producer, storageInventory.getEmptySlot()));
        }
    }

//    private String parseOverHeatExpiry(LocalDateTime overHeatExpiry) {
//        if (overHeatExpiry == null) return "null";
//
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
//        return overHeatExpiry.format(dtf);
//    }

//    public ArrayList<String> getItemButtonsAsList() {
//        ArrayList<String> list = new ArrayList<>();
//
//        itemButtons.forEach((buttonID, button) -> {
//            String string = button.getSlot() + ";" + button.getItem().getIdentifier();
//            list.add(string);
//        });
//
//        return list;
//    }
//
//    public void addItemButton(ItemButton button) {
//        itemButtons.put(button.getID(), button);
//    }
//
//    public void removeItemButton(String buttonID) {
//        itemButtons.remove(buttonID);
//    }

//    public void addProducerButton(ProducerButton button) {
//        producerButtons.put(button.getID(), button);
//    }
//
//    public void removeProducerButton(String buttonID) {
//        producerButtons.remove(buttonID);
//    }
}
