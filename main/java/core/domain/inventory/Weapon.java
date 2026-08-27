package core.domain.inventory;

import core.domain.inventory.types.ItemType;
import core.domain.inventory.types.WeaponSubType;
import core.domain.units.Player;
import datalayer.dto.WeaponState;
import lombok.Getter;

@Getter
public class Weapon extends Item<WeaponSubType> {
    private final int strengthBonus;

    public Weapon(WeaponSubType subType, int strengthBonus) {
        super(ItemType.WEAPON, subType);
        this.strengthBonus = strengthBonus;
    }

    public Weapon(WeaponState weaponState) {
        super(ItemType.WEAPON, weaponState.getSubType());
        this.strengthBonus = weaponState.getStrengthBonus();
    }

    @Override
    public String getDescription() {
        return subType.getName() + ": increase the power by +" + strengthBonus;
    }

    @Override
    public void applyTo(Player player) {
        player.setWeaponEquipped(this);
    }
}
