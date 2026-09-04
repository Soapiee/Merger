package me.soapiee.merger.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

//    public static final String VERSION = getVersion();
//
//    public static int getMinorVersion() {
//        String[] parts = VERSION.split("_");
//
//        if (parts[0].equalsIgnoreCase("1")) return parts.length == 3 ? Integer.parseInt(parts[2]) : 0;
//        return Integer.parseInt(parts[1]);
//    }
//
//    public static int getMajorVersion() {
//        String[] parts = VERSION.split("_");
//
//        if (parts[0].equalsIgnoreCase("1")) return Integer.parseInt(parts[1]);
//        return Integer.parseInt(parts[0]);
//    }
//
//    public static String getVersion() {
//        return Bukkit.getBukkitVersion().split("-")[0].replace(".", "_");
//    }

    public static void consoleMsg(String message) {
        String prefix = "[" + Bukkit.getServer().getPluginManager().getPlugin("Merger").getDescription().getPrefix() + "]";
        Bukkit.getConsoleSender().sendMessage(addColour(prefix + " " + message));
    }

    public static void debugMsg(String playerName, String message) {
        consoleMsg(org.bukkit.ChatColor.YELLOW + "[DEBUG] " + (playerName.isEmpty() ? "" : "@" + playerName ) + " " + message);
    }

    public static String addColour(String message) {
        Matcher matcher = Pattern.compile("#([A-Fa-f0-9]{6})").matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String color = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : color.toCharArray()) {
                replacement.append('§').append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
}
