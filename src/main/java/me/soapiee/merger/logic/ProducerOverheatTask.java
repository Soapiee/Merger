package me.soapiee.merger.logic;

import me.soapiee.merger.Merger;
import me.soapiee.merger.gui.buttons.ProducerButton;
import org.bukkit.scheduler.BukkitRunnable;

public class ProducerOverheatTask extends BukkitRunnable {

    private final ProducerButton producerButton;

    public ProducerOverheatTask(Merger main, ProducerButton producerButton, long delay) {
        this.producerButton = producerButton;
        runTaskLater(main, delay);
    }

    @Override public void run() {
        producerButton.resetOverHeating();
        this.cancel();
    }
}
