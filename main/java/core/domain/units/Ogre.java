package core.domain.units;

import core.domain.space.Direction;
import core.domain.space.Location;
import core.domain.space.Point;
import core.domain.space.Room;
import core.util.Rng;
import datalayer.dto.StatisticsRun;

import java.util.List;

public class Ogre extends Enemy {
    private boolean rest = false;

    public Ogre(int levelNumber) {
        super(9, 2, 9, 5, EnemyType.OGRE, levelNumber);
    }

    @Override
    protected Point followPattern(Location location) {
        List<Direction> directions = new java.util.ArrayList<>(List.of(Direction.values()));
        do {
            Room room = location.getRoom(this.getPosition());
            Direction direction = directions.get(Rng.nextInt(directions.size()));
            Point newPosition = this.getPosition().add(direction).add(direction);
            if (location.isWalkable(newPosition) && room.isInside(newPosition)) {
                return newPosition;
            }
            directions.remove(direction);
        } while (!directions.isEmpty());
        return this.getPosition();
    }

    @Override
    public void attack(Unit other, StatisticsRun currentRun) {
        if (rest) {
            rest = false;
        } else {
            super.attack(other, currentRun);
            rest = true;
        }
    }

    @Override
    public Enemy clone() {
        return new Ogre(getLevelNumber());
    }
}
