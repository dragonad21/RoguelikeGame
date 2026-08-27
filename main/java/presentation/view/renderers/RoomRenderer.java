package presentation.view.renderers;

import core.domain.space.Point;
import core.domain.space.Room;
import presentation.config.ColorConstants;
import presentation.view.PixelsBuffer;

public class RoomRenderer implements Renderer<Room> {
    @Override
    public void render(Room room, PixelsBuffer buffer) {
        Point pos = room.getPosition();
        renderWalls(buffer, pos, room.getWidth(), room.getHeight());
        renderFloor(buffer, pos, room.getWidth(), room.getHeight());
    }

    public void renderWallsOnly(Room room, PixelsBuffer buffer) {
        Point pos = room.getPosition();
        renderWalls(buffer, pos, room.getWidth(), room.getHeight());
    }

    private void renderWalls(PixelsBuffer buffer, Point position, int width, int height) {
        buffer.setPixel(position.getX(), position.getY(), '╔', ColorConstants.WALL_COLOR);
        buffer.setPixel(position.getX() + width - 1, position.getY(), '╗', ColorConstants.WALL_COLOR);
        buffer.setPixel(position.getX(), position.getY() + height - 1, '╚', ColorConstants.WALL_COLOR);
        buffer.setPixel(position.getX() + width - 1, position.getY() + height - 1, '╝', ColorConstants.WALL_COLOR);

        for (int x = position.getX() + 1; x < position.getX() + width - 1; x++) {
            buffer.setPixel(x, position.getY(), '═', ColorConstants.WALL_COLOR);
            buffer.setPixel(x, position.getY() + height - 1, '═', ColorConstants.WALL_COLOR);
        }

        for (int y = position.getY() + 1; y < position.getY() + height - 1; y++) {
            buffer.setPixel(position.getX(), y, '║', ColorConstants.WALL_COLOR);
            buffer.setPixel(position.getX() + width - 1, y, '║', ColorConstants.WALL_COLOR);
        }
    }

    private void renderFloor(PixelsBuffer buffer, Point position, int width, int height) {
        for (int y = position.getY() + 1; y < position.getY() + height - 1; y++) {
            for (int x = position.getX() + 1; x < position.getX() + width - 1; x++) {
                buffer.setPixel(x, y, '.', ColorConstants.FLOOR_COLOR);
            }
        }
    }
}
