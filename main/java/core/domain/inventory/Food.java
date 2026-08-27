package core.domain.inventory;

import core.domain.Game;
import core.domain.inventory.types.FoodSubType;
import core.domain.inventory.types.ItemType;
import core.domain.units.Player;
import lombok.Getter;

@Getter
public class Food extends Item<FoodSubType> {
    private final int healthRestore;

    public Food(FoodSubType subType, int healthRestore) {
        super(ItemType.FOOD, subType);
        this.healthRestore = healthRestore;
    }

    @Override
    public String getDescription() {
        return subType.getName() + ": restore " + healthRestore;
    }

    @Override
    public void applyTo(Player player, Game game) {
        if (player.getHealth() + healthRestore > player.getMaxHealth()) {
            player.modifyHealth(player.getMaxHealth() - player.getHealth());
        } else {
            player.modifyHealth(healthRestore);
        }
        game.getCurrentRun().upFood();
    }
}