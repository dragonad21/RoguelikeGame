package presentation.view.renderers;

import core.domain.space.Door;
import core.domain.space.Point;
import core.domain.space.Tunnel;
import presentation.config.ColorConstants;
import presentation.view.PixelsBuffer;

public class TunnelRenderer implements Renderer<Tunnel> {
    @Override
    public void render(Tunnel tunnel, PixelsBuffer buffer) {
        renderDoor(tunnel.getStartDoor(), buffer);
        renderDoor(tunnel.getEndDoor(), buffer);

        for (Point point : tunnel.points()) {
            buffer.setPixel(point.getX(), point.getY(), '░', ColorConstants.TUNNEL_COLOR);
        }
    }

    private void renderDoor(Door door, PixelsBuffer buffer) {
        buffer.setPixel(door.getPosition().getX(), door.getPosition().getY(), doorSymbol(door), ColorConstants.TUNNEL_COLOR);
    }

    private char doorSymbol(Door door) {
        return door.checkHorizontal() ? '=' : '║';
    }
}
