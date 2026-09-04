package me.soapiee.merger.utils;

import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerCache {

    @Getter private final Set<OfflinePlayer> offlinePlayers;

    public PlayerCache(OfflinePlayer[] offlinePlayers) {
        this.offlinePlayers = new HashSet<>();
        this.offlinePlayers.addAll(Arrays.asList(offlinePlayers));
    }

    public void addOfflinePlayer(OfflinePlayer offlinePlayer) {
        offlinePlayers.add(offlinePlayer);
    }

    public OfflinePlayer getOfflinePlayer(String name) {

        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if (offlinePlayer.getName().equalsIgnoreCase(name)) {
                return offlinePlayer;
            }
        }
        return null;
    }

    public OfflinePlayer getOfflinePlayer(UUID uuid) {

        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if (offlinePlayer.getUniqueId() == uuid) {
                return offlinePlayer;
            }
        }
        return null;
    }

    public boolean contains(Player player) {
        return offlinePlayers.contains(player);
    }
}
