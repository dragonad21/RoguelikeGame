package core.domain.units;

import core.domain.Game;
import core.domain.Logger;
import core.domain.inventory.*;
import core.domain.inventory.types.StatType;
import datalayer.dto.StatisticsRun;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
@Setter
public class Player extends Unit {
    private final String name;
    private final Pack pack;
    private int gold;
    private Weapon equippedWeapon;
    private Consumer<Weapon> onWeaponDropped;
    private final List<Elixir> elixirs = new ArrayList<>();
    private boolean sleep = false;

    public Player(int health, int dexterity, int strength, Pack pack, String name) {
        super(health, dexterity, strength);
        this.name = name;
        if (pack == null) {
            throw new IllegalArgumentException("Pack cannot be null");
        }
        this.pack = pack;
    }

    public boolean fillPack(Item<?> item) {
        if (item instanceof Treasure treasure) {
            this.gold += treasure.getCost();
            Logger.getInstance().addEvent("You found " + treasure.getCost() + " gold!");
            return true;
        }
        return pack.addItem(item);
    }

    public void useItem(int slot, Game game) {
        // 0 - снять экипированное оружие на пол
        if (slot == 0) {
            Weapon oldWeapon = setWeaponUnequipped();
            if (oldWeapon != null) {
                onWeaponDropped.accept(oldWeapon);
                Logger.getInstance().addEvent("Weapon dropped: " + oldWeapon.getDescription());
            } else {
                Logger.getInstance().addEvent("No weapon equipped");
            }
            return;
        }

        try {
            Item<?> item = pack.getItem(slot);

            if (item instanceof Weapon weapon) {
                if (equippedWeapon != null && equippedWeapon.equals(weapon)) {
                    Weapon oldWeapon = setWeaponUnequipped();
                    onWeaponDropped.accept(oldWeapon);
                    Logger.getInstance().addEvent("Weapon dropped: " + oldWeapon.getDescription());
                } else {
                    Weapon oldWeapon = setWeaponUnequipped();
                    if (oldWeapon != null) {
                        onWeaponDropped.accept(oldWeapon);
                        Logger.getInstance().addEvent("Weapon dropped: " + oldWeapon.getDescription());
                    }
                    setWeaponEquipped(weapon);
                    pack.removeItem(weapon);
                    Logger.getInstance().addEvent("Weapon equipped: " + weapon.getDescription());
                }
            } else {
                item.applyTo(this, game);
                pack.removeItem(item);
            }
        } catch (IndexOutOfBoundsException e) {
            Logger.getInstance().addEvent("There is no such thing in the pack: " + slot);
        } catch (IllegalArgumentException e) {
            Logger.getInstance().addEvent("There is no such slot in the pack: " + slot);
        }
    }

    public void setWeaponEquipped(Weapon weapon) {
        Weapon oldWeapon = this.equippedWeapon;
        if (oldWeapon != null) {
            modifyStrength(-oldWeapon.getStrengthBonus());
        }
        this.equippedWeapon = weapon;
        if (weapon != null) {
            modifyStrength(weapon.getStrengthBonus());
        }
    }

    public Weapon setWeaponUnequipped() {
        Weapon oldWeapon = this.equippedWeapon;
        if (oldWeapon != null) {
            modifyStrength(-oldWeapon.getStrengthBonus());
            this.equippedWeapon = null;
        }
        return oldWeapon;
    }

    public void setWeaponDropped(Consumer<Weapon> callback) {
        this.onWeaponDropped = callback;
    }

    public void changeStat(StatType statType, int value) {
        statType.apply(this, value);
    }

    public void drinkElixir(Elixir elixir) {
        elixirs.add(elixir);
        changeStat(elixir.getStat(), elixir.getBonusValue());
    }

    public void resetElixirs() {
        for (Elixir elixir : elixirs) {
            changeStat(elixir.getStat(), -elixir.getBonusValue());
        }
        elixirs.clear();
    }

    @Override
    public void attack(Unit other, StatisticsRun currentRun) {
        if (other instanceof Vampire vampire) {
            if (vampire.isFirstHit()) {
                vampire.resetFirstHit();
                return;
            }
        }
        super.attack(other, currentRun);
    }
}