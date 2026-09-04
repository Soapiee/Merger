package me.soapiee.merger.commands.adminCmds;

import lombok.Getter;
import me.soapiee.merger.Merger;
import me.soapiee.merger.data.BukkitExecutor;
import me.soapiee.merger.data.PlayerData;
import me.soapiee.merger.enums.EnergyChangeResult;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TakeEnergySub extends AbstractAdminSub {

    @Getter private final String IDENTIFIER = "take";

    public TakeEnergySub(Merger main) {
        super(main, "merger.admin", 3, 3);
    }

    // /adminmerger take <player> <amount>
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, args, label)) return;

        OfflinePlayer player = getTarget(sender, args[1]);
        if (player == null) return;

        int amount = getValue(sender, args[2]);
        if (amount <= 0) return;

        playerDataManager.getOrLoad(player)
                .thenAcceptAsync(playerData -> takeEnergy(sender, playerData, amount), BukkitExecutor.sync(main))
                .exceptionally(error -> {
                    logError(sender, error, player.getName());
                    return null;
                });
    }

    private void takeEnergy(CommandSender sender, PlayerData playerData, int amount) {
        EnergyChangeResult result = playerData.removeEnergy(amount);

        //TODO:
        switch (result) {
            case ENERGY_REMOVED_SUCCESS:
//                sendMessage(sender, messageManager.getWithPlaceholder(Message.ENERGYREMOVED, playerData.getEnergy()));
    //                sendAdminUpdateMsg(sender, playerData.getPlayer(), Message.PLAYERENERGYREMOVED);
                break;
            case ENERGY_REMOVED_FAIL:
//                sendMessage(sender, messageManager.getWithPlaceholder(Message.ENERGYREMOVEDERROR, amount));
        }
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }
}
