package me.soapiee.merger.commands.playerCmds;

import lombok.Getter;
import me.soapiee.merger.Merger;
import me.soapiee.merger.utils.Message;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class UpgradeSub extends AbstractPlayerSub {

    @Getter private final String IDENTIFIER = "upgrade";

    public UpgradeSub(Merger main) {
        super(main, null, 1, 1);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        //TODO: Open upgrades gui for player
//        if (!checkRequirements(args, sender, main)) return;
        // updateProgress((Player) sender);
        // sendMessage(player, messageManager.get(Message.GUIOPENED));

        sendMessage(sender, messageManager.getWithPlaceholder(Message.PLAYERHELP, label));
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();

    }
}
