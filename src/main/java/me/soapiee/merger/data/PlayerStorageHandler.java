package me.soapiee.merger.data;

import me.soapiee.merger.Merger;
import me.soapiee.merger.gui.buttons.ItemButton;
import me.soapiee.merger.gui.buttons.ProducerButton;
import me.soapiee.merger.logic.ItemsManager;
import me.soapiee.merger.logic.items.Item;
import me.soapiee.merger.logic.items.Producer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PlayerStorageHandler {

    CompletableFuture<PlayerData> readData();
    void saveData(boolean async);

    default ItemButton createButton(Merger main, String itemString) {
        String[] split = itemString.replace("[", "")
                .replace("]", "")
                .split(";");
        if (split.length != 2) return null;

        int slot = validateSlot(split[0]);
        if (slot == -1) return null;

        Item item = getItem(main.getItemsManager(), split[1]);
        if (item == null) return null;

        return new ItemButton(item, slot);
    }

    default ProducerButton createProducerButton(Merger main, String itemString) {
        String[] split = itemString.replace("[", "")
                .replace("]", "")
                .split(";");
        if (split.length != 5) return null;

        int slot = validateSlot(split[0]);
        if (slot == -1) return null;

        Producer producer = main.getItemsManager().getProducer(split[1]);
        if (producer == null) return null;

        int maxUses = validateMaxUses(producer, split[2]);
        if (maxUses == 0) return null;

        int overHeatCount = validateOverHeatCount(producer, split[4]);
        LocalDateTime overHeatExpiry = validateOverHeatExpiry(split[3]);

        if (overHeatExpiry == null && overHeatCount <= 0) overHeatCount = producer.getOverHeatCount();

        HashMap<String, Object> data = new HashMap<>();
        data.put("slot", slot);
        data.put("maxUses", maxUses);
        data.put("overHeatCount", overHeatCount);
        data.put("overHeatExpiry", overHeatExpiry);

        return new ProducerButton(main, producer, data);
    }

    private Item getItem(ItemsManager itemsManager, String rawID) {
        Item item = null;
        if (itemsManager.isMergableItem(rawID)) item = itemsManager.getMergableItem(rawID);
        if (itemsManager.isStaticItem(rawID)) item = itemsManager.getStaticItem(rawID);

        return item;
    }

    private int validateSlot(String rawSlot) {
        int slot;

        try {
            slot = Integer.parseInt(rawSlot);
        } catch (NumberFormatException e) {
            slot = -1;
        }

        //TODO:
        int minSlot = 0;
        int maxSlot = 54;

        return (slot < minSlot || slot > maxSlot) ? -1 : slot;
    }

    private int validateMaxUses(Producer producer, String rawMaxUses) {
        int producerMaxUses = producer.getMaxUses();

        if (producerMaxUses == -1) return -1;
        int maxUses;

        try {
            maxUses = Integer.parseInt(rawMaxUses);
        } catch (NumberFormatException e) {
            maxUses = -1;
        }

        return (maxUses == -1 && producerMaxUses > 0) ? producerMaxUses : maxUses;
    }

    private int validateOverHeatCount(Producer producer, String rawCount) {
        int maxCount = producer.getOverHeatCount();
        int count;

        try {
            count = Integer.parseInt(rawCount);
        } catch (NumberFormatException e) {
            count = maxCount;
        }

        return (count < 0 || count > maxCount) ? maxCount : count;
    }

    private LocalDateTime validateOverHeatExpiry(String rawLocalDateTime) {
        if (rawLocalDateTime == null || rawLocalDateTime.equalsIgnoreCase("null")) return null;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
        LocalDateTime overHeatExpiry;
        try {
            overHeatExpiry = LocalDateTime.parse(rawLocalDateTime, dtf);
        } catch (DateTimeParseException e) {
            return null;
        }

        return isExpired(overHeatExpiry) ? null : overHeatExpiry;
    }

    private boolean isExpired(LocalDateTime overHeatExpiry) {
        long difference = ChronoUnit.SECONDS.between(LocalDateTime.now(), overHeatExpiry);

        return difference <= 0;
    }

    default void registerUnlockedProducers(PlayerData playerData, List<String> unlockedProducers, ItemsManager itemsManager) {
        unlockedProducers.forEach(string -> {
            if (!itemsManager.isProducer(string)) return;
            playerData.getUnlockedProducers().add(string);
        });
    }

    default void registerGameInventory(PlayerData playerData, List<String> rawItems, Merger main) {
        GameInventory gameInventory = playerData.getGameInventory();
        ItemsManager itemsManager = main.getItemsManager();

        rawItems.forEach(string -> {
            if (itemsManager.isProducer(string)) {
                ProducerButton button = createProducerButton(main, string);
                if (button != null) gameInventory.addProducerButton(button);
                return;
            }

            if (itemsManager.isMergableItem(string)) {
                ItemButton button = createButton(main, string);
                if (button != null) gameInventory.addItemButton(button);
                return;
            }
        });
    }

    default void registerStorageInventory(PlayerData playerData, List<String> rawItems, Merger main) {
        StorageInventory storageInventory = playerData.getStorageInventory();
        ItemsManager itemsManager = main.getItemsManager();

        rawItems.forEach(string -> {
            if (itemsManager.isProducer(string)) {
                ProducerButton button = createProducerButton(main, string);
                if (button != null) storageInventory.addProducerButton(button);
                return;
            }

            if (itemsManager.isMergableItem(string)) {
                ItemButton button = createButton(main, string);
                if (button != null) storageInventory.addItemButton(button);
                return;
            }
        });
    }

    default void registerOrders(PlayerData playerData, List<String> rawOrders) {

    }
}
