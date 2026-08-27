package presentation.view.renderers;

import core.domain.units.Player;
import presentation.config.ColorConstants;
import presentation.view.PixelsBuffer;

public class PlayerRenderer implements Renderer<Player> {
    @Override
    public void render(Player player, PixelsBuffer buffer) {
        buffer.setPixel(player.getPosition().getX(), player.getPosition().getY(), '☺', ColorConstants.PLAYER_COLOR);
    }
}
