package me.soapiee.merger.logic;

import lombok.Getter;
import me.soapiee.merger.logic.items.MergableItem;
import me.soapiee.merger.logic.items.Producer;
import me.soapiee.merger.logic.items.StaticItem;
import me.soapiee.merger.utils.Utils;

import java.util.HashSet;

public class ItemsManager {

    @Getter private final HashSet<Producer> producers = new HashSet<>();
    private final HashSet<MergableItem> mergableItems = new HashSet<>();
    private final HashSet<StaticItem> staticItems = new HashSet<>();
    @Getter private Producer starterProducer;

    public ItemsManager() {
    }

    public int getProducerCount() {
        return producers.size();
    }

    public boolean isProducer(String rawIdentifier) {
        for (Producer producer : producers) {
            if (producer.getIdentifier().equalsIgnoreCase(rawIdentifier)) {
                return true;
            }
        }

        return false;
    }

    public boolean isMergableItem(String rawIdentifier) {
        for (MergableItem item : mergableItems) {
            if (item.getIdentifier().equalsIgnoreCase(rawIdentifier)) {
                return true;
            }
        }

        return false;
    }

    public MergableItem getMergableItem(String identifier) {
        for (MergableItem item : mergableItems) {
            if (item.getIdentifier().equalsIgnoreCase(identifier)) {
                return item;
            }
        }

        return null;
    }

    public boolean isStaticItem(String rawIdentifier) {
        for (StaticItem item : staticItems) {
            if (item.getIdentifier().equalsIgnoreCase(rawIdentifier)) {
                return true;
            }
        }

        return false;
    }

    public StaticItem getStaticItem(String identifier) {
        for (StaticItem item : staticItems) {
            if (item.getIdentifier().equalsIgnoreCase(identifier)) {
                return item;
            }
        }

        return null;
    }

    public Producer getProducer(String identifier) {
        for (Producer producer : producers) {
            if (producer.getIdentifier().equalsIgnoreCase(identifier)) {
                return producer;
            }
        }

        return null;
    }

    public void registerProducer(Producer producer) {
        producers.add(producer);
    }

    public void registerMergableItem(MergableItem mergabletem) {
        mergableItems.add(mergabletem);

    }

    public void registerStaticItem(StaticItem staticItem) {
        staticItems.add(staticItem);
    }

    public void setStarterProducer(String string) {
        Producer producer = getProducer(string);
        if (producer == null) producer = producers.iterator().next();

        starterProducer = producer;

        //TODO: Remove msg
        Utils.consoleMsg("&cStarter producer set to " + starterProducer.getIdentifier());
    }

}
