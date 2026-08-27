package core.domain.units;

import core.domain.space.Point;
import datalayer.dto.StatisticsRun;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Unit {
    private Point position;
    private int health;
    private int maxHealth;
    private int dexterity;
    private int strength;
    private boolean alive = true;

    public Unit(int health, int dexterity, int strength) {
        position = new Point(0, 0);
        this.health = health;
        this.maxHealth = health;
        this.dexterity = dexterity;
        this.strength = strength;
    }

    public Point getPosition() {
        return new Point(position);
    }

    public void modifyHealth(int delta) {
        health += delta;
        if (health <= 0) alive = false;
    }

    public void modifyMaxHealth(int delta) {
        maxHealth += delta;
    }

    public void modifyStrength(int delta) {
        if (strength + delta < 0)
            throw new IllegalArgumentException("The strength cannot be less than zero");

        strength += delta;
    }

    public void modifyDexterity(int delta) {
        if (dexterity + delta < 0)
            throw new IllegalArgumentException("The dexterity cannot be less than zero");

        dexterity += delta;
    }

    public boolean isDead() {
        return !alive;
    }

    public void attack(Unit other, StatisticsRun currentRun) {
        Combat combat = new Combat();
        combat.makeHit(this, other, currentRun);
    }
}