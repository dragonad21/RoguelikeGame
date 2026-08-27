package core.domain.inventory.types;

import core.domain.units.Player;
import lombok.Getter;

@Getter
public enum StatType {
    STRENGTH("Strength") {
        @Override
        public void apply(Player player, int bonus) {
            player.modifyStrength(bonus);
        }
    },
    DEXTERITY("Dexterity") {
        @Override
        public void apply(Player player, int bonus) {
            player.modifyDexterity(bonus);
        }
    },
    MAX_HEALTH("Max health") {
        @Override
        public void apply(Player player, int bonus) {
            if (bonus > 0) {
                player.modifyMaxHealth(bonus);
                player.modifyHealth(bonus);
            } else {
                player.modifyHealth((player.getHealth() + bonus) < 0 ? 1 : bonus);
                player.modifyMaxHealth((player.getMaxHealth() + bonus) < 0 ? 1 : bonus);
            }
        }
    };

    private final String name;

    StatType(String name) {
        this.name = name;
    }

    public abstract void apply(Player player, int bonus);

}
