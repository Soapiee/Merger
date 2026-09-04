package me.soapiee.merger.utils;

import org.bukkit.command.CommandSender;

public interface FileLoader {

    boolean load(CommandSender sender);

    default boolean reload(CommandSender sender){
        return load(sender);
    }

    boolean read(CommandSender sender);

    void save();
}
