package core.domain.inventory;

import core.domain.Game;
import core.domain.inventory.types.ItemType;
import core.domain.inventory.types.SubType;
import core.domain.units.Player;
import lombok.Getter;

@Getter
public abstract class Item<T extends SubType> {
    protected final ItemType type;
    protected final T subType;

    public Item(ItemType type, T subType) {
        this.type = type;
        this.subType = subType;
    }

    public void applyTo(Player player) {
    }

    public void applyTo(Player player, Game game) {
        applyTo(player);
    }

    public abstract String getDescription();
}
