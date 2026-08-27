package core.domain.space;

import core.config.WorldConstants;
import core.domain.space.graph.Edge;
import core.domain.space.graph.MST;
import core.util.Rng;

import java.util.ArrayList;
import java.util.List;

public class LocationBuilder {
    public LocationBuilder() {
    }

    public Location generate() {
        List<Room> rooms = generateRooms();
        List<Tunnel> tunnels = generateTunnels(rooms);
        Point exit = generateExit(rooms);
        return new Location(rooms, tunnels, exit);
    }

    private List<Room> generateRooms() {
        List<Room> rooms = new ArrayList<>();

        int[][] roomMaxHeights = new int[3][3];
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                roomMaxHeights[row][column] = WorldConstants.ROOM_MAX_HEIGHT;
            }
        }

        for (int column = 0; column < 3; column++) {
            roomMaxHeights[Rng.nextInt(3)][column] = WorldConstants.ROOM_MAX_HEIGHT - 1;
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if (Rng.nextBoolean(0.8)) {
                    int cellStartX = column * (WorldConstants.ROOM_MAX_WIDTH + 1);
                    int cellStartY = 0;
                    for (int i = 0; i < row; i++) {
                        cellStartY += roomMaxHeights[i][column] + 1;
                    }

                    int roomWidth = Rng.between(WorldConstants.ROOM_MIN_WIDTH, WorldConstants.ROOM_MAX_WIDTH);
                    int roomHeight = Rng.between(WorldConstants.ROOM_MIN_HEIGHT, roomMaxHeights[row][column]);

                    int maxOffsetX = WorldConstants.ROOM_MAX_WIDTH - roomWidth;
                    int maxOffsetY = roomMaxHeights[row][column] - roomHeight;

                    int posX = cellStartX + Rng.nextInt(maxOffsetX + 1);
                    int posY = cellStartY + Rng.nextInt(maxOffsetY + 1);
                    rooms.add(new Room(new Point(posX, posY), roomWidth, roomHeight));
                }
            }
        }

        rooms.get(Rng.nextInt(rooms.size())).setStart();
        return rooms;
    }

    private List<Tunnel> generateTunnels(List<Room> rooms) {
        List<Edge> mstEdges = MST.findMST(rooms);
        List<Tunnel> tunnels = new ArrayList<>();
        for (Edge edge : mstEdges) {
            Tunnel tunnel = new Tunnel(rooms, edge.index1(), edge.index2());
            tunnels.add(tunnel);
        }
        return tunnels;
    }

    private Point generateExit(List<Room> rooms) {
        while (true) {
            Room room = rooms.get(Rng.nextInt(rooms.size()));
            if (!room.isStart()) {
                return room.getRandomPointInRoom();
            }
        }
    }
}
