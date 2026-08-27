package core.domain.space;

import core.config.WorldConstants;
import core.util.Rng;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class Room {
    private int width;
    private int height;
    private Point position;
    private boolean start = false;

    public Room(Point position, int width, int height) {
        if (width <= 0 || width > WorldConstants.ROOM_MAX_WIDTH || height <= 0 || height > WorldConstants.ROOM_MAX_HEIGHT) {
            throw new IllegalArgumentException("A non-existent area");
        }
        this.position = position;
        this.width = width;
        this.height = height;
    }

    public Point getCentre() {
        return new Point(position.getX() + width / 2, position.getY() + height / 2);
    }

    public void setStart() {
        start = true;
    }

    public boolean isInside(Point point) {
        return point.getX() > position.getX() && point.getX() < position.getX() + width - 1 &&
                point.getY() > position.getY() && point.getY() < position.getY() + height - 1;
    }

    public List<Point> getInsidePoints() {
        List<Point> points = new ArrayList<>();
        for (int Y = position.getY() + 1; Y < position.getY() + height; Y++) {
            for (int X = position.getX() + 1; X < position.getX() + width; X++) {
                points.add(new Point(X, Y));
            }
        }
        return points;
    }

    public boolean isBorder(Point point) {
        return (point.getX() == position.getX() && point.getY() >= position.getY() && point.getY() < position.getY() + height) ||
                (point.getX() == position.getX() + width - 1 && point.getY() >= position.getY() && point.getY() < position.getY() + height) ||
                (point.getY() == position.getY() && point.getX() >= position.getX() && point.getX() < position.getX() + width) ||
                (point.getY() == position.getY() + height - 1 && point.getX() >= position.getX() && point.getX() < position.getX() + width);
    }

    public double distanceFromCentreTo(Point other) {
        Point centre = getCentre();
        int difX = centre.getX() - other.getX();
        int difY = centre.getY() - other.getY();
        return Math.sqrt(difX * difX + difY * difY);
    }

    public Point getRandomPointInRoom() {
        int x = Rng.between(this.getPosition().getX() + 1, this.getPosition().getX() + this.getWidth() - 2);
        int y = Rng.between(this.getPosition().getY() + 1, this.getPosition().getY() + this.getHeight() - 2);
        return new Point(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return width == room.width &&
                height == room.height &&
                start == room.start &&
                Objects.equals(position, room.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, position, start);
    }

}
