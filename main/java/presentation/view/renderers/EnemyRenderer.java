package presentation.view.renderers;

import core.domain.space.Visibility;
import core.domain.units.*;
import presentation.config.ColorConstants;
import presentation.view.PixelsBuffer;

public class EnemyRenderer implements Renderer<Enemy> {
    private Visibility visibility;

    @Override
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public void render(Enemy enemy, PixelsBuffer buffer) {
        if (visibility != null && !visibility.isVisible(enemy.getPosition().getX(), enemy.getPosition().getY())) {
            return;
        }

        if (enemy instanceof Mimic mimic) {
            if (!mimic.isRevealed()) {
                buffer.setPixel(enemy.getPosition().getX(), enemy.getPosition().getY(), mimic.getFakeSymbol(), ColorConstants.ITEM_COLOR);
            } else {
                buffer.setPixel(enemy.getPosition().getX(), enemy.getPosition().getY(), 'M', ColorConstants.MIMIC_COLOR);
            }
        } else if (enemy instanceof Zombie) {
            buffer.setPixel(enemy.getPosition().getX(), enemy.getPosition().getY(), 'Z', ColorConstants.ZOMBIE_COLOR);
        } else if (enemy instanceof Vampire) {
            buffer.setPixel(enemy.getPosition().getX(), enemy.getPosition().getY(), 'V', ColorConstants.VAMPIRE_COLOR);
        } else if (enemy instanceof Ghost) {
            buffer.setPixel(enemy.getPosition().getX(), enemy.getPosition().getY(), 'G', ColorConstants.GHOST_COLOR);
        } else if (enemy instanceof Ogre) {
            buffer.setPixel(enemy.getPosition().getX(), enemy.getPosition().getY(), 'O', ColorConstants.OGRE_COLOR);
        } else if (enemy instanceof SnakeMage) {
            buffer.setPixel(enemy.getPosition().getX(), enemy.getPosition().getY(), 'S', ColorConstants.SNAKE_MAGE_COLOR);
        }
    }
}
