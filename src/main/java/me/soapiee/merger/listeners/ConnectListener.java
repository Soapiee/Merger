package me.soapiee.merger.listeners;

import me.soapiee.merger.Merger;
import me.soapiee.merger.data.BukkitExecutor;
import me.soapiee.merger.data.PlayerData;
import me.soapiee.merger.data.PlayerDataManager;
import me.soapiee.merger.logic.items.Producer;
import me.soapiee.merger.utils.CustomLogger;
import me.soapiee.merger.utils.MessageManager;
import me.soapiee.merger.utils.PlayerCache;
import me.soapiee.merger.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class ConnectListener implements Listener {

    private final Merger main;
    private final PlayerCache playerCache;
    private final PlayerDataManager playerDataManager;
    private final MessageManager messageManager;
    private final CustomLogger customLogger;

    public ConnectListener(Merger main) {
        this.main = main;
        playerCache = main.getPlayerCache();
        playerDataManager = main.getDataManager().getPlayerDataManager();
        messageManager = main.getMessageManager();
        customLogger = main.getCustomLogger();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) playerCache.addOfflinePlayer(player);

        playerDataManager.getOrLoad(player)
                .thenAcceptAsync(data -> setupPlayer(player, data), BukkitExecutor.sync(main))
                .exceptionally(error -> {
                    customLogger.logToFile(error, "Could not create new data for " + player.getName());
                    // TODO: Create Message enum
                    return null;
                });

        Bukkit.getScheduler().runTaskLater(main, () -> {
            if (player.hasPermission("merger.admin.update")) updateNotif(player);
        }, 20);
    }

    private void setupPlayer(Player player, PlayerData playerData) {
        if (!player.hasPlayedBefore()) {
            playerCache.addOfflinePlayer(player);
        }

//        if (main.getConfigManager().isDebugMode()) Utils.consoleMsg("&aPlayer has unlocked " + playerData.getUnlockedProducers().size() + " producers.");
        Utils.consoleMsg("&aPlayer has unlocked " + playerData.getUnlockedProducers().size() + " producers.");
        if (playerData.getUnlockedProducers().isEmpty()) {
            Producer starterProducer = main.getItemsManager().getStarterProducer();
            playerData.unlockProducer(starterProducer);
//            playerData.getGameInventory().addProducerButton(new ProducerButton(main, starterProducer, 49));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        PlayerData playerData = playerDataManager.getPlayerData(uuid);

        if (playerData == null) {
            customLogger.logToFile(new NullPointerException(), "Could not find " + player.getName() + " players data");
            // TODO: Create Message enum
            return;
        }

        cancelButtonTasks(playerData);
        playerData.saveData(true);

        playerDataManager.remove(uuid);
    }

    private void cancelButtonTasks(PlayerData playerData) {
        playerData.getGameInventory().getProducerButtons().forEach((id, button) -> {
            if (button.getOverheatTask() != null) {
                try {
                    button.getOverheatTask().cancel();
                } catch (IllegalStateException ignored) {
                }
            }
        });
    }

    private void updateNotif(Player player) {
//        if (configManager.isUpdateNotif()) main.getUpdateManager().updateAlert(player);
    }
}
