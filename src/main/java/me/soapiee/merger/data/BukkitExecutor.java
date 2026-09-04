package me.soapiee.merger.data;

import me.soapiee.merger.Merger;
import org.bukkit.Bukkit;

import java.util.concurrent.Executor;

public final class BukkitExecutor {

    private BukkitExecutor() {}

    public static Executor sync(Merger main) {
        return command -> Bukkit.getScheduler().runTask(main, command);
    }
}
