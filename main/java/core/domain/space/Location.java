package core.domain.space;

import core.config.WorldConstants;
import lombok.Getter;

import java.util.List;

public class Location {
    @Getter
    private final List<Room> rooms;
    @Getter
    private final List<Tunnel> tunnels;
    private final Point exit;

    public Location(List<Room> rooms, List<Tunnel> tunnels, Point exit) {
        this.rooms = rooms;
        this.tunnels = tunnels;
        this.exit = exit;
    }

    public Room getStartRoom() {
        for (Room room : rooms)
            if (room.isStart()) return room;

        throw new RuntimeException("There is no start room");
    }

    public Point getExitPosition() {
        return exit;
    }

    public Room getRoom(Point position) {
        for (Room room : rooms) {
            if (room.isInside(position)) return room;
        }
        return null;
    }

    public static boolean isInside(Point position) {
        return position.getX() >= 0 && position.getX() < WorldConstants.LEVEL_WIDTH &&
                position.getY() >= 0 && position.getY() < WorldConstants.LEVEL_HEIGHT;
    }

    public boolean isWalkable(Point position) {
        if (isInsideRoom(position)) return true;

        for (Tunnel tunnel : tunnels)
            if (tunnel.isInside(position)) return true;

        return false;
    }

    public boolean isInsideRoom(Point position) {
        for (Room room : rooms)
            if (room.isInside(position)) return true;

        return false;
    }
}
