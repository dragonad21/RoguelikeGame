package core.domain.space;

import core.util.Rng;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Tunnel {
    private Point startPosition;
    private Point endPosition;
    private Door startDoor;
    private Door endDoor;
    private List<Point> way;
    private List<Room> rooms;

    public Tunnel(List<Room> rooms, int numberStartRoom, int numberEndRoom) {
        this.rooms = rooms;
        this.startDoor = new Door(rooms.get(numberStartRoom), rooms.get(numberEndRoom).getCentre());
        this.startPosition = new Point(
                startDoor.getPosition().getX() + startDoor.getDirection().getX(),
                startDoor.getPosition().getY() + startDoor.getDirection().getY()
        );

        this.endDoor = new Door(rooms.get(numberEndRoom), rooms.get(numberStartRoom).getCentre());
        this.endPosition = new Point(
                endDoor.getPosition().getX() + endDoor.getDirection().getX(),
                endDoor.getPosition().getY() + endDoor.getDirection().getY()
        );

        this.way = new ArrayList<>();
        createTunnel();
    }

    public List<Point> points() {
        return way;
    }

    private void createTunnel() {
        Point currentPoint = new Point(startPosition);
        Direction lastDirection = null;
        int stepsInSameDirection = 0;

        while (!currentPoint.equals(endPosition)) {
            wayAddPoint(new Point(currentPoint));

            Direction direction;

            if (lastDirection != null && stepsInSameDirection < 3 && Rng.nextInt(100) < 30) {
                direction = lastDirection;
                stepsInSameDirection++;
            } else {
                direction = chooseDirection(currentPoint, endPosition);
                lastDirection = direction;
                stepsInSameDirection = 1;
            }

            Point nextPoint = new Point(
                    currentPoint.getX() + direction.getX(),
                    currentPoint.getY() + direction.getY()
            );

            if (isPossibleTunnelPoint(nextPoint)) {
                currentPoint = nextPoint;
            } else {
                direction = findSafeDirection(currentPoint);
                currentPoint.setX(currentPoint.getX() + direction.getX());
                currentPoint.setY(currentPoint.getY() + direction.getY());
                lastDirection = direction;
                stepsInSameDirection = 1;
            }
        }

        wayAddPoint(new Point(currentPoint));
    }

    public boolean isInside(Point position) {
        if (position.equals(getStartDoor().getPosition()) || position.equals(getEndDoor().getPosition())) {
            return true;
        }
        for (Point point : points()) {
            if (position.equals(point)) {
                return true;
            }
        }
        return false;
    }

    private void wayAddPoint(Point point) {
        for (Point p : way) {
            if (point.equals(p)) {
                return;
            }
        }
        way.add(point);
    }

    private Direction chooseDirection(Point current, Point target) {
        int difX = target.getX() - current.getX();
        int difY = target.getY() - current.getY();

        if (Math.abs(difX) > Math.abs(difY)) {
            if (Rng.nextInt(100) < 70) {
                return difX > 0 ? Direction.RIGHT : Direction.LEFT;
            }
        } else {
            if (Rng.nextInt(100) < 70) {
                return difY > 0 ? Direction.DOWN : Direction.UP;
            }
        }

        return Direction.values()[Rng.nextInt(4)];
    }

    private Direction findSafeDirection(Point current) {
        List<Direction> possibleDirections = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            Point nextPoint = new Point(
                    current.getX() + direction.getX(),
                    current.getY() + direction.getY()
            );
            if (isPossibleTunnelPoint(nextPoint)) {
                possibleDirections.add(direction);
            }
        }

        return possibleDirections.get(Rng.nextInt(possibleDirections.size()));
    }

    private boolean isPossibleTunnelPoint(Point point) {
        if (point.equals(startPosition) || point.equals(endPosition)) {
            return true;
        }
        for (Room room : this.rooms) {
            if (room.isBorder(point)) {
                return false;
            }
        }
        return Location.isInside(point);
    }
}