package me.soapiee.merger.utils;

import me.soapiee.merger.Merger;
import org.bukkit.NamespacedKey;

public class Keys {

    public static final NamespacedKey MERGEITEMKEY = new NamespacedKey(Merger.getPlugin(Merger.class), "MergeItem");
    public static final NamespacedKey PRODUCERKEY = new NamespacedKey(Merger.getPlugin(Merger.class), "ProducerItem");
    public static final NamespacedKey STANDARDBUTTONKEY = new NamespacedKey(Merger.getPlugin(Merger.class), "StandardButton");
    public static final NamespacedKey UPGRADEKEY = new NamespacedKey(Merger.getPlugin(Merger.class), "UpgradeProducer");
}
