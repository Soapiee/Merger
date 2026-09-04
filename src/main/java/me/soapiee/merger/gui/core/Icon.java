package me.soapiee.merger.gui.core;

import lombok.Getter;
import lombok.Setter;
import me.soapiee.merger.enums.ClickAction;
import me.soapiee.merger.enums.PageType;
import org.bukkit.inventory.ItemStack;

import java.util.List;


public class Icon {

    @Getter private final ItemStack itemStack;
    @Getter private final List<String> lore;
    @Getter private final int slot;
    @Getter private final ClickAction clickAction;
    @Getter @Setter private PageType page;

    public Icon(ItemStack itemStack, List<String> lore, int slot, ClickAction clickAction) {
        this.itemStack = itemStack;
        this.lore = lore;
        this.slot = slot;
        this.clickAction = clickAction;
        this.page = PageType.NONE;
    }
}
