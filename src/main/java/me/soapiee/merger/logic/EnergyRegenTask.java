package me.soapiee.merger.logic;

import me.soapiee.merger.data.PlayerData;
import org.bukkit.OfflinePlayer;

public class EnergyRegenTask implements Runnable {

    private final OfflinePlayer player;
    private final PlayerData playerData;
    private final int energyIncrement;
    private final long delay;

    public EnergyRegenTask(OfflinePlayer player, PlayerData playerData, int energyIncrement, int delay) {
        this.player = player;
        this.playerData = playerData;
        this.energyIncrement = energyIncrement;
        this.delay = delay * 20L;
    }

    @Override public void run() {

        //TODO:
        // adds the energy to the player
        // Updates the GUI icon
    }
}
