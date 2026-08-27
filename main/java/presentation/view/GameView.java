package presentation.view;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;
import core.config.WorldConstants;
import core.domain.Game;
import core.domain.Logger;
import core.domain.ModelListener;
import core.domain.inventory.LocationItem;
import core.domain.levels.Level;
import core.domain.space.Visibility;
import core.domain.units.Enemy;
import core.domain.units.Ghost;
import core.domain.units.Player;
import presentation.config.ColorConstants;
import presentation.view.renderers.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class GameView implements ModelListener {
    private final Terminal terminal;
    private final PixelsBuffer buffer;
    private final Map<Class<?>, Renderer<?>> renderers;
    private Game game;
    private String lastEvent = " ";

    public GameView(Terminal terminal) {
        this.terminal = terminal;
        this.buffer = new PixelsBuffer();
        this.renderers = new HashMap<>();
        registerRenderers();
    }

    public void setModel(Game game) {
        this.game = game;
        this.game.addModelListener(this);
    }

    @Override
    public void onModelChanged() {
        try {
            render();
        } catch (Exception e) {
            System.err.println("Render error: " + e.getMessage());
        }
    }

    public void render() throws IOException {
        if (game == null) return;
        buffer.clear();
        terminal.clearScreen();
        showLastEvent();
        updateRenderersVisibility();
        renderStaticObjects();
        renderDynamicObjects();
        showBuffer();
        showStatusBar();
        terminal.flush();
    }

    private void updateRenderersVisibility() {
        Visibility vis = game.isFogVisible() ? game.getVisibility() : null;

        setRendererVisibility(Level.class, vis);
        setRendererVisibility(Enemy.class, vis);
        setRendererVisibility(LocationItem.class, vis);
    }

    @SuppressWarnings("unchecked")
    private <T> void setRendererVisibility(Class<T> clazz, Visibility visibility) {
        Renderer<T> renderer = (Renderer<T>) renderers.get(clazz);
        if (renderer != null) {
            renderer.setVisibility(visibility);
        }
    }

    private void showLastEvent() throws IOException {
        terminal.setCursorPosition(0, 0);
        terminal.setForegroundColor(ColorConstants.EVENT_COLOR);
        String currentEvent = Logger.getInstance().getLastEvent();

        if (currentEvent != null && !currentEvent.isEmpty()) {
            terminal.putString("%s".formatted(currentEvent));
        } else {
            terminal.putString(" ");
        }
    }

    private void showBuffer() throws IOException {
        int startCursorPositionY = 1;
        for (int y = 0; y < WorldConstants.LEVEL_HEIGHT; y++) {
            terminal.setCursorPosition(0, startCursorPositionY + y);
            for (int x = 0; x < WorldConstants.LEVEL_WIDTH; x++) {
                terminal.setForegroundColor(buffer.getPixel(x, y).getColor());
                terminal.putCharacter(buffer.getPixel(x, y).getSymbol());
            }
        }
    }

    private void showStatusBar() throws IOException {
        int termWidth = terminal.getTerminalSize().getColumns();
        int termHeight = terminal.getTerminalSize().getRows();

        String status = "Level:%-5d ❤️ %d(%d)  ⚔ Str:%d  🛡 Dxt:%d  💰 Gold:%-8d"
                .formatted(game.getLevelManager().getCurrentLevelNumber(),
                        game.getPlayer().getHealth(),
                        game.getPlayer().getMaxHealth(),
                        game.getPlayer().getStrength(),
                        game.getPlayer().getDexterity(),
                        game.getPlayer().getGold());

        int cursorX = (termWidth - status.length()) / 2;
        if (cursorX < 0) cursorX = 0;

        int cursorY = termHeight - 2;

        terminal.setCursorPosition(cursorX, cursorY);
        terminal.setForegroundColor(ColorConstants.STATUS_BAR_COLOR);
        terminal.putString(status);
    }

    public int showGameOver(boolean won) throws IOException {
        terminal.clearScreen();

        int termWidth = terminal.getTerminalSize().getColumns();
        int termHeight = terminal.getTerminalSize().getRows();

        int centerX = termWidth / 2;
        int centerY = termHeight / 2;

        TextColor titleColor = won ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.RED_BRIGHT;
        String title = won ? "🏆  YOU WON!  🏆" : "💀  GAME OVER  💀";
        String subTitle = won ? "Congratulations! You've completed all 21 levels!" : "Your adventure has come to an end...";

        int boxWidth = 40;
        int startX = centerX - boxWidth / 2;
        int startY = centerY - 6;

        terminal.setForegroundColor(titleColor);

        terminal.setCursorPosition(startX, startY);
        terminal.putString("╔" + "═".repeat(boxWidth - 2) + "╗");

        terminal.setCursorPosition(startX, startY + 1);
        terminal.putString("║");
        terminal.setCursorPosition(startX + boxWidth - 1, startY + 1);
        terminal.putString("║");

        terminal.setCursorPosition(startX, startY + 2);
        terminal.putString("╚" + "═".repeat(boxWidth - 2) + "╝");

        int titleX = centerX - title.length() / 2;
        terminal.setCursorPosition(titleX, startY + 1);
        terminal.setForegroundColor(titleColor);
        terminal.putString(title);

        if (game != null) {
            int subX = centerX - subTitle.length() / 2;
            terminal.setCursorPosition(subX, startY + 5);
            terminal.setForegroundColor(TextColor.ANSI.WHITE);
            terminal.putString(subTitle);

            String stats = String.format(
                    "Level: %d   Gold: %d   Kills: %d   Steps: %d",
                    game.getLevelManager().getCurrentLevelNumber(),
                    game.getPlayer().getGold(),
                    game.getCurrentRun().getKills(),
                    game.getCurrentRun().getSteps()
            );
            int statsX = centerX - stats.length() / 2;
            terminal.setCursorPosition(statsX, startY + 8);
            terminal.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
            terminal.putString(stats);
        }

        String hint = won ? "Press 'SPACE' to continue" : "Press 'SPACE' to new game or 'ESC' to main menu";
        int hintX = centerX - hint.length() / 2;
        int hintY = startY + 12;
        terminal.setCursorPosition(hintX, hintY);
        terminal.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        terminal.putString(hint);

        terminal.flush();

        while (true) {
            KeyStroke key = terminal.readInput();
            KeyType keyType = key.getKeyType();

            if (keyType == KeyType.Escape) {
                return 0;
            } else if (keyType == KeyType.Character && key.getCharacter() == ' ') {
                return 1;
            }
        }
    }

    private void renderStaticObjects() {
        renderObject(game.getLevelManager().getCurrentLevel());
        for (LocationItem item : game.getLevelManager().getCurrentLevel().getLocationItems()) {
            renderObject(item);
        }
    }

    private void renderDynamicObjects() {
        renderObject(game.getPlayer());
        for (Enemy enemy : game.getLevelManager().getCurrentLevel().getEnemies()) {
            if (enemy instanceof Ghost ghost) {
                if (ghost.isVisible()) {
                    renderObject(enemy);
                }
            } else {
                renderObject(enemy);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void renderObject(T object) {
        if (object instanceof Enemy) {
            Renderer<Enemy> renderer = (Renderer<Enemy>) renderers.get(Enemy.class);
            if (renderer != null) {
                renderer.render((Enemy) object, buffer);
            }
            return;
        }

        Renderer<T> renderer = (Renderer<T>) renderers.get(object.getClass());
        if (renderer != null) {
            renderer.render(object, buffer);
        }
    }

    private void registerRenderers() {
        registerRenderer(Level.class, new LevelRenderer(new LocationRenderer(new RoomRenderer(), new TunnelRenderer())));
        registerRenderer(Player.class, new PlayerRenderer());
        registerRenderer(Enemy.class, new EnemyRenderer());
        registerRenderer(LocationItem.class, new ItemRenderer());
    }

    private <T> void registerRenderer(Class<T> sourceClass, Renderer<T> renderer) {
        renderers.put(sourceClass, renderer);
    }
}