package presentation.view.renderers;

import core.domain.space.*;
import lombok.Setter;
import presentation.config.ColorConstants;
import presentation.view.PixelsBuffer;

public class LocationRenderer implements Renderer<Location> {
    private final Renderer<Room> roomRenderer;
    private final Renderer<Tunnel> tunnelRenderer;
    @Setter
    private Visibility visibility;

    public LocationRenderer(Renderer<Room> roomRenderer, Renderer<Tunnel> tunnelRenderer) {
        this.roomRenderer = roomRenderer;
        this.tunnelRenderer = tunnelRenderer;
    }

    public void render(Location location, PixelsBuffer buffer) {
        for (Room room : location.getRooms()) {
            if (visibility == null || isRoomVisible(room)) {
                roomRenderer.render(room, buffer);
            } else if (isRoomExplored(room)) {
                ((RoomRenderer) roomRenderer).renderWallsOnly(room, buffer);
            }
        }

        for (Tunnel tunnel : location.getTunnels()) {
            if (visibility == null || isTunnelVisible(tunnel)) {
                tunnelRenderer.render(tunnel, buffer);
            }
        }

        if (visibility == null || visibility.isVisible(location.getExitPosition().getX(), location.getExitPosition().getY())) {
            buffer.setPixel(location.getExitPosition().getX(), location.getExitPosition().getY(), '☰', ColorConstants.EXIT_COLOR);
        }
    }

    private boolean isRoomVisible(Room room) {
        Point pos = room.getPosition();
        for (int y = pos.getY(); y < pos.getY() + room.getHeight(); y++) {
            for (int x = pos.getX(); x < pos.getX() + room.getWidth(); x++) {
                if (visibility.isVisible(x, y)) return true;
            }
        }
        return false;
    }

    private boolean isRoomExplored(Room room) {
        Point pos = room.getPosition();
        for (int y = pos.getY(); y < pos.getY() + room.getHeight(); y++) {
            for (int x = pos.getX(); x < pos.getX() + room.getWidth(); x++) {
                if (visibility.isExplored(x, y)) return true;
            }
        }
        return false;
    }

    private boolean isTunnelVisible(Tunnel tunnel) {
        for (Point p : tunnel.points()) {
            if (visibility.isVisible(p.getX(), p.getY())) return true;
        }
        return false;
    }

}
