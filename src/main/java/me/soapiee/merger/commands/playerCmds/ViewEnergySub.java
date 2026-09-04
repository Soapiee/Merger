package me.soapiee.merger.commands.playerCmds;

import lombok.Getter;
import me.soapiee.merger.Merger;
import me.soapiee.merger.data.BukkitExecutor;
import me.soapiee.merger.data.PlayerData;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ViewEnergySub extends AbstractPlayerSub {

    @Getter private final String IDENTIFIER = "view";

    public ViewEnergySub(Merger main) {
        super(main, "merger.player.view", 2, 2);
    }

    // /adminmerger view <player>
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, args, label)) return;

        OfflinePlayer player = getTarget(sender, args[1]);
        if (player == null) return;

        playerDataManager.getOrLoad(player)
                .thenAcceptAsync(playerData -> displayEnergy(sender, playerData), BukkitExecutor.sync(main))
                .exceptionally(error -> {
                    logError(sender, error, player.getName());
                    return null;
                });
    }

    private void displayEnergy(CommandSender sender, PlayerData playerData) {
        int totalEnergy = playerData.getEnergy();

        //TODO:
//        sendMessage(sender, messageManager.getWithPlaceholder(Message.PLAYERENERGY, totalEnergy));
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }
}
