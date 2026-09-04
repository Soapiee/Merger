package me.soapiee.merger.factories;

import lombok.Setter;
import me.soapiee.merger.Merger;
import me.soapiee.merger.gui.core.Icon;
import me.soapiee.merger.logic.ItemsManager;
import me.soapiee.merger.logic.items.MergableItem;
import me.soapiee.merger.logic.items.Producer;
import me.soapiee.merger.utils.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProducerFactory {

    private final CustomLogger customLogger;
    private final MessageManager messageManager;
    @Setter private FileConfiguration config;

    public ProducerFactory(Merger main) {
        customLogger = main.getCustomLogger();
        messageManager = main.getMessageManager();
    }

    public Producer createProducer(String producerID, Icon icon, Icon upgradeIcon, CommandSender sender, ItemsManager itemsManager) {
        int overHeat = validateOverheatCount(producerID, sender);
        int overHeatDelay = validateOverheatCooldown(producerID, sender);
        int maxUses = validateMaxUses(producerID, sender);
        double upgradeCost = validateUpgradeCost(producerID, sender);
        ProbabilityCollection<MergableItem> producerItems = createItemsList(producerID, sender, itemsManager);
        if (producerItems.isEmpty()) return null;

        Map<String, Object> data = new HashMap<>();
        data.put("overHeatCount", overHeat);
        data.put("overHeatDelay", overHeatDelay);
        data.put("maxUses", maxUses);
        data.put("producerItems", producerItems);
        data.put("upgradeCost", upgradeCost);

        return new Producer(producerID, icon, upgradeIcon, data);
    }

    private String getPath(ConfigPath path, String producerID) {
        return path.getPath().replace("{producer}", producerID);
    }

    private double validateUpgradeCost(String producerID, CommandSender sender) {
        String def = (String) ConfigPath.PRODUCER_UPGRADE_COST.getDefaultValue();
        String path = getPath(ConfigPath.PRODUCER_UPGRADE_COST, producerID);
        String input = config.getString(path, def);
        int cost;

        try {
            cost = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            cost = Integer.parseInt(def);
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDPRODUCERUPGRADE_COST, producerID));
        }

        return cost;
    }

    private int validateOverheatCount(String producerID, CommandSender sender) {
        String def = (String) ConfigPath.PRODUCER_OVERHEAT_COUNT.getDefaultValue();
        String path = getPath(ConfigPath.PRODUCER_OVERHEAT_COUNT, producerID);
        String input = config.getString(path, def);
        int count;

        try {
            count = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            count = Integer.parseInt(def);
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDPRODUCEROVERHEAT_COUNT, producerID));
        }

        return count;
    }

    private int validateOverheatCooldown(String producerID, CommandSender sender) {
        String def = (String) ConfigPath.PRODUCER_OVERHEAT_COOLDOWN.getDefaultValue();
        String path = getPath(ConfigPath.PRODUCER_OVERHEAT_COOLDOWN, producerID);
        String input = config.getString(path, def);
        int cooldown;

        try {
            cooldown = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            cooldown = Integer.parseInt(def);
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDPRODUCEROVERHEAT_COOLDOWN, producerID));
        }

        return cooldown;
    }

    private int validateMaxUses(String producerID, CommandSender sender) {
        String def = (String) ConfigPath.PRODUCER_MAX.getDefaultValue();
        String path = getPath(ConfigPath.PRODUCER_MAX, producerID);
        String input = config.getString(path, def);
        int maxUses;

        try {
            maxUses = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            maxUses = Integer.parseInt(def);
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDPRODUCERMAX, producerID));
        }

        return maxUses;
    }

    private List<String> getProducerItems(String path) {
        return (config.isList(path) ? config.getStringList(path) : new ArrayList<>());
    }

    private ProbabilityCollection<MergableItem> createItemsList(String producerID, CommandSender sender, ItemsManager itemsManager) {
        List<String> rawProducerItems = getProducerItems(getPath(ConfigPath.PRODUCER_ITEMS, producerID));
        ProbabilityCollection<MergableItem> list = new ProbabilityCollection<>();

        for (String rawString : rawProducerItems) {
            String[] split = rawString.split(";");

            MergableItem item = getMergableItem(itemsManager, producerID, split[0], sender);
            if (item == null) continue;

            int probability = getProbability(split[1], producerID, sender);
            if (probability < 0) continue;

            list.add(item, probability);
        }

        if (list.isEmpty())
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDPRODUCERITEMS, producerID));

        return list;
    }

    private MergableItem getMergableItem(ItemsManager itemsManager, String producerID, String itemID, CommandSender sender) {
        MergableItem item = itemsManager.getMergableItem(itemID);

        if (item == null)
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDPRODUCERITEM, producerID, itemID));

        return item;
    }

    private int getProbability(String rawProbability, String producerID, CommandSender sender) {
        int probability;

        try {
            probability = Integer.parseInt(rawProbability);
        } catch (NumberFormatException ex) {
            probability = -1;
            customLogger.logToPlayer(sender, null, messageManager.getWithPlaceholder(Message.INVALIDPRODUCERPROBABILITY, producerID));
        }

        return probability;
    }
}
