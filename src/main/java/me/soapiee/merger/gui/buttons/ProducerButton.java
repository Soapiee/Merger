package me.soapiee.merger.gui.buttons;

import lombok.Getter;
import lombok.Setter;
import me.soapiee.merger.Merger;
import me.soapiee.merger.data.PlayerData;
import me.soapiee.merger.gui.pages.game.GamePage;
import me.soapiee.merger.logic.ProducerOverheatTask;
import me.soapiee.merger.logic.items.MergableItem;
import me.soapiee.merger.logic.items.Producer;
import me.soapiee.merger.utils.Keys;
import me.soapiee.merger.utils.Utils;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProducerButton implements ButtonsHandler {

    @Getter private final Producer producer;
    @Getter private final ItemStack icon;
    private final List<String> lore;
    @Getter private final String ID;
    @Setter @Getter private int slot;
    private final int CLICK_THRESHOLD = 250;
    @Getter private ProducerOverheatTask overheatTask;

    @Getter private int maxUses;
    @Getter private int overHeatCount;
    private final int overHeatDelay;
    @Getter private LocalDateTime overHeatExpiry;
    private boolean overHeating;
    private LocalDateTime lastClicked;

    public ProducerButton(Merger main, Producer producer, HashMap<String, Object> data) {
        this.producer = producer;
        this.slot = (int) data.get("slot");
        this.ID = producer.getIdentifier();
        lore = producer.getIcon().getLore();
        maxUses = (int) data.get("maxUses");
        overHeatCount = (int) data.get("overHeatCount");
        overHeatDelay = producer.getOverHeatDelay();
        overHeatExpiry = (LocalDateTime) data.get("overHeatExpiry");
        overHeating = isOverHeating(main);
        lastClicked = null;
        icon = setIcon();
    }

    public ProducerButton(Merger main, Producer producer, int slot) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("slot", slot);
        data.put("maxUses", producer.getMaxUses());
        data.put("overHeatCount", producer.getOverHeatCount());
        data.put("overHeatExpiry", null);

        this(main, producer, data);
    }

    public ItemStack setIcon() {
        ItemStack newItem = producer.getIcon().getItemStack().clone();

        ItemMeta meta = newItem.getItemMeta();
        meta.getPersistentDataContainer().set(Keys.PRODUCERKEY, PersistentDataType.STRING, ID);
        meta.setLore(getLore());

        newItem.setItemMeta(meta);

        return newItem;
    }

    @Override
    public void onClick(InventoryClickEvent event, Merger main, PlayerData playerData) {
        boolean debugMode = main.getConfigManager().isDebugMode();
        if (debugMode) Utils.consoleMsg("&a&lProducer button clicked");

        ItemStack handItem = getItem(event.getCursor());
        ItemStack slotItem = getItem(event.getCurrentItem());

        if (handItem != null && slotItem != null) event.setCancelled(true);
        if (handItem == null && slotItem == null) event.setCancelled(true);

        ClickType click = event.getClick();

        //Producer is being picked up
        if (handItem == null && slotItem != null && click == ClickType.RIGHT) processItemPickup(debugMode);

        //Trying to produce an item from the producer
        if (handItem == null && slotItem != null && click == ClickType.LEFT)
            validateProducingItem(event, main, playerData, debugMode);

        //Producer is being placed
        if (handItem != null && slotItem == null) processItemMove(event, click, debugMode);
    }

    private void processItemPickup(boolean debugMode) {
        if (debugMode) Utils.consoleMsg("&6Producer picked up");
    }

    private void processItemMove(InventoryClickEvent event, ClickType click, boolean debugMode) {
        if (click == ClickType.LEFT) updateSlot(event, debugMode);
        else event.setCancelled(true);
    }

    private void validateProducingItem(InventoryClickEvent event, Merger main, PlayerData playerData, boolean debugMode) {
        if (debugMode) Utils.consoleMsg("&6Producer trying to produce");
        event.setCancelled(true);

        if (overHeating) return;
//        if (debugMode) Utils.consoleMsg("&6Producer is not overheating");
        if (onCooldown(debugMode)) return;

        int emptySlot = getNewSlot(event.getInventory(), debugMode);
        if (emptySlot == -1) return;

        produceItem(main, playerData, emptySlot, debugMode);
    }

    private void produceItem(Merger main, PlayerData playerData, int emptySlot, boolean debugMode) {
        MergableItem producedItem = producer.produce();
        playerData.getGameInventory().addItemButton(new ItemButton(producedItem, emptySlot));

        registerUsage(main, playerData, debugMode);
        main.getGuiManager().openGUI(new GamePage(main, playerData), playerData.getPlayer().getPlayer());
    }

    private int getNewSlot(Inventory inventory, boolean debugMode) {
        boolean[] occupiedSlots = getOccupiedSlots(inventory);
        int slot = findNearestSlot(occupiedSlots, this.slot);
        if (debugMode) Utils.consoleMsg("&6Empty slot: " + slot);

        return slot;
    }

    private boolean[] getOccupiedSlots(Inventory inventory) {
        int maxSlot = inventory.getSize();
        int minLimit = 9;
        boolean[] occupied = new boolean[maxSlot];

        for (int i = 0; i < inventory.getSize(); i++) {
            if (i < minLimit) {
                occupied[i] = true;
                continue;
            }

            ItemStack item = inventory.getItem(i);
            occupied[i] = item != null && item.getType() != Material.AIR;
        }

        return occupied;
    }

    private int findNearestSlot(boolean[] occupied, int producerSlot) {
        int size = occupied.length;
        int width = 9;

        int targetX = producerSlot % width;
        int targetY = producerSlot / width;

        int bestSlot = -1;
        double bestDistance = Double.MAX_VALUE;

        for (int slot = 0; slot < size; slot++) {

            if (occupied[slot]) continue;

            int x = slot % width;
            int y = slot / width;

            int dx = x - targetX;
            int dy = y - targetY;

            double dist = dx * dx + dy * dy; // squared distance

            if (dist < bestDistance) {
                bestDistance = dist;
                bestSlot = slot;
            }
        }

        return bestSlot;
    }

    private void updateSlot(InventoryClickEvent event, boolean debugMode) {
        if (debugMode) Utils.consoleMsg("&cSlot updated: " + getID());
        int eventSlot = event.getRawSlot();
        if (eventSlot != slot) setSlot(eventSlot);
    }

    private ItemStack getItem(ItemStack item) {
        if (item == null) return null;

        return item.getType() == Material.AIR ? null : item;
    }

    private boolean onCooldown(boolean debugMode) {
        if (lastClicked == null) return false;

        if (lastClickExpired(lastClicked)) {
            lastClicked = null;
            return false;
        }

        if (debugMode) Utils.consoleMsg("&6Producer is on cooldown");
        return true;
    }

    private boolean lastClickExpired(LocalDateTime toCompare) {
        return getMSRemaining(toCompare) <= 0;
    }

    private boolean isOverHeating(Merger main) {
        if (overHeatExpiry != null) {
            overheatTask = new ProducerOverheatTask(main, this, getSecsRemaining(overHeatExpiry) * 20L);
            return true;
        } else return false;
    }

    private long getSecsRemaining(LocalDateTime toCompare) {
        return ChronoUnit.SECONDS.between(LocalDateTime.now(), toCompare);
    }

    private long getMSRemaining(LocalDateTime toCompare) {
        long difference = ChronoUnit.MILLIS.between(toCompare, LocalDateTime.now());
        return CLICK_THRESHOLD - difference;
    }

    private void registerUsage(Merger main, PlayerData playerData, boolean debugMode) {
        lastClicked = LocalDateTime.now();

        if (maxUses != -1) {
            maxUses--;
        }

        if (maxUses == 0) {
            if (debugMode) Utils.consoleMsg("&6Producer hit max uses");
            playerData.getGameInventory().removeProducerButton(getID());
            return;
        }

        if (overHeatCount != -1) {
            overHeatCount--;
//        if (debugMode) Utils.consoleMsg("&6Overheat count: " + overHeatCount);
        }

        if (overHeatCount == 0) {
            if (debugMode) Utils.consoleMsg("&6Producer has overheated");
            overHeating = true;
            overHeatExpiry = LocalDateTime.now().plusSeconds(overHeatDelay);

            overheatTask = new ProducerOverheatTask(main, this, overHeatDelay * 20L);
        }

        updateLore();
    }

    public void resetOverHeating() {
        overHeating = false;
        overHeatExpiry = null;
        overHeatCount = producer.getOverHeatCount();
    }

    private void updateLore() {
        ItemMeta meta = icon.getItemMeta();
        meta.setLore(getLore());
        icon.setItemMeta(meta);
    }

    private List<String> getLore() {
        ArrayList<String> updatedLore = new ArrayList<>();

        for (String line : lore) {
            String string = line;
            if (string.contains("%max_uses%")) string = string.replace("%max_uses%", String.valueOf(maxUses));
            if (string.contains("%overheat%")) string = string.replace("%overheat%", String.valueOf(overHeatCount));

            updatedLore.add(Utils.addColour(string));
        }

        return updatedLore;
    }
}
