package core.domain.units;

import core.util.Rng;
import datalayer.dto.StatisticsRun;

public class Combat {

    private static final double MIN_HIT_CHANCE = 0.30;
    private static final double MAX_HIT_CHANCE = 0.95;
    private static final double GROWTH_RATE = 0.3;
    private static final double MIDPOINT = 10;

    public boolean makeHit(Unit attacker, Unit defender, StatisticsRun currentRun) {
        if (isHitSuccessful(attacker.getDexterity())) {
            defender.modifyHealth(-attacker.getStrength());
            currentRun.upHits();
            return true;
        }
        currentRun.upMisses();
        return false;
    }

    private boolean isHitSuccessful(int dexterity) {
        double exponent = -GROWTH_RATE * (dexterity - MIDPOINT);
        double logistic = 1.0 / (1.0 + Math.exp(exponent));
        double chance = MIN_HIT_CHANCE + (MAX_HIT_CHANCE - MIN_HIT_CHANCE) * logistic;

        return Rng.nextBoolean(chance);
    }
}

