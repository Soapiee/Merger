package me.soapiee.merger.utils;

public enum Message {

    //                    --->    GENERAL MESSAGES    <---
    PREFIX("prefix", "&5&l[Merger]&r"), // Replace to "" if you want no prefix
    MUSTBEPLAYERERROR("must_be_player", "&cYou must be a player to use this command"),
    NOPERMISSION("no_permission", "&cYou do not have permission to use this command"),
    PLAYERNOTFOUND("player_not_found", "&cPlayer not found"),
    DATAERROR("data_error", "&c%player_name%'s data could not be loaded or changed at this time"),
    DATAERRORPLAYER("data_error_player", "&cThere was an error loading/saving your data. Please re-log."
            + "\nIf this error persists, contact support immediately"),
    LANGUAGEFIELDERROR("language_field_error", "&cCould not add new fields to the language file"),
    INVALIDLANGUAGE("invalid_language", "&cAn invalid language has been configured. Defaulting to \"lang_en\""),
    DATABASECONNECTED("database_connected", "&2Database connected."),
    DATABASEFAILED("database_connected_error", "&cDatabase could not connect. Switching to file storage"),
    FILESYSTEMACTIVATED("file_storage_enabled", "&2File Storage enabled."),
    FILEFOLDERERROR("file_storage_error", "&cData folder could not be created"),
    MAJORDATAERROR("data_error_disabling_plugin", "There was an error creating/retrieving player data. Disabling plugin.."),
    HOOKEDPLACEHOLDERAPI("hooked_placeholderapi", "&aHooked into PlaceholderAPI"),
    HOOKEDVAULT("hooked_vault", "&aHooked into Vault"),
    HOOKEDVAULTERROR("hooked_vault_error", "&cError hooking into Vault"),
    LOGGERFILEERROR("logger_file_error", "&cThe logger.log file could not be created"),
    LOGGERLOGSUCCESS("logger_log_success", "&cAn error was added to the logger.log file"),
    LOGGERLOGERROR("logger_log_error", "&cA new error log failed to be saved"),

    //                    --->    ADMIN MESSAGES    <---
    UPDATEAVAILABLE("update_available", "&aThere is an update available for Merger"),
    RELOADSUCCESS("reload_success", "&aSuccessfully reloaded Merger"),
    RELOADINPROGRESS("reload_inprogress", "&eReloading configuration..."),
    RELOADERROR("reload_error", "&cError reloading Merger"),
    ADMINHELP("admin_help", "#01d54a--------- Admin Help ---------"
            + "\n#01d54aKey: < > = Optional | [ ] = Required"
            + "\n#01d54a/%cmd_label% reload &7- Reloads the plugin"
            + "\n#01d54a/%cmd_label% list worlds &7- Lists all the enabled worlds"
            + "\n#01d54a/%cmd_label% reset [player] <biome> &7- Clears all of the players data or just for a specified biome"),
    INVALIDMATERIAL("invalid_material", "&cInvalid material for %path%"),
    INVALIDPAGE("invalid_gui_page", "&cInvalid gui page for %path%"),
    INVALIDCLICKACTION("invalid_click_action", "&cInvalid click action for %path%"),
    INVALIDPRODUCERITEMS("invalid_producer_list", "&cThe producer %producer_ID% has no items to produce"),
    INVALIDPRODUCERITEM("invalid_producer_item", "&cThe %producer_ID% producer has an invalid item (%item_ID%)"),
    INVALIDPRODUCEROVERHEAT_COUNT("invalid_producer_overheat", "&cThe %producer_ID% producer has an invalid overheat count value"),
    INVALIDPRODUCEROVERHEAT_COOLDOWN("invalid_producer_overheat", "&cThe %producer_ID% producer has an invalid overheat cooldown value"),
    INVALIDPRODUCERMAX("invalid_producer_maxuses", "&cThe %producer_ID% producer has an invalid max uses value"),
    INVALIDPRODUCERUPGRADE_COST("invalid_producer_maxuses", "&cThe %producer_ID% producer has an invalid upgrade cost value"),
    INVALIDPRODUCERPROBABILITY("invalid_producer_probability", "&cThe %producer_ID% producer has an invalid probability on one of its producer items"),
    MERGEPATHCREATED("successfully_created_mergepath", "&aA Merge Path was successfully created &6(%mergepath_ID%) &2- &6%path_items%"),

    //                    --->    PLAYER MESSAGES    <---
    PLAYERHELP("player_help", "#01d54a--------- Merger Help ---------"
            + "\n#01d54aKey: < > = Optional | [ ] = Required"
            + "\n#01d54a/%cmd_label% info <page> &7- Shows your biome levels"
            + "\n#01d54a/%cmd_label% info [player] <page> &7- Shows biome levels of another player"
            + "\n#01d54a/%cmd_label% info [biome] <player> &7- Shows more in-depth details about a particular biome"
            + "\n#01d54a/%cmd_label% reward [biome] [level] &7- Toggles a reward (Used for the multi-use rewards)"),
    INVALIDNUMBER("player_invalid_number", "&c%input% is not a valid number");

    public final String path;
    private final String defaultText;

    Message(String path, String defaultText) {
        this.path = path;
        this.defaultText = defaultText;
    }

    public String getPath() {
        return path;
    }

    public String getDefaultText() {
        return defaultText;
    }
}
