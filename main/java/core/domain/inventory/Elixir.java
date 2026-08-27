package core.domain.inventory;

import core.domain.Game;
import core.domain.inventory.types.ElixirSubType;
import core.domain.inventory.types.ItemType;
import core.domain.inventory.types.StatType;
import core.domain.units.Player;
import lombok.Getter;

@Getter
public class Elixir extends Item<ElixirSubType> {
    private final int bonusValue;

    public Elixir(ElixirSubType subType, int bonusValue) {
        super(ItemType.ELIXIR, subType);
        this.bonusValue = bonusValue;
    }

    public StatType getStat() {
        return subType.getStat();
    }

    @Override
    public String getDescription() {
        return "Elixir: increase " + subType.getName() + " +" + bonusValue;
    }

    @Override
    public void applyTo(Player player, Game game) {
        player.drinkElixir(this);
        game.getCurrentRun().upElixirs();
    }
}