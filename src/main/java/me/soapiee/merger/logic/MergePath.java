package me.soapiee.merger.logic;

import me.soapiee.merger.logic.items.MergableItem;
import me.soapiee.merger.utils.Utils;

import java.util.ArrayList;

public class MergePath {

    private final ArrayList<MergableItem> path;

    public MergePath(ArrayList<MergableItem> path) {
        this.path = path;

        for (MergableItem item : path) {
            item.setPath(this);
        }
    }

    public MergableItem next(MergableItem current) {
        int index = path.indexOf(current);
        MergableItem item;

        try {
            item = path.get(index + 1);
        } catch (IndexOutOfBoundsException ex){
            item = null;
        }

        return item;
    }

    public MergableItem previous(MergableItem current) {
        int index = path.indexOf(current);
        MergableItem item;

        try {
            item = path.get(index - 1);
        } catch (IndexOutOfBoundsException ex){
            item = null;
        }

        return item;
    }

    public boolean isFirst(MergableItem current) {
        return path.indexOf(current) == 0;
    }

    public boolean isLast(MergableItem current) {
        return path.indexOf(current) == path.size() - 1;
    }
}
