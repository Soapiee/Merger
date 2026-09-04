package me.soapiee.merger.data;

import me.soapiee.merger.Merger;
import org.bukkit.OfflinePlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

    private final Merger main;
    private final Map<UUID, PlayerData> cachedData = new ConcurrentHashMap<>();
    private final Map<UUID, CompletableFuture<PlayerData>> loading = new ConcurrentHashMap<>();

    public PlayerDataManager(Merger main) {
        this.main = main;
    }

    public CompletableFuture<PlayerData> getOrLoad(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();

        // Loaded
        PlayerData playerData = cachedData.get(uuid);
        if (playerData != null) {
            return CompletableFuture.completedFuture(playerData);
        }

        // Return loading or create new
        return loading.computeIfAbsent(uuid, id -> {
            PlayerData data = new PlayerData(main, player);

            return data.load()
                    .thenApply(loadedData -> {
                        cachedData.put(uuid, loadedData);
                        return loadedData;
                    })
                    .whenComplete((results, exception) -> loading.remove(uuid));
        });
    }

    public PlayerData getPlayerData(UUID uuid) {
        return cachedData.get(uuid);
    }

    public void remove(UUID uuid) {
        cachedData.remove(uuid);
        loading.remove(uuid);
    }

    public void saveAll(boolean async) {
        for (PlayerData playerData : cachedData.values()) playerData.saveData(async);
    }
}
