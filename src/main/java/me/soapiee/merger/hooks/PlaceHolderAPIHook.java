package me.soapiee.merger.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.soapiee.merger.Merger;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceHolderAPIHook extends PlaceholderExpansion {

    private final Merger main;

    public PlaceHolderAPIHook(Merger main) {
        this.main = main;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "merger";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Soapiee";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String identifier) {
        if (offlinePlayer != null && offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();

            if (identifier.equalsIgnoreCase("energy")) {
                return "example1";
            }
            if (identifier.equalsIgnoreCase("coins")) {
                return main.getMessageManager() == null ? "false" : "true";
            }
        }
        return null;
    }
}
