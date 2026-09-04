package me.soapiee.merger.commands;

import me.soapiee.merger.Merger;
import me.soapiee.merger.commands.playerCmds.GUISub;
import me.soapiee.merger.utils.MessageManager;
import me.soapiee.merger.utils.Message;
import me.soapiee.merger.utils.PlayerCache;
import me.soapiee.merger.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerCmd implements CommandExecutor, TabCompleter {

    private final PlayerCache playerCache;
    private final MessageManager messageManager;

    private final String PERMISSION = "merger.player.command";
    private final Map<String, SubCmd> subCommands = new HashMap<>();

    public PlayerCmd(Merger main) {
        playerCache = main.getPlayerCache();
        messageManager = main.getMessageManager();

        register(new GUISub(main));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!hasPermission(sender)) return true;

        SubCmd cmd = subCommands.get(getSubCmd(args));
        if (cmd == null) {
            sendMessage(sender, messageManager.getWithPlaceholder(Message.PLAYERHELP, label));
            return true;
        }

        cmd.execute(sender, label, args);
        return true;
    }

    private void register(SubCmd cmd) {
        subCommands.put(cmd.getIDENTIFIER(), cmd);
    }

    private String getSubCmd(String[] args) {
        if (args.length == 0) return "gui";

        return args[0].toLowerCase();
    }

    private void sendMessage(CommandSender sender, String message) {
        if (message == null) return;

        if (sender instanceof Player) sender.sendMessage(Utils.addColour(message));
        else Utils.consoleMsg(message);
    }

    private boolean hasPermission(CommandSender sender) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        if (!player.hasPermission(PERMISSION))
            sendMessage(player, messageManager.get(Message.NOPERMISSION));

        return player.hasPermission(PERMISSION);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        final List<String> results = new ArrayList<>();

        switch (args.length) {
            case 1:
                results.add("money");
                results.add("multi");
                results.add("help");
                if (sender instanceof Player && sender.hasPermission("eveconomy.admin")) {
                    results.add("reload");
                }
                break;
            case 2:
                playerCache.getOfflinePlayers().forEach(offlinePlayer -> results.add(offlinePlayer.getName()));
                break;
            case 3:
                results.addAll(Arrays.asList("list", "enable", "disable", "setlevel", "addlevel", "removelevel",
                        "setprogress", "addprogress", "removeprogress", "reset"));
        }
        return results.stream()
                .filter(completion -> completion.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
