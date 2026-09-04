package me.soapiee.merger.commands.adminCmds;

import lombok.Getter;
import me.soapiee.merger.Merger;
import me.soapiee.merger.data.BukkitExecutor;
import me.soapiee.merger.data.PlayerData;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class StatsSub extends AbstractAdminSub {

    @Getter private final String IDENTIFIER = "stats";

    public StatsSub(Merger main) {
        super(main, "merger.admin", 2, 2);
    }

    // /adminmerger stats <player>
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, args, label)) return;

        OfflinePlayer player = getTarget(sender, args[1]);
        if (player == null) return;

        playerDataManager.getOrLoad(player)
                .thenAcceptAsync(playerData -> displayStats(sender, playerData), BukkitExecutor.sync(main))
                .exceptionally(error -> {
                    logError(sender, error, player.getName());
                    return null;
                });
    }

    private void displayStats(CommandSender sender, PlayerData playerData) {
        int totalEnergy = playerData.getEnergy();
        ArrayList<String> unlockedProducers =  new ArrayList<>();

        //TODO:
//        sendMessage(sender, messageManager.getWithPlaceholder(Message.PLAYERSTATS, totalEnergy, unlockedProducers));
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }
}
