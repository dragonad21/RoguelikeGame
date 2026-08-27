package core.domain.units;


import core.domain.Logger;
import core.domain.space.Location;
import core.domain.space.Point;
import core.util.Rng;
import datalayer.dto.StatisticsRun;
import lombok.Getter;

@Getter
public class Mimic extends Enemy {
    private static final char[] FAKE_SYMBOLS = {'$', '!', '?', ')'};
    private final char fakeSymbol;
    private boolean revealed = false;

    public Mimic(int levelNumber) {
        super(12, 10, 2, 8, EnemyType.MIMIC, levelNumber);
        this.fakeSymbol = FAKE_SYMBOLS[Rng.nextInt(FAKE_SYMBOLS.length)];
    }

    @Override
    public boolean move(Point playerPosition, Location location) {
        if (!revealed) {
            return true;
        }
        return super.move(playerPosition, location);
    }

    @Override
    public void attack(Unit other, StatisticsRun currentRun) {
        if (!revealed) {
            reveal();
        }
        super.attack(other, currentRun);
    }

    public void reveal() {
        if (!revealed) {
            revealed = true;
            Logger.getInstance().addEvent("Mimic revealed itself!");
        }
    }

    @Override
    public Enemy clone() {
        Mimic clone = new Mimic(this.getLevelNumber());
        clone.setPosition(this.getPosition());
        clone.setHealth(this.getHealth());
        clone.setMaxHealth(this.getMaxHealth());
        clone.setStrength(this.getStrength());
        clone.setDexterity(this.getDexterity());
        clone.revealed = this.revealed;
        return clone;
    }

}
