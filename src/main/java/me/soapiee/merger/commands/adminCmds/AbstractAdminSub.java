package me.soapiee.merger.commands.adminCmds;

import me.soapiee.merger.Merger;
import me.soapiee.merger.commands.SubCmd;
import me.soapiee.merger.managers.ConfigManager;
import me.soapiee.merger.utils.MessageManager;
import me.soapiee.merger.data.PlayerDataManager;
import me.soapiee.merger.utils.CustomLogger;
import me.soapiee.merger.utils.Message;
import me.soapiee.merger.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class AbstractAdminSub implements SubCmd {

    protected final Merger main;
    protected final MessageManager messageManager;
    protected final CustomLogger customLogger;
    protected final PlayerDataManager playerDataManager;
    protected final ConfigManager configManager;

    protected final String PERMISSION;
    protected final int MIN_ARGS;
    protected final int MAX_ARGS;

    public AbstractAdminSub(Merger main, String PERMISSION, int MIN_ARGS, int MAX_ARGS) {
        this.main = main;
        this.messageManager = main.getMessageManager();
        this.customLogger = main.getCustomLogger();
        this.playerDataManager = main.getDataManager().getPlayerDataManager();
        this.configManager = main.getConfigManager();

        this.PERMISSION = PERMISSION;
        this.MIN_ARGS = MIN_ARGS;
        this.MAX_ARGS = MAX_ARGS;
    }

    public boolean checkRequirements(CommandSender sender, String[] args, String label) {
        if (!checkPermission(sender, PERMISSION)) {
            sendMessage(sender, messageManager.get(Message.NOPERMISSION));
            return false;
        }

        if (!checkArgs(args)) {
            sendMessage(sender, messageManager.getWithPlaceholder(Message.ADMINHELP, label));
            return false;
        }

        return true;
    }

    protected boolean checkPermission(CommandSender sender, String permission){
        if (permission == null) return true;
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        return player.hasPermission(permission);
    }

    private boolean checkArgs(String[] args){
        if (MIN_ARGS == -1 && MAX_ARGS == -1) return true;

        if (args.length < MIN_ARGS) return false;
        return !(args.length > MAX_ARGS);
    }

    protected void sendMessage(CommandSender sender, String message){
        if (message == null) return;

        if (sender instanceof Player) sender.sendMessage(Utils.addColour(message));
        else Utils.consoleMsg(message);
    }

    protected void sendAdminUpdateMsg(CommandSender sender, OfflinePlayer target, String message){
        if (target.isOnline())
            if (sender instanceof ConsoleCommandSender || sender instanceof Player && sender != target.getPlayer())
                sendMessage(target.getPlayer(), message);
    }

    protected void logError(CommandSender sender, Throwable error, String playerName){
        customLogger.logToPlayer(sender, error, Utils.addColour(messageManager.getWithPlaceholder(Message.DATAERROR, playerName)));
    }

    protected OfflinePlayer getTarget(CommandSender sender, String input) {
        OfflinePlayer target = main.getPlayerCache().getOfflinePlayer(input);

        if (target == null) sendMessage(sender, messageManager.getWithPlaceholder(Message.PLAYERNOTFOUND, input));

        return target;
    }

    protected int getValue(CommandSender sender, String input){
        int value;
        try {
            value = Integer.parseInt(input);
        } catch (NumberFormatException error) {
            sendMessage(sender, messageManager.getWithPlaceholder(Message.INVALIDNUMBER, input));
            return -1;
        }

        return value;
    }
}
