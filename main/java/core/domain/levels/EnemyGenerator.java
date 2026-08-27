package core.domain.levels;

import core.domain.units.*;
import core.util.Rng;

import java.util.ArrayList;
import java.util.List;

public class EnemyGenerator {
    private final List<EnemyTemplate> allEnemyTypes = new ArrayList<>();

    private record EnemyTemplate(Enemy enemy, int weight) {
    }

    public EnemyGenerator(int levelNumber) {
        allEnemyTypes.add(new EnemyTemplate(new Zombie(levelNumber), Rng.between(0, 5)));
        allEnemyTypes.add(new EnemyTemplate(new Vampire(levelNumber), Rng.between(0, 1)));
        allEnemyTypes.add(new EnemyTemplate(new Ghost(levelNumber), Rng.between(0, 2)));
        allEnemyTypes.add(new EnemyTemplate(new Ogre(levelNumber), Rng.between(0, 2)));
        allEnemyTypes.add(new EnemyTemplate(new SnakeMage(levelNumber), Rng.between(0, 2)));
        allEnemyTypes.add(new EnemyTemplate(new Mimic(levelNumber), Rng.between(1, 3)));
    }

    public List<Enemy> generate(int levelNumber) {
        int enemiesCount = Rng.between(1 + levelNumber / 7, 2 + levelNumber / 3);

        List<Enemy> enemies = new ArrayList<>();
        for (int i = 0; i < enemiesCount; i++)
            enemies.add(generateEnemy());
        return enemies;
    }

    private Enemy cloneEnemy(Enemy originalEnemy) {
        return originalEnemy.clone();
    }

    private Enemy generateEnemy() {
        int sumWeight = allEnemyTypes.stream().mapToInt(t -> t.weight).sum();
        int randomValue = sumWeight > 0 ? Rng.nextInt(sumWeight) : 0;

        int currentWeight = 0;
        for (EnemyGenerator.EnemyTemplate template : allEnemyTypes) {
            currentWeight += template.weight;
            if (randomValue < currentWeight)
                return cloneEnemy(template.enemy);
        }
        return allEnemyTypes.getFirst().enemy;
    }

}
