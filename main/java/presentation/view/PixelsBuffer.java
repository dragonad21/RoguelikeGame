package presentation.view;

import com.googlecode.lanterna.TextColor;
import core.config.WorldConstants;

public class PixelsBuffer {
    private final Pixel[][] pixels;
    private final int width;
    private final int height;

    public PixelsBuffer() {
        this.width = WorldConstants.LEVEL_WIDTH;
        this.height = WorldConstants.LEVEL_HEIGHT;
        this.pixels = new Pixel[height][width];
        initialize();
    }

    public void setPixel(int x, int y, char symbol, TextColor color) {
        if (isInvalidPosition(x, y)) {
            throw new IllegalArgumentException("Unavailable position!");
        }
        pixels[y][x].set(symbol, color);
    }

    public Pixel getPixel(int x, int y) {
        if (isInvalidPosition(x, y)) {
            throw new IllegalArgumentException("Unavailable position!");
        }
        return pixels[y][x];
    }

    public void clear() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y][x].clear();
            }
        }
    }

    private void initialize() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y][x] = new Pixel();
            }
        }
    }

    private boolean isInvalidPosition(int x, int y) {
        return x < 0 || x >= width || y < 0 || y >= height;
    }
}
