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

public class GiveEnergySub extends AbstractAdminSub {

    @Getter private final String IDENTIFIER = "give";

    public GiveEnergySub(Merger main) {
        super(main, "merger.admin", 3, 3);
    }

    // /adminmerger give <player> <amount>
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!checkRequirements(sender, args, label)) return;

        OfflinePlayer player = getTarget(sender, args[1]);
        if (player == null) return;

        int amount = getValue(sender, args[2]);
        if (amount <= 0) return;

        playerDataManager.getOrLoad(player)
                .thenAcceptAsync(playerData -> addEnergy(sender, playerData, amount), BukkitExecutor.sync(main))
                .exceptionally(error -> {
                    logError(sender, error, player.getName());
                    return null;
                });
    }

    private void addEnergy(CommandSender sender, PlayerData playerData, int amount) {
        EnergyChangeResult result = playerData.addEnergy(amount);

        //TODO:
        switch (result) {
            case ENERGY_ADDED_SUCCESS:
//                sendMessage(sender, messageManager.getWithPlaceholder(Message.ENERGYADDED, playerData.getEnergy()));
//                sendAdminUpdateMsg(sender, playerData.getPlayer(), Message.PLAYERENERGYADDED);
                break;
            case ENERGY_ADDED_FAIL:
//                sendMessage(sender, messageManager.getWithPlaceholder(Message.ENERGYADDEDERROR, amount));
        }
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return new ArrayList<>();
    }
}
