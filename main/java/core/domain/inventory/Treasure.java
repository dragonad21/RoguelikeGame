package core.domain.inventory;

import core.domain.inventory.types.ItemType;
import core.domain.inventory.types.TreasureSubType;
import lombok.Getter;

@Getter
public class Treasure extends Item<TreasureSubType> {
    private final int cost;

    public Treasure(TreasureSubType subType, int cost) {
        super(ItemType.TREASURE, subType);
        this.cost = cost;
    }

    @Override
    public String getDescription() {
        return subType + ": get " + cost + " gold value";
    }
}
