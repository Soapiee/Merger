package me.soapiee.merger.utils;

import lombok.Getter;

public enum ConfigPath {

    // Config File
    DATABASE_ENABLED("database.enabled", false),
    DEBUG_MODE("debug_mode", false),
    PLUGIN_NOTIF("settings.update_notification", true),
    LANGUAGE("settings.language", "lang_en"),
    ENERGYREGEN("settings.energy_regen", 1),
    STARTERPRODUCER("settings.starter_producer", "starter_producer"),
    COINSMAX("settings.max_coins", 10000),
    COINSHOOK("settings.coins_hook_name", null),
    ITEM_PATHS("item_paths", null),

    // Database
    DATABASE_HOST("database.host", "database.host"),
    DATABASE_PORT("database.port", "database.port"),
    DATABASE_NAME("database.database", "database.database"),
    DATABASE_USERNAME("database.username", "database.username"),
    DATABASE_PASSWORD("database.password", "database.password"),

    // Producer File,
    PRODUCER_LIST("producers", "producers"),
    PRODUCER_ICON("producers.{producer}.item", null),
    PRODUCER_UPGRADE_ICON("producers.{producer}.upgrade_icon", null),
    PRODUCER_OVERHEAT_COUNT("producers.{producer}.settings.overheat_count", "20"),
    PRODUCER_OVERHEAT_COOLDOWN("producers.{producer}.settings.overheat_cooldown", "60"),
    PRODUCER_MAX("producers.{producer}.settings.max_uses", "-1"),
    PRODUCER_UPGRADE_COST("producers.{producer}.settings.upgrade_cost", "100"),
    PRODUCER_ITEMS("producers.{producer}.produces", null),

    // Items File,
    ITEM_LIST("items", "items"),
    ITEM_ICON("items.{item}", null),

    // Player data
    ENERGY("Energy", 0),
    COINS("Coins", 0.0),
//    ITEMS("Items", null),
    GAMEINV("Game_Inv_Items", null),
    STORAGEINV("Storage_Inv_Items", null),
    UNLOCKED_PRODUCERS("Unlocked_Producers", null),
    ORDERS("Orders", null),

    // Game Page
    GAME_TITLE("gui.game_page.title", "Test GUI"),
    GAME_ICONS("gui.game_page.icons", null),

    // Upgrades Page
    UPGRADES_TITLE("gui.upgrades_page.title", "Upgrades GUI"),
    UPGRADES_SIZE("gui.upgrades_page.size", 54),
    UPGRADES_ICONS("gui.upgrades_page.icons", null),
    UPGRADES_MAX("settings.max_upgrades_per_page", 10),

    // Orders Page
    ORDERS_TITLE("gui.orders_page.title", "#1baecf&lOrders Page"),
    ORDERS_SIZE("gui.orders_page.size", 54),
    ORDERS_MAX_PLAYER("settings.max_orders_per_player", 10),
    ORDERS_MAX_PAGE("settings.max_orders_per_page", 10),
    ORDERS_ICONS("gui.orders_page.icons", null);

    @Getter private final String path;
    @Getter private final Object defaultValue;

    ConfigPath(String path, Object defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }
}
