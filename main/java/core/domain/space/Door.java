package core.domain.space;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Door {
    private Point position;
    private Direction direction;

    public Door(Room room, Point targetPosition) {
        double difX = targetPosition.getX() - room.getCentre().getX();
        double difY = targetPosition.getY() - room.getCentre().getY();

        double tRight = (difX > 0) ? room.getWidth() / 2.0 / difX : Double.POSITIVE_INFINITY;
        double tLeft = (difX < 0) ? (-1) * room.getWidth() / 2.0 / difX : Double.POSITIVE_INFINITY;
        double tUp = (difY < 0) ? (-1) * room.getHeight() / 2.0 / difY : Double.POSITIVE_INFINITY;
        double tDown = (difY > 0) ? room.getHeight() / 2.0 / difY : Double.POSITIVE_INFINITY;

        double tMin = Math.min(tRight, Math.min(tLeft, Math.min(tUp, tDown)));

        Direction dir = null;
        int x = 0, y = 0;

        if (tMin == tRight) {
            dir = Direction.RIGHT;
            x = room.getPosition().getX() + room.getWidth() - 1;
        } else if (tMin == tLeft) {
            dir = Direction.LEFT;
            x = room.getPosition().getX();
        } else if (tMin == tUp) {
            dir = Direction.UP;
            y = room.getPosition().getY();
        } else if (tMin == tDown) {
            dir = Direction.DOWN;
            y = room.getPosition().getY() + room.getHeight() - 1;
        }
        direction = dir;

        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            y = (int) (room.getCentre().getY() + difY * tMin);
            if (y <= room.getPosition().getY()) {
                y = room.getPosition().getY() + 1;
            } else if (y >= (room.getPosition().getY() + room.getHeight() - 1)) {
                y = room.getPosition().getY() + room.getHeight() - 2;
            }
        } else if (direction == Direction.UP || direction == Direction.DOWN) {
            x = (int) (room.getCentre().getX() + difX * tMin);
            if (x <= room.getPosition().getX()) {
                x = room.getPosition().getX() + 1;
            } else if (x >= (room.getPosition().getX() + room.getWidth() - 1)) {
                x = room.getPosition().getX() + room.getWidth() - 2;
            }
        }
        position = new Point(x, y);
    }

    public boolean checkHorizontal() {
        return direction == Direction.LEFT || direction == Direction.RIGHT;
    }
}
