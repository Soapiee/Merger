package me.soapiee.merger.logic.items;

import lombok.Setter;
import me.soapiee.merger.gui.core.Icon;
import me.soapiee.merger.logic.MergePath;

public class MergableItem extends Item {

    @Setter private MergePath path;

    public MergableItem(String identifier, Icon icon) {
        super(identifier, icon);
    }


    public MergableItem next(){
        return path.next(this);
    }

    public MergableItem previous(){
        return path.next(this);
    }

    public boolean isLast(){
        return path.isLast(this);
    }
}
