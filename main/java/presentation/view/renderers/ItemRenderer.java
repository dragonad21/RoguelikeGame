package presentation.view.renderers;

import core.domain.inventory.*;
import core.domain.space.Visibility;
import presentation.config.ColorConstants;
import presentation.view.PixelsBuffer;

public class ItemRenderer implements Renderer<LocationItem> {
    private Visibility visibility;

    @Override
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public void render(LocationItem locationItem, PixelsBuffer buffer) {
        if (visibility != null && !visibility.isVisible(locationItem.position().getX(), locationItem.position().getY())) {
            return;
        }

        if (locationItem.item() instanceof Food) {
            buffer.setPixel(locationItem.position().getX(), locationItem.position().getY(), ':', ColorConstants.ITEM_COLOR);
        } else if (locationItem.item() instanceof Treasure) {
            buffer.setPixel(locationItem.position().getX(), locationItem.position().getY(), '$', ColorConstants.ITEM_COLOR);
        } else if (locationItem.item() instanceof Weapon) {
            buffer.setPixel(locationItem.position().getX(), locationItem.position().getY(), ')', ColorConstants.ITEM_COLOR);
        } else if (locationItem.item() instanceof Elixir) {
            buffer.setPixel(locationItem.position().getX(), locationItem.position().getY(), '!', ColorConstants.ITEM_COLOR);
        } else if (locationItem.item() instanceof Scroll) {
            buffer.setPixel(locationItem.position().getX(), locationItem.position().getY(), '?', ColorConstants.ITEM_COLOR);
        }
    }
}
