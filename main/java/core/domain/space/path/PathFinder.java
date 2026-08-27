package core.domain.space.path;

import core.domain.space.Direction;
import core.domain.space.Location;
import core.domain.space.Point;

import java.util.*;

public class PathFinder {
    private final Location location;

    public PathFinder(Location location) {
        this.location = location;
    }

    public Direction getDirection(Point start, Point target) {
        Queue<Node> nodeQueue = new ArrayDeque<>();
        Set<Point> discoveredPoints = new HashSet<>();

        Node startNode = new Node(start, null);
        nodeQueue.add(startNode);

        while (!nodeQueue.isEmpty()) {
            Node currentNode = nodeQueue.poll();

            for (Direction direction : Direction.values()) {
                Point neighborPosition = currentNode.point().add(direction);

                if (location.isWalkable(neighborPosition)) {
                    Direction nextDirection = currentNode.direction() == null ? direction : currentNode.direction();

                    if (neighborPosition.equals(target)) {
                        return nextDirection;
                    }

                    Node neighborNode = new Node(neighborPosition, nextDirection);

                    if (!discoveredPoints.contains(neighborPosition) && !nodeQueue.contains(neighborNode)) {
                        discoveredPoints.add(neighborNode.point());
                        nodeQueue.add(neighborNode);
                    }
                }
            }
        }
        throw new IllegalStateException("logic.Way does not exist");
    }

    public double getHCost(Point from, Point to) {
        return Math.abs(from.getX() - to.getX()) + Math.abs(from.getY() - to.getY());
    }
}
