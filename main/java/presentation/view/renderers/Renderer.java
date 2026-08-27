package presentation.view.renderers;

import core.domain.space.Visibility;
import presentation.view.PixelsBuffer;

public interface Renderer<T> {
    void render(T object, PixelsBuffer buffer);

    default void setVisibility(Visibility visibility) {
    }
}
