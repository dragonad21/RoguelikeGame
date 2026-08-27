package datalayer.dto;

import core.domain.inventory.types.WeaponSubType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class WeaponState {
    private WeaponSubType subType;
    private int strengthBonus;
}