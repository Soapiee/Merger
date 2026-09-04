package me.soapiee.merger.commands.playerCmds;

import lombok.Getter;
import me.soapiee.merger.Merger;
import me.soapiee.merger.data.PlayerData;
import me.soapiee.merger.gui.pages.game.GamePage;
import me.soapiee.merger.utils.Message;
import me.soapiee.merger.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GUISub extends AbstractPlayerSub {

    @Getter private final String IDENTIFIER = "gui";

    public GUISub(Merger main) {
        super(main, null, 0, 0);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sendMessage(sender, Utils.addColour(messageManager.get(Message.MUSTBEPLAYERERROR)));
            return;
        }

        if (!checkRequirements(sender, args, label)) return;

        Player player = (Player) sender;

        PlayerData playerData = playerDataManager.getPlayerData(player.getUniqueId());
        if (playerData == null) return; //TODO: Add error message

        main.getGuiManager().openGUI(new GamePage(main, playerData), player);

        //TODO:
        sendMessage(player, "Opened GUI");
//        sendMessage(player, messageManager.get(Message.GUIOPENED));
//        sendMessage(sender, messageManager.getWithPlaceholder(Message.PLAYERHELP, label));
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();

    }
}
