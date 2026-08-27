package core.domain.levels;

import core.domain.space.Location;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class LevelManager {
    private final List<Level> levels;
    private int currentLevelNumber = 0;

    public LevelManager() {
        this.levels = new ArrayList<>();
    }

    public LevelManager(List<Level> levels, int currentLevelNumber) {
        if (levels == null) {
            throw new IllegalArgumentException("Levels cannot be null");
        }
        this.levels = new ArrayList<>(levels);
        this.currentLevelNumber = currentLevelNumber;
    }

    public void addLevel() {
        LevelBuilder levelBuilder = new LevelBuilder();
        // Новый уровень получает номер = levels.size() + 1
        levels.add(levelBuilder.generate(levels.size() + 1));
        if (currentLevelNumber == 0) {
            currentLevelNumber = 1;
        }
    }

    public void goToNextLevel() {
        currentLevelNumber++;

        // Если у нас нет уровня с таким номером — создаём его
        if (currentLevelNumber > levels.size()) {
            LevelBuilder levelBuilder = new LevelBuilder();
            levels.add(levelBuilder.generate(currentLevelNumber));
        }
    }

    public Level getCurrentLevel() {
        if (levels.isEmpty()) {
            return null;
        }

        // Индекс в списке = currentLevelNumber - 1
        int index = currentLevelNumber - 1;

        // Если индекс выходит за границы, но уровни есть - возвращаем последний
        if (index >= levels.size()) {
            return levels.getLast();
        }

        return levels.get(index);
    }

    public Location getCurrentLocation() {
        Level currentLevel = getCurrentLevel();
        if (currentLevel == null) {
            throw new IllegalStateException(
                    "Current level is not available. currentLevelNumber="
                            + currentLevelNumber + ", levelsCount=" + levels.size()
            );
        }
        return currentLevel.getLocation();
    }

    public List<Level> getLevels() {
        return Collections.unmodifiableList(levels);
    }
}