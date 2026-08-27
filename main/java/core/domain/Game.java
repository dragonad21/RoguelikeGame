package core.domain;

import core.config.WorldConstants;
import core.domain.inventory.*;
import core.domain.levels.Level;
import core.domain.levels.LevelManager;
import core.domain.space.*;
import core.domain.units.Enemy;
import core.domain.units.Mimic;
import core.domain.units.Player;
import core.util.Rng;
import datalayer.dto.*;
import datalayer.serialization.SaveService;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Game {
    private final LevelManager levelManager;
    private final Player player;
    private final List<ModelListener> positionListeners = new ArrayList<>();
    private final List<GameListener> gameListeners = new ArrayList<>();
    private Visibility visibility;
    private boolean fogVisible = true;
    private final StatisticsRun currentRun;

    public Game(LevelManager levelManager, Player player, boolean isNewGame) {
        this.levelManager = levelManager;
        this.player = player;
        if (isNewGame) {
            initPlayerPosition();
        }
        initVisibility();
        this.currentRun = new StatisticsRun();
    }

    public GameSnapshot toSnapshot() {
        GameSnapshot snapshot = new GameSnapshot();

        snapshot.setPlayer(playerToState());
        snapshot.setLevel(levelToState());
        snapshot.setStatistics(new StatisticsRecord(currentRun));
        snapshot.setLevelNumber(levelManager.getCurrentLevelNumber());
        snapshot.setPlayerName(player.getName());

        return snapshot;
    }

    private PlayerState playerToState() {
        PlayerState state = new PlayerState();
        state.setName(player.getName());
        state.setHealth(player.getHealth());
        state.setMaxHealth(player.getMaxHealth());
        state.setStrength(player.getStrength());
        state.setDexterity(player.getDexterity());
        state.setGold(player.getGold());
        state.setPosition(player.getPosition());

        if (player.getEquippedWeapon() != null) {
            Weapon weapon = player.getEquippedWeapon();
            WeaponState weaponState = new WeaponState();
            weaponState.setSubType(weapon.getSubType());
            weaponState.setStrengthBonus(weapon.getStrengthBonus());
            state.setEquippedWeapon(weaponState);
        }

        List<ItemState> inventory = new ArrayList<>();
        for (Item<?> item : player.getPack().getAllItems()) {
            if (!(item instanceof Treasure)) {
                inventory.add(itemToState(item, null));
            }
        }
        state.setInventory(inventory);

        return state;
    }

    private LevelState levelToState() {
        LevelState state = new LevelState();
        Level level = levelManager.getCurrentLevel();

        state.setLevelNumber(levelManager.getCurrentLevelNumber());

        Location loc = level.getLocation();
        LocationState locationState = new LocationState();
        locationState.setRooms(roomsToState(loc.getRooms()));
        locationState.setTunnels(tunnelsToState(loc.getTunnels(), loc.getRooms()));
        locationState.setExit(loc.getExitPosition());
        state.setLocation(locationState);

        List<EnemyState> enemies = new ArrayList<>();
        for (Enemy enemy : level.getEnemies()) {
            enemies.add(enemyToState(enemy));
        }
        state.setEnemies(enemies);

        List<ItemState> items = new ArrayList<>();
        for (LocationItem locItem : level.getLocationItems()) {
            items.add(itemToState(locItem.item(), locItem.position()));
        }
        state.setItems(items);

        return state;
    }

    private List<RoomState> roomsToState(List<Room> rooms) {
        List<RoomState> roomStates = new ArrayList<>();
        for (Room room : rooms) {
            RoomState state = new RoomState();
            state.setWidth(room.getWidth());
            state.setHeight(room.getHeight());
            state.setPosition(room.getPosition());
            state.setStart(room.isStart());
            state.setCentre(room.getCentre());
            state.setRandomPointInRoom(room.getRandomPointInRoom());
            roomStates.add(state);
        }
        return roomStates;
    }

    private List<TunnelState> tunnelsToState(List<Tunnel> tunnels, List<Room> rooms) {
        List<TunnelState> tunnelStates = new ArrayList<>();
        for (Tunnel tunnel : tunnels) {
            TunnelState state = new TunnelState();

            Room startRoom = getRoomForDoor(tunnel.getStartDoor(), rooms);
            Room endRoom = getRoomForDoor(tunnel.getEndDoor(), rooms);

            state.setStartRoomPosition(startRoom != null ? startRoom.getPosition() : null);
            state.setEndRoomPosition(endRoom != null ? endRoom.getPosition() : null);

            state.setStartPosition(tunnel.getStartPosition());
            state.setEndPosition(tunnel.getEndPosition());
            state.setStartDoor(tunnel.getStartDoor());
            state.setEndDoor(tunnel.getEndDoor());
            state.setPoints(tunnel.points());
            tunnelStates.add(state);
        }
        return tunnelStates;
    }

    private Room getRoomForDoor(Door door, List<Room> rooms) {
        for (Room room : rooms) {
            if (room.isInside(door.getPosition()) || room.isBorder(door.getPosition())) {
                return room;
            }
        }
        return null;
    }


    private ItemState itemToState(Item<?> item, Point position) {
        ItemState state = new ItemState();
        state.setType(item.getType().name());
        state.setSubType(item.getSubType().getName());
        state.setPosition(position);

        int value = 0;
        switch (item) {
            case Weapon weapon -> value = weapon.getStrengthBonus();
            case Food food -> value = food.getHealthRestore();
            case Treasure treasure -> value = treasure.getCost();
            case Elixir elixir -> value = elixir.getBonusValue();
            case Scroll scroll -> value = scroll.getBonusValue();
            default -> {
            }
        }
        state.setValue(value);

        return state;
    }

    private EnemyState enemyToState(Enemy enemy) {
        EnemyState state = new EnemyState();
        state.setType(enemy.getType().name());
        state.setHealth(enemy.getHealth());
        state.setMaxHealth(enemy.getMaxHealth());
        state.setStrength(enemy.getStrength());
        state.setDexterity(enemy.getDexterity());
        state.setPosition(enemy.getPosition());
        state.setLevelNumber(enemy.getLevelNumber());
        return state;
    }

    public void switchFog() {
        fogVisible = !fogVisible;
        updateVisibility();
        notifyModelListeners();
    }

    public void initVisibility() {
        this.visibility = new Visibility(WorldConstants.LEVEL_WIDTH, WorldConstants.LEVEL_HEIGHT);
        updateVisibility();
    }

    public void updateVisibility() {
        visibility.clearVisible();

        if (!fogVisible) {
            for (int x = 0; x < WorldConstants.LEVEL_WIDTH; x++) {
                for (int y = 0; y < WorldConstants.LEVEL_HEIGHT; y++) {
                    visibility.setVisible(x, y);
                }
            }
            return;
        }

        Point position = player.getPosition();
        Location location = levelManager.getCurrentLocation();

        RayCasting(position, location);
        Bresenham(position, location);
    }

    private void Bresenham(Point pos, Location loc) {
        int r = 8, w = WorldConstants.LEVEL_WIDTH, h = WorldConstants.LEVEL_HEIGHT;

        for (int y = -r; y <= r; y++) {
            for (int x = -r; x <= r; x++) {
                int px = pos.getX() + x, py = pos.getY() + y;
                if (px < 0 || px >= w || py < 0 || py >= h) continue;
                if (x * x + y * y > r * r || visibility.isVisible(px, py)) continue;
                if (hasLineOfSight(loc, pos.getX(), pos.getY(), px, py)) {
                    visibility.setVisible(px, py);
                }
            }
        }
    }

    private boolean hasLineOfSight(Location loc, int x0, int y0, int x1, int y1) {
        int dx = Math.abs(x1 - x0), dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1, sy = y0 < y1 ? 1 : -1, err = dx - dy;

        while (true) {
            if (x0 == x1 && y0 == y1) return true;
            if (!loc.isWalkable(new Point(x0, y0))) return false;

            // Защита от бесконечного цикла
            if (Math.abs(x0) > WorldConstants.LEVEL_WIDTH * 2 || Math.abs(y0) > WorldConstants.LEVEL_HEIGHT * 2) {
                return false;
            }

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    private void RayCasting(Point position, Location location) {
        int width = WorldConstants.LEVEL_WIDTH;
        int height = WorldConstants.LEVEL_HEIGHT;

        for (int degrees = 0; degrees < 360; degrees += 2) {
            double radians = Math.toRadians(degrees);

            for (int step = 0; step < WorldConstants.VISIBILITY_RADIUS; step++) {
                int x = position.getX() + (int) Math.round(Math.cos(radians) * step);
                int y = position.getY() + (int) Math.round(Math.sin(radians) * step);

                if (x < 0 || x >= width || y < 0 || y >= height) break;

                visibility.setVisible(x, y);

                if (!location.isWalkable(new Point(x, y))) {
                    break;
                }
            }
        }
    }

    public void initPlayerPosition() {
        Room startRoom = levelManager.getCurrentLocation().getStartRoom();
        int x = Rng.between(startRoom.getPosition().getX() + 1, startRoom.getPosition().getX() + startRoom.getWidth() - 2);
        int y = Rng.between(startRoom.getPosition().getY() + 1, startRoom.getPosition().getY() + startRoom.getHeight() - 2);
        player.setPosition(new Point(x, y));
    }

    public void movePlayer(Direction direction) {
        if (player.isSleep()) {
            player.setSleep(false);
            return;
        }

        Point newPosition = player.getPosition().add(direction);

        Enemy enemy = levelManager.getCurrentLevel().getEnemyFromPosition(newPosition);
        if (enemy instanceof Mimic mimic && !mimic.isRevealed()) {
            mimic.reveal();

            updateVisibility();
            moveEnemies();
            notifyModelListeners();
            return;
        }

        if (levelManager.getCurrentLevel().getLocation().isWalkable(newPosition) &&
                !levelManager.getCurrentLevel().isEnemyPosition(newPosition)) {
            player.setPosition(newPosition);
            pickUpItem();
            currentRun.upSteps();
        } else if (levelManager.getCurrentLevel().isEnemyPosition(newPosition)) {
            enemy = levelManager.getCurrentLevel().getEnemyFromPosition(newPosition);
            player.attack(enemy, currentRun);
            if (enemy.isDead()) {
                levelManager.getCurrentLevel().dropLoot(enemy.generateLoot(), enemy.getPosition());
                levelManager.getCurrentLevel().getEnemies().remove(enemy);
                Logger logger = Logger.getInstance();
                logger.addEvent("You killed %s".formatted(enemy.getType().getName()));
                currentRun.upKills();
            }
        }
        updateVisibility();
        moveEnemies();
        notifyModelListeners();

        if (player.isDead()) {
            notifyGameOver();
        }
    }

    public void moveEnemies() {
        for (Enemy enemy : levelManager.getCurrentLevel().getEnemies()) {
            if (!enemy.move(player.getPosition(), levelManager.getCurrentLocation()))
                enemy.attack(player, currentRun);
        }
    }

    public void goToNextLevel() {
        System.out.println("DEBUG: goToNextLevel() called, levels.size()=" + levelManager.getLevels().size() + ", LEVELS_LIMIT=" + WorldConstants.LEVELS_LIMIT);
        if (levelManager.getLevels().size() < WorldConstants.LEVELS_LIMIT) {
            levelManager.addLevel();
            levelManager.goToNextLevel();
            player.resetElixirs();
            initPlayerPosition();
            initVisibility();
            notifyModelListeners();
            currentRun.upLevel();
            SaveService.saveGame(this);
            System.out.println("DEBUG: Level advanced to " + levelManager.getCurrentLevelNumber());
        } else {
            System.out.println("DEBUG: Notify Game Over (WIN)");
            notifyGameOver();
        }
    }

    public void addGameListener(GameListener listener) {
        gameListeners.add(listener);
    }

    private void notifyGameOver() {
        System.out.println("DEBUG: notifyGameOver() called, currentLevel=" + levelManager.getCurrentLevelNumber());
        currentRun.setTreasures(player.getGold());
        SaveService.deleteSave();
        for (GameListener listener : gameListeners) {
            listener.onGameOver();
        }
    }

    public void addModelListener(ModelListener listener) {
        positionListeners.add(listener);
    }

    private void notifyModelListeners() {
        for (ModelListener listener : positionListeners) {
            listener.onModelChanged();
        }
    }

    private void pickUpItem() {
        List<LocationItem> items = levelManager.getCurrentLevel().getLocationItems();
        for (LocationItem locationItem : items) {
            if (player.getPosition().equals(locationItem.position())) {
                Logger logger = Logger.getInstance();
                if (player.fillPack(locationItem.item())) {
                    logger.addEvent("You found %s".formatted(locationItem.item().getDescription()));
                    items.remove(locationItem);
                    break;
                } else {
                    logger.addEvent("Not enough space to get %s".formatted(locationItem.item().getDescription()));
                }
            }
        }
    }
}