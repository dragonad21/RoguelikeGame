package core.domain.levels;

import core.domain.inventory.Item;
import core.domain.inventory.LocationItem;
import core.domain.space.Location;
import core.domain.space.LocationBuilder;
import core.domain.space.Point;
import core.domain.units.Enemy;
import core.util.Rng;

import java.util.ArrayList;
import java.util.List;

public class LevelBuilder {

    public LevelBuilder() {
    }

    public Level generate(int levelNumber) {
        LocationBuilder localBuilder = new LocationBuilder();
        Location location = localBuilder.generate();

        ItemGenerator itemGenerator = new ItemGenerator();
        List<Item<?>> items = itemGenerator.generateByLevel(levelNumber);
        List<LocationItem> itemsWithPositions = generateItemPositions(items, location);

        EnemyGenerator enemyGenerator = new EnemyGenerator(levelNumber);
        List<Enemy> enemies = enemyGenerator.generate(levelNumber);
        generateEnemyPositions(location, enemies);

        return new Level(location, itemsWithPositions, enemies);
    }

    private List<LocationItem> generateItemPositions(List<Item<?>> items, Location location) {
        List<LocationItem> locationItems = new ArrayList<>();
        for (Item<?> item : items) {
            locationItems.add(new LocationItem(getRandomFreePosition(location), item));
        }
        return locationItems;
    }

    private void generateEnemyPositions(Location location, List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            int roomNumber = -1;
            while (roomNumber == -1) {
                int number = Rng.nextInt(location.getRooms().size());
                if (!location.getRooms().get(number).isStart()) {
                    roomNumber = number;
                }
            }
            enemy.setPosition(getRandomFreePosition(location));
        }
    }

    private Point getRandomFreePosition(Location location) {
        int roomNumber = Rng.nextInt(location.getRooms().size());

        Point roomPosition = location.getRooms().get(roomNumber).getPosition();
        int roomHeight = location.getRooms().get(roomNumber).getHeight();
        int roomWidth = location.getRooms().get(roomNumber).getWidth();

        Point position;
        do {
            int posX = Rng.between(roomPosition.getX() + 1, roomPosition.getX() + roomWidth - 2);
            int posY = Rng.between(roomPosition.getY() + 1, roomPosition.getY() + roomHeight - 2);
            position = new Point(posX, posY);
        } while (position.equals(location.getExitPosition()));

        return position;
    }
}
