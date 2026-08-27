package core.domain;

import core.domain.inventory.*;
import core.domain.inventory.types.*;
import core.domain.levels.LevelManager;
import core.domain.levels.Level;
import core.domain.space.*;
import core.domain.units.*;
import datalayer.dto.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
public class GameBuilder {

    public Game generate() {
        LevelManager levelManager = new LevelManager();
        levelManager.addLevel();

        Pack pack = new Pack();
        Player player = new Player(10, 6, 5, pack, "Name");

        player.setWeaponDropped(weapon -> {
            levelManager.getCurrentLevel().dropWeapon(weapon, player.getPosition());
        });

        return new Game(levelManager, player, true);
    }

    public static Game restoreGame(GameSnapshot snapshot) {
        Player player = restorePlayer(snapshot.getPlayer());
        LevelManager levelManager = restoreLevelManager(snapshot);
        if (levelManager.getCurrentLevel() != null) {
            player.setWeaponDropped(weapon -> {
                levelManager.getCurrentLevel().dropWeapon(weapon, player.getPosition());
            });
        }

        return new Game(levelManager, player, false);
    }

    private static LevelManager restoreLevelManager(GameSnapshot snapshot) {
        List<Level> levels = new ArrayList<>();
        levels.add(restoreLevel(snapshot.getLevel()));
        return new LevelManager(levels, snapshot.getLevelNumber());
    }

    private static Level restoreLevel(LevelState levelState) {

        LocationState locationState = levelState.getLocation();

        List<Room> rooms = new ArrayList<>();
        for (RoomState roomState : locationState.getRooms()) {
            Room room = new Room(
                    roomState.getPosition(),
                    roomState.getWidth(),
                    roomState.getHeight()
            );
            if (roomState.isStart()) {
                room.setStart();
            }
            rooms.add(room);
        }

        List<Tunnel> tunnels = new ArrayList<>();
        for (TunnelState tunnelState : locationState.getTunnels()) {
            Tunnel tunnel = restoreTunnel(tunnelState, rooms);
            tunnels.add(tunnel);
        }

        Location location = new Location(rooms, tunnels, locationState.getExit());

        List<LocationItem> items = new ArrayList<>();
        if (levelState.getItems() != null) {
            for (ItemState itemState : levelState.getItems()) {
                Item<?> item = restoreItem(itemState);
                items.add(new LocationItem(itemState.getPosition(), item));
            }
        }

        List<Enemy> enemies = new ArrayList<>();
        if (levelState.getEnemies() != null) {
            for (EnemyState enemyState : levelState.getEnemies()) {
                Enemy enemy = restoreEnemy(enemyState);
                enemies.add(enemy);
            }
        }

        return new Level(location, items, enemies);
    }

    private static Tunnel restoreTunnel(TunnelState state, List<Room> rooms) {
        int startIndex = findRoomIndexByPosition(state.getStartRoomPosition(), rooms);
        int endIndex = findRoomIndexByPosition(state.getEndRoomPosition(), rooms);
        if (startIndex == -1 || endIndex == -1) {
            return new Tunnel(rooms, 0, rooms.size() > 1 ? 1 : 0);
        }

        return new Tunnel(rooms, startIndex, endIndex);
    }

    private static int findRoomIndexByPosition(Point position, List<Room> rooms) {
        if (position == null) return -1;
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (room.getPosition().equals(position)) {
                return i;
            }
        }
        return -1;
    }

    private static Player restorePlayer(PlayerState playerState) {
        if (playerState == null) {
            return new Player(10, 6, 5, new Pack(), "undefined");
        }

        Pack pack = new Pack();
        if (playerState.getInventory() != null) {
            for (ItemState itemState : playerState.getInventory()) {
                Item<?> item = restoreItem(itemState);
                // Пропускаем сокровища при восстановлении
                if (!(item instanceof Treasure)) {
                    pack.addItem(item);
                }
            }
        }

        Player player = createPlayerFromState(playerState, pack);

        player.modifyMaxHealth(playerState.getMaxHealth() - player.getMaxHealth());

        player.setGold(playerState.getGold());
        player.setPosition(playerState.getPosition());

        if (playerState.getEquippedWeapon() != null) {
            Weapon weapon = new Weapon(playerState.getEquippedWeapon());
            player.setWeaponEquipped(weapon);
        }

        return player;
    }

    private static Player createPlayerFromState(PlayerState state, Pack pack) {
        return new Player(
                state.getHealth(),
                state.getDexterity(),
                state.getStrength(),
                pack,
                state.getName()
        );
    }

    private static Enemy restoreEnemy(EnemyState state) {
        EnemyType type = EnemyType.valueOf(state.getType());
        Enemy enemy = switch (type) {
            case ZOMBIE -> new Zombie(state.getLevelNumber());
            case VAMPIRE -> new Vampire(state.getLevelNumber());
            case GHOST -> new Ghost(state.getLevelNumber());
            case OGRE -> new Ogre(state.getLevelNumber());
            case SNAKE_MAGE -> new SnakeMage(state.getLevelNumber());
            case MIMIC -> new Mimic(state.getLevelNumber());
        };

        enemy.setPosition(state.getPosition());
        enemy.modifyMaxHealth(state.getMaxHealth() - enemy.getMaxHealth());
        enemy.modifyHealth(state.getHealth() - enemy.getHealth());
        enemy.modifyStrength(state.getStrength() - enemy.getStrength());
        enemy.modifyDexterity(state.getDexterity() - enemy.getDexterity());

        return enemy;
    }

    private static Item<?> restoreItem(ItemState state) {
        ItemType type = ItemType.valueOf(state.getType());
        return switch (type) {
            case FOOD -> new Food(resolveSubType(FoodSubType.class, state.getSubType()), state.getValue());
            case TREASURE -> new Treasure(resolveSubType(TreasureSubType.class, state.getSubType()), state.getValue());
            case WEAPON -> new Weapon(resolveSubType(WeaponSubType.class, state.getSubType()), state.getValue());
            case ELIXIR -> new Elixir(resolveSubType(ElixirSubType.class, state.getSubType()), state.getValue());
            case SCROLL -> new Scroll(resolveSubType(ScrollSubType.class, state.getSubType()), state.getValue());
        };
    }

    private static <E extends Enum<E> & SubType> E resolveSubType(Class<E> enumClass, String savedValue) {
        if (savedValue == null) {
            throw new IllegalArgumentException("Missing subtype for " + enumClass.getSimpleName());
        }
        for (E enumValue : enumClass.getEnumConstants()) {
            if (enumValue.name().equals(savedValue) || Objects.equals(enumValue.getName(), savedValue)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException(
                "Unknown subtype '" + savedValue + "' for " + enumClass.getSimpleName()
        );
    }
}