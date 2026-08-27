package core.domain.units;

import core.util.Rng;
import datalayer.dto.StatisticsRun;
import lombok.Getter;

@Getter
public class Vampire extends Enemy {
    private boolean firstHit = true;

    public Vampire(int levelNumber) {
        super(8, 5, 3, 8, EnemyType.VAMPIRE, levelNumber);
    }

    public void resetFirstHit() {
        this.firstHit = false;
    }

    @Override
    public void attack(Unit other, StatisticsRun currentRun) {
        Combat combat = new Combat();
        if (combat.makeHit(this, other, currentRun)) {
            other.modifyMaxHealth(-Rng.nextInt(3));
        }
    }

    @Override
    public Enemy clone() {
        return new Vampire(this.getLevelNumber());
    }
}