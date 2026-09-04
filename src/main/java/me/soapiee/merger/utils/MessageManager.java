package me.soapiee.merger.utils;

import me.soapiee.merger.Merger;
import me.soapiee.merger.logic.items.MergableItem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;

public class MessageManager {

    private final Merger main;

    private final File file;
    private final YamlConfiguration contents;
    private final String language;

    public MessageManager(Merger main) {
        this.main = main;

        String languageString = validateLanguage();
        if (languageString == null) language = Languages.LANG_EN.toString().toLowerCase();
        else language = languageString;

        file = new File(main.getDataFolder() + File.separator + "language", language + ".yml");
        contents = new YamlConfiguration();

        load(Bukkit.getConsoleSender());

        if (languageString == null) Utils.consoleMsg(get(Message.INVALIDLANGUAGE));
    }

    private String validateLanguage() {
        String configString = main.getConfig().getString("language", "lang_en");

        Languages lang;
        try {
            lang = Languages.valueOf(configString.toUpperCase());
        } catch (IllegalArgumentException error) {
            return null;
        }

        return lang.toString().toLowerCase();
    }

    public boolean reload(CommandSender sender) {
        return load(sender);
    }

    private boolean load(CommandSender sender) {
        if (!file.exists()) main.saveResource("language" + File.separator + language + ".yml", false);

        try {
            contents.load(file);
        } catch (Exception ex) {
            if (sender != null) {
//                customLogger.logToPlayer(sender, ex, get(Message.RELOADERROR));
                ex.printStackTrace();
            }
        }
        return true;
    }

    private void save() {
        try {
            contents.save(file);
        } catch (Exception ex) {
            main.getCustomLogger().logToFile(ex, get(Message.LANGUAGEFIELDERROR));
        }
    }

    private String getPrefix(Message messageEnum) {
        if (messageEnum == Message.UPDATEAVAILABLE
                || messageEnum == Message.PLAYERHELP || messageEnum == Message.ADMINHELP
                || messageEnum == Message.LANGUAGEFIELDERROR || messageEnum == Message.INVALIDLANGUAGE
                || messageEnum == Message.DATABASECONNECTED || messageEnum == Message.DATABASEFAILED
                || messageEnum == Message.FILESYSTEMACTIVATED || messageEnum == Message.FILEFOLDERERROR
                || messageEnum == Message.MAJORDATAERROR || messageEnum == Message.HOOKEDPLACEHOLDERAPI
                || messageEnum == Message.HOOKEDVAULT || messageEnum == Message.HOOKEDVAULTERROR
                || messageEnum == Message.LOGGERFILEERROR || messageEnum == Message.LOGGERLOGSUCCESS
                || messageEnum == Message.LOGGERLOGERROR) return "";

        String path = Message.PREFIX.getPath();
        if (contents.isSet(path)) {
            return contents.getString(path).isEmpty() ? "" : contents.getString(path) + " ";
        } else return "";
    }

    public String get(Message messageEnum) {
        String path = messageEnum.getPath();
        String defaultText = messageEnum.getDefaultText();

        if (contents.isSet(path)) {
            String text = ((contents.isList(path)) ? String.join("\n", contents.getStringList(path)) : contents.getString(path));

            return text.isEmpty() ? null : getPrefix(messageEnum) + text;
        } else {
            if (defaultText.contains("\n")) {
                String[] list;
                list = defaultText.split("\n");
                contents.set(path, list);
            } else {
                contents.set(path, defaultText);
            }
            save();
            return getPrefix(messageEnum) + defaultText;
        }
    }

    public String getWithPlaceholder(Message messageEnum, String string) {
        String message = get(messageEnum);

        if (message.contains("%producer_ID%")) message = message.replace("%producer_ID%", string);
        if (message.contains("%mergepath_ID%")) message = message.replace("%mergepath_ID%", string);
        if (message.contains("%path%")) message = message.replace("%path%", string);
        if (message.contains("%player_name%")) message = message.replace("%player_name%", string);

        return message;
    }

    public String getWithPlaceholder(Message messageEnum, String producerID, String itemID) {
        String message = get(messageEnum);

        if (message.contains("%producer_ID%")) message = message.replace("%producer_ID%", producerID);
        if (message.contains("%item_ID%")) message = message.replace("%item_ID%", itemID);

        return message;
    }

    public String getWithPlaceholder(Message messageEnum, String pathID, ArrayList<MergableItem> mergeItems) {
        String message = get(messageEnum);

        if (message.contains("%mergepath_ID%")) message = message.replace("%mergepath_ID%", pathID);

        StringBuilder builder = new StringBuilder();
        int i = 1;
        for (MergableItem item : mergeItems) {
            builder.append(i).append("=").append(item.getIdentifier()).append(", ");
            i++;
        }

        if (message.contains("%path_items%")) message = message.replace("%path_items%", builder.toString());

        return message;
    }

    public String getWithPlaceholder(Message messageEnum, int integer) {
        String message = get(messageEnum);

        if (message.contains("%countdown%")) message = message.replace("%countdown%", String.valueOf(integer));
        if (message.contains("%round_countdown%"))
            message = message.replace("%round_countdown%", integer + " second" + (integer == 1 ? "" : "s"));

        return message;
    }
}
