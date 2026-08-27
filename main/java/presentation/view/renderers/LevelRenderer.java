package presentation.view.renderers;

import core.domain.levels.Level;
import core.domain.space.Visibility;
import presentation.view.PixelsBuffer;

public class LevelRenderer implements Renderer<Level> {
    private final LocationRenderer locationRenderer;

    public LevelRenderer(LocationRenderer locationRenderer) {
        this.locationRenderer = locationRenderer;
    }

    @Override
    public void setVisibility(Visibility visibility) {
        locationRenderer.setVisibility(visibility);
    }

    @Override
    public void render(Level level, PixelsBuffer buffer) {
        locationRenderer.render(level.getLocation(), buffer);
    }
}
