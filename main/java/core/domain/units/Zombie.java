package core.domain.units;

public class Zombie extends Enemy {
    public Zombie(int levelNumber) {
        super(7, 2, 4, 5, EnemyType.ZOMBIE, levelNumber);
    }

    @Override
    public Enemy clone() {
        return new Zombie(this.getLevelNumber());
    }
}