package core.domain.space;

public class Visibility {
    private final boolean[][] explored;
    private final boolean[][] visible;
    private final int width;
    private final int height;

    public Visibility(int width, int height) {
        this.width = width;
        this.height = height;
        this.explored = new boolean[width][height];
        this.visible = new boolean[width][height];
    }

    public boolean isExplored(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return false;
        return explored[x][y];
    }

    public boolean isVisible(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return false;
        return visible[x][y];
    }

    public void setVisible(int x, int y) {
        visible[x][y] = true;
        explored[x][y] = true;
    }

    public void clearVisible() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                visible[x][y] = false;
            }
        }
    }

}
