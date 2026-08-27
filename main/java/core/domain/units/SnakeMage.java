package core.domain.units;

import core.domain.space.Direction;
import core.domain.space.Location;
import core.domain.space.Point;
import core.domain.space.Room;
import core.util.Rng;
import datalayer.dto.StatisticsRun;

import java.util.List;

public class SnakeMage extends Enemy {
    public SnakeMage(int levelNumber) {
        super(4, 8, 3, 8, EnemyType.SNAKE_MAGE, levelNumber);
    }

    @Override
    protected Point followPattern(Location location) {
        List<Direction> directions = new java.util.ArrayList<>(List.of(Direction.values()));
        do {
            Room room = location.getRoom(this.getPosition());
            Direction direction = directions.get(Rng.nextInt(directions.size()));
            Point newPosition = this.getPosition().add(direction).add(getDiagonal(direction));
            if (location.isWalkable(newPosition) && room.isInside(newPosition)) {
                return newPosition;
            }
            directions.remove(direction);
        } while (!directions.isEmpty());
        return this.getPosition();
    }

    private Direction getDiagonal(Direction direction) {
        if (direction == Direction.UP) {
            return Direction.RIGHT;
        } else if (direction == Direction.RIGHT) {
            return Direction.DOWN;
        } else if (direction == Direction.DOWN) {
            return Direction.LEFT;
        } else {
            return Direction.UP;
        }
    }

    @Override
    public void attack(Unit other, StatisticsRun currentRun) {
        Combat combat = new Combat();
        if (combat.makeHit(this, other, currentRun)) {
            if (Rng.nextBoolean(0.5)) {
                Player player = (Player) other;
                player.setSleep(true);
            }
        }
    }

    @Override
    public Enemy clone() {
        return new SnakeMage(this.getLevelNumber());
    }
}
