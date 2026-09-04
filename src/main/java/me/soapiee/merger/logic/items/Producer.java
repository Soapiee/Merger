package me.soapiee.merger.logic.items;

import lombok.Getter;
import me.soapiee.merger.gui.core.Icon;
import me.soapiee.merger.utils.ProbabilityCollection;

import java.util.Map;

public class Producer extends Item {

    @Getter private final ProbabilityCollection<MergableItem> producedItems;
    @Getter private final int overHeatCount;
    @Getter private final int overHeatDelay;
    @Getter private final int maxUses;
    @Getter private final double upgradeCost;
    @Getter private final Icon upgradeIcon;

    public Producer(String identifier, Icon icon, Icon upgradeIcon, Map<String, Object> data) {
        super(identifier, icon);
        this.upgradeIcon = upgradeIcon;
        this.overHeatCount = (int) data.get("overHeatCount");
        this.overHeatDelay = (int) data.get("overHeatDelay");
        this.maxUses = (int) data.get("maxUses");
        this.producedItems = (ProbabilityCollection<MergableItem>) data.get("producerItems");
        this.upgradeCost = (double) data.get("upgradeCost");;
    }

    public MergableItem produce() {
        return producedItems.get();
    }
}
