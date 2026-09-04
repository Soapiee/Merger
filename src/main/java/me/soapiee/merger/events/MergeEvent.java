package me.soapiee.merger.events;

import lombok.Getter;
import me.soapiee.merger.data.PlayerData;
import me.soapiee.merger.logic.items.MergableItem;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MergeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    @Getter private final String handItem;
    @Getter private final String slotItem;
    @Getter private final MergableItem nextItem;
    @Getter private final PlayerData playerData;
    @Getter private final int slot;

    public MergeEvent(String handItem, String slotItem, PlayerData playerData, int slot, @NotNull MergableItem nextItem) {
        this.handItem = handItem;
        this.slotItem = slotItem;
        this.nextItem = nextItem;
        this.playerData = playerData;
        this.slot = slot;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
