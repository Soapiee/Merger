package me.soapiee.merger.commands;

import me.soapiee.merger.Merger;
import me.soapiee.merger.commands.adminCmds.ReloadSub;
import me.soapiee.merger.utils.MessageManager;
import me.soapiee.merger.utils.Message;
import me.soapiee.merger.utils.PlayerCache;
import me.soapiee.merger.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class AdminCmd implements CommandExecutor, TabCompleter {

    private final PlayerCache playerCache;
    private final MessageManager messageManager;

    private final String PERMISSION = "merger.admin";
    private final Map<String, SubCmd> subCommands = new HashMap<>();

    public AdminCmd(Merger main) {
        playerCache = main.getPlayerCache();
        messageManager = main.getMessageManager();

        register(new ReloadSub(main));
    }

    private void register(SubCmd cmd) {
        subCommands.put(cmd.getIDENTIFIER(), cmd);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof CommandBlock) return true;
        if (!hasPermission(sender)) return true;

        if (args.length == 0) {
            sendMessage(sender, messageManager.get(Message.ADMINHELP));
            return true;
        }

        SubCmd cmd = subCommands.get(args[0]);
        if (cmd == null) {
            sendMessage(sender, messageManager.getWithPlaceholder(Message.ADMINHELP, label));
            return true;
        }

        cmd.execute(sender, label, args);
        return true;
    }

    private boolean hasPermission(CommandSender sender) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        if (!player.hasPermission(PERMISSION))
            sendMessage(player, messageManager.get(Message.NOPERMISSION));

        return player.hasPermission(PERMISSION);
    }

    private void sendMessage(CommandSender sender, String message) {
        if (message == null) return;

        if (sender instanceof Player) sender.sendMessage(Utils.addColour(message));
        else Utils.consoleMsg(message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        final List<String> results = new ArrayList<>();

        switch (args.length) {
            case 1:
                results.addAll(Arrays.asList("list", "enable", "disable", "setlevel", "addlevel", "removelevel",
                        "setprogress", "addprogress", "removeprogress", "reset"));

                if (sender instanceof Player && sender.hasPermission("merger.admin")) {
                    results.add("reload");
                }
                break;

            case 2:
                if (args[0].equalsIgnoreCase("reload")) break;

                if (args[0].equalsIgnoreCase("list")) {
                    results.add("worlds");
                    results.add("biomes");
                    break;
                }

                if (args[0].equalsIgnoreCase("enable")) {
                    Bukkit.getWorlds().forEach(world -> results.add(world.getName()));

                    Arrays.stream(Biome.values()).forEach(biome -> results.add(biome.name().toLowerCase()));
                    break;
                }

                playerCache.getOfflinePlayers().forEach(player -> results.add(player.getName()));
                break;

        }
        return results.stream()
                .filter(completion -> completion.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
