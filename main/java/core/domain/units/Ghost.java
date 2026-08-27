package core.domain.units;

import core.domain.space.Location;
import core.domain.space.Point;
import core.util.Rng;
import lombok.Getter;

@Getter
public class Ghost extends Enemy {
    private boolean visible = true;

    public Ghost(int levelNumber) {
        super(3, 7, 2, 3, EnemyType.GHOST, levelNumber);
    }

    @Override
    public boolean move(Point playerPosition, Location location) {
        boolean resultMove = super.move(playerPosition, location);
        if (this.isChasing && !this.visible) {
            this.visible = true;
        }
        return resultMove;
    }

    @Override
    protected Point followPattern(Location location) {
        this.visible = Rng.nextBoolean(0.5);
        return location.getRoom(this.getPosition()).getRandomPointInRoom();
    }

    @Override
    public Enemy clone() {
        Ghost clone = new Ghost(this.getLevelNumber());
        clone.setPosition(this.getPosition());
        clone.setHealth(this.getHealth());
        clone.setMaxHealth(this.getMaxHealth());
        clone.setStrength(this.getStrength());
        clone.setDexterity(this.getDexterity());
        clone.visible = this.visible;
        return clone;
    }
}
