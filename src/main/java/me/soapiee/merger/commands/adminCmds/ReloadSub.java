package me.soapiee.merger.commands.adminCmds;

import lombok.Getter;
import me.soapiee.merger.Merger;
import me.soapiee.merger.utils.Message;
import me.soapiee.merger.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ReloadSub extends AbstractAdminSub {

    @Getter private final String IDENTIFIER = "reload";

    public ReloadSub(Merger main) {
        super(main, "merger.admin.reload", 1, 1);
    }

    // /adminmerger reload
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
//        if (sender instanceof ConsoleCommandSender) {
//            sendMessage(sender, Utils.addColour(messageManager.get(Message.MUSTBEPLAYERERROR)));
//            return;
//        }

        if (!checkRequirements(sender, args, label)) return;

        reload(sender);
    }

    private void reload(CommandSender sender){
        sendMessage(sender, messageManager.get(Message.RELOADINPROGRESS));
        String reloadOutcome = messageManager.get(Message.RELOADSUCCESS);

        boolean errors = false;
        main.getConfigManager().reload(sender);
        if (!messageManager.reload(sender)) errors = true;

        if (errors) reloadOutcome = messageManager.get(Message.RELOADERROR);

        if (sender instanceof Player) {
            if (reloadOutcome != null)
                Utils.consoleMsg(ChatColor.GOLD + sender.getName() + " " + reloadOutcome.replace("[BM] ", ""));
        }

        sendMessage(sender, reloadOutcome);
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }
}
