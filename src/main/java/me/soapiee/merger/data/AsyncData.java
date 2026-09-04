package me.soapiee.merger.data;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class AsyncData {

    @Getter private final int energy;
    @Getter private final double coins;
//    @Getter private final List<String> itemButtons;
    @Getter private final List<String> gameItems;
    @Getter private final List<String> storageItems;
    @Getter private final List<String> unlockedProducers;
    @Getter private final List<String> orders;

//    public AsyncData(int energy, double coins, List<String> rawItems, List<String> rawProducers, List<String> rawOrders) {
//        this.energy = energy;
//        this.coins = coins;
//        this.itemButtons = rawItems;
//        this.unlockedProducers = rawProducers;
//        this.orders = rawOrders;
//    }

    public AsyncData(int energy, double coins, List<String> gameItems, List<String> storageItems, List<String> unlockedProducers, List<String> rawOrders) {
        this.energy = energy;
        this.coins = coins;
        this.gameItems = gameItems;
        this.storageItems = storageItems;
        this.unlockedProducers = unlockedProducers;
        this.orders = rawOrders;
    }

    public AsyncData() {
        this(0, 0,  new ArrayList<>(), new ArrayList<>(),  new ArrayList<>(),  new ArrayList<>());
    }


}
