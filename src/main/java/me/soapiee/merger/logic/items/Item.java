package me.soapiee.merger.logic.items;

import lombok.Getter;
import me.soapiee.merger.gui.core.Icon;

public abstract class Item implements ItemInterface {

    @Getter private final String identifier;
    @Getter private final Icon icon;

    public Item(String identifier, Icon icon) {
        this.identifier = identifier;
        this.icon = icon;
    }
}
