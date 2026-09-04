package me.soapiee.merger.data;

import lombok.Getter;
import me.soapiee.merger.gui.buttons.ItemButton;
import me.soapiee.merger.gui.buttons.ProducerButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameInventory {

    @Getter private final Map<String, ItemButton> itemButtons = new ConcurrentHashMap<>();
    @Getter private final Map<String, ProducerButton> producerButtons = new ConcurrentHashMap<>();

    public GameInventory() {

    }

    public ArrayList<String> getButtonsAsList() {
        ArrayList<String> list = new ArrayList<>();

        itemButtons.forEach((buttonID, button) -> {
            list.add(getItemButtonsAsString(button));
        });

        producerButtons.forEach((buttonID, button) -> {
            list.add(getProducerButtonsAsString(button));
        });

        return list;
    }

    private String getItemButtonsAsString(ItemButton button) {
        return button.getSlot() + ";" + button.getItem().getIdentifier();
    }

    private String getProducerButtonsAsString(ProducerButton button) {
        return button.getSlot()
                + ";" + button.getProducer().getIdentifier()
                + ";" + button.getMaxUses()
                + ";" + parseOverHeatExpiry(button.getOverHeatExpiry())
                + ";" + button.getOverHeatCount();
    }

//    public ArrayList<String> getItemButtonsAsList() {
//        ArrayList<String> list = new ArrayList<>();
//
//        itemButtons.forEach((buttonID, button) -> {
//            String string = button.getSlot() + ";" + button.getItem().getIdentifier();
//            list.add(string);
//        });
//
//        return list;
//    }

    private String parseOverHeatExpiry(LocalDateTime overHeatExpiry) {
        if (overHeatExpiry == null) return "null";

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
        return overHeatExpiry.format(dtf);
    }

    public void addItemButton(ItemButton button) {
        itemButtons.put(button.getID(), button);
    }

    public void removeItemButton(String buttonID) {
        itemButtons.remove(buttonID);
    }

    public void addProducerButton(ProducerButton button) {
        producerButtons.put(button.getID(), button);
    }

    public void removeProducerButton(String buttonID) {
        producerButtons.remove(buttonID);
    }

    public boolean hasSpace(){
        return !getEmptySlots().isEmpty();
    }

    public int getEmptySlot(){
        Set<Integer> emptySlots = getEmptySlots();

        for (int i = 9; i < 54; i++){
            if (emptySlots.contains(i)) return i;
        }

        return -1;
    }

    private Set<Integer> getEmptySlots(){
//        Set<Integer> occupiedSlots = Stream.of(9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 ,20, 21, 22, 23, 24, 25, 26, 27, 28,
//        29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53).collect(Collectors.toSet());

        Set<Integer> occupiedSlots = IntStream.rangeClosed(9, 53)
                .boxed()
                .collect(Collectors.toSet());

        for (ItemButton button : itemButtons.values()){
            occupiedSlots.remove(button.getSlot());
        }

        for (ProducerButton button : producerButtons.values()){
            occupiedSlots.remove(button.getSlot());
        }

        return occupiedSlots;
    }
}
