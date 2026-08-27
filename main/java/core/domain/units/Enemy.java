package core.domain.units;

import core.domain.inventory.Item;
import core.domain.levels.ItemGenerator;
import core.domain.space.Direction;
import core.domain.space.Location;
import core.domain.space.Point;
import core.domain.space.Room;
import core.domain.space.path.PathFinder;
import core.util.Rng;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class Enemy extends Unit {
    private final EnemyType type;
    private final int aggression;
    private final int levelNumber;
    protected boolean isChasing = false;

    public Enemy(int health, int dexterity, int strength, int aggression, EnemyType type, int levelNumber) {
        super(health, dexterity, strength);
        this.type = type;
        this.aggression = aggression;
        this.levelNumber = levelNumber;
        scaleStats();
    }

    public abstract Enemy clone();

    public boolean move(Point playerPosition, Location location) {
        checkAndSetChasing(playerPosition, location);
        Point newPosition = null;
        if (isChasing) {
            newPosition = followPlayer(playerPosition, location);
            if (newPosition.equals(playerPosition)) {
                return false;
            }
        } else {
            newPosition = followPattern(location);
        }
        this.setPosition(newPosition);
        return true;
    }

    public List<Item<?>> generateLoot() {
        ItemGenerator itemGenerator = new ItemGenerator();
        return itemGenerator.generateByCount((getMaxHealth() + getStrength() + getDexterity()) / 16);
    }

    protected Point followPlayer(Point playerPosition, Location location) {
        PathFinder pathFinder = new PathFinder(location);
        Direction direction = pathFinder.getDirection(this.getPosition(), playerPosition);
        return this.getPosition().add(direction);
    }

    protected Point followPattern(Location location) {
        List<Direction> directions = new java.util.ArrayList<>(List.of(Direction.values()));
        do {
            Room room = location.getRoom(this.getPosition());
            Direction direction = directions.get(Rng.nextInt(directions.size()));
            Point newPosition = this.getPosition().add(direction);
            if (location.isWalkable(newPosition) && room.isInside(newPosition)) {
                return newPosition;
            }
            directions.remove(direction);
        } while (!directions.isEmpty());
        return this.getPosition();
    }

    protected void checkAndSetChasing(Point playerPosition, Location location) {
        if (!isChasing) {
            isChasing = isPlayerInRange(playerPosition, location);
        }
    }

    private boolean isPlayerInRange(Point playerPosition, Location location) {
        PathFinder pathFinder = new PathFinder(location);
        return (pathFinder.getHCost(this.getPosition(), playerPosition) <= aggression) &&
                (location.getRoom(this.getPosition()).equals(location.getRoom(playerPosition)));
    }

    private void scaleStats() {
        float scaleFactor = (levelNumber - 1) * 0.1f;
        modifyMaxHealth(Math.round(getMaxHealth() * scaleFactor));
        modifyHealth(Math.round(getHealth() * scaleFactor));
        modifyStrength(Math.round(getStrength() * scaleFactor));
        modifyDexterity(Math.round(getDexterity() * scaleFactor));
    }
}
