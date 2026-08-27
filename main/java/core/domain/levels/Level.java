package core.domain.levels;

import core.domain.Logger;
import core.domain.inventory.Item;
import core.domain.inventory.LocationItem;
import core.domain.inventory.Weapon;
import core.domain.space.Direction;
import core.domain.space.Location;
import core.domain.space.Point;
import core.domain.space.Room;
import core.domain.units.Enemy;
import lombok.Getter;

import java.util.*;


public class Level {
    @Getter
    private final Location location;
    private final List<LocationItem> items;
    @Getter
    private final List<Enemy> enemies;

    public Level(Location location, List<LocationItem> items, List<Enemy> enemies) {
        this.location = location;
        this.items = items;
        this.enemies = enemies;
    }

    public List<LocationItem> getLocationItems() {
        return items;
    }

    public void dropWeapon(Weapon weapon, Point position) {
        Logger logger = Logger.getInstance();
        List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));
        Collections.shuffle(directions);
        for (Direction direction : directions) {
            Point newPosition = position.add(direction);
            if (location.isWalkable(newPosition) && isFreePosition(newPosition)) {
                items.add(new LocationItem(newPosition, weapon));
                logger.addEvent("Weapon %s dropped".formatted(weapon.getDescription()));
                return;
            }
        }
        logger.addEvent("Weapon %s can't be dropped, it disappears".formatted(weapon.getDescription()));
    }

    public void dropLoot(List<Item<?>> lootItems, Point position) {
        List<Point> availablePositions = getAvailablePositionsFromPoint(position);
        for (int i = 0; i < lootItems.size(); i++) {
            if (i > availablePositions.size()) {
                break;
            }
            Point placementPosition = availablePositions.get(i);
            items.add(new LocationItem(placementPosition, lootItems.get(i)));
        }
    }

    private List<Point> getAvailablePositionsFromPoint(Point position) {
        List<Point> availablePositions = new ArrayList<>();
        Room room = location.getRoom(position);
        for (Point point : room.getInsidePoints()) {
            if (isFreePosition(point))
                availablePositions.add(point);
        }
        availablePositions.sort(Comparator.comparingDouble(p -> p.distanceTo(position)));
        return availablePositions;
    }

    private boolean isFreePosition(Point position) {
        return !isEnemyPosition(position) && !isItemPosition(position);
    }

    public boolean isItemPosition(Point position) {
        for (LocationItem locationItem : items) {
            if (locationItem.position().equals(position)) return true;
        }
        return false;
    }

    public boolean isEnemyPosition(Point position) {
        return getEnemyFromPosition(position) != null;
    }

    public Enemy getEnemyFromPosition(Point position) {
        for (Enemy enemy : enemies) {
            if (enemy.getPosition().equals(position)) return enemy;
        }
        return null;
    }
}
