package datalayer.dto;

import core.domain.space.Point;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class PlayerState {
    private String name;
    private int health;
    private int maxHealth;
    private int strength;
    private int dexterity;
    private int gold;
    private Point position;
    private WeaponState equippedWeapon;
    private List<ItemState> inventory;
}