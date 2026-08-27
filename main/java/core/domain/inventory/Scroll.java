package core.domain.inventory;

import core.domain.Game;
import core.domain.inventory.types.ItemType;
import core.domain.inventory.types.ScrollSubType;
import core.domain.units.Player;
import lombok.Getter;

@Getter
public class Scroll extends Item<ScrollSubType> {
    private final int bonusValue;

    public Scroll(ScrollSubType subType, int bonusValue) {
        super(ItemType.SCROLL, subType);
        this.bonusValue = bonusValue;
    }

    @Override
    public String getDescription() {
        return "Scroll: increase " + subType.getName() + " +" + bonusValue + " by several steps";
    }

    @Override
    public void applyTo(Player player, Game game) {
        player.changeStat(subType.getStat(), bonusValue);
        game.getCurrentRun().upScrolls();
    }
}