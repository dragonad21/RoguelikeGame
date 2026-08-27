package core.controller;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;
import core.config.WorldConstants;
import core.domain.Game;
import core.domain.GameBuilder;
import core.domain.GameListener;
import core.domain.inventory.*;
import core.domain.space.Direction;
import datalayer.dto.GameSnapshot;
import datalayer.serialization.SaveService;
import datalayer.serialization.StatisticsService;
import presentation.view.GameView;
import presentation.view.MainMenuView;
import presentation.view.PackView;
import presentation.view.StatisticsView;

import java.io.IOException;

public class GameController implements GameListener {

    private Game game;
    private GameView view;
    private final Terminal terminal;
    private boolean inGame = false;
    private boolean restartRequested = false;

    public GameController(Terminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public void onGameOver() {
        if (game != null && game.getCurrentRun() != null) {
            game.getCurrentRun().setTreasures(game.getPlayer().getGold());
            StatisticsService.saveRun(game.getCurrentRun());
        }

        try {
            boolean won = game != null && game.getLevelManager().getCurrentLevelNumber() == WorldConstants.LEVELS_LIMIT;
            int choice = view.showGameOver(won);

            if (choice == 1) {
                restartRequested = true;
                inGame = false;
                SaveService.deleteSave();
            } else {
                inGame = false;
            }
        } catch (IOException e) {
            System.err.println("Failed to show game over menu: " + e.getMessage());
        }
    }

    public void run() throws IOException {
        while (true) {
            if (!restartRequested) {
                MainMenuView menuView = new MainMenuView(terminal);
                int choice = menuView.show();

                if (choice == MainMenuView.EXIT) {
                    break;
                }

                if (choice == MainMenuView.LEADERBOARD) {
                    StatisticsView statsView = new StatisticsView(terminal);
                    statsView.show();
                    continue;
                }

                if (choice == MainMenuView.LOAD_GAME) {
                    GameSnapshot snapshot = SaveService.loadGame();
                    if (snapshot != null) {
                        this.game = GameBuilder.restoreGame(snapshot);
                    } else {
                        this.game = new GameBuilder().generate();
                    }
                } else {
                    SaveService.deleteSave();
                    this.game = new GameBuilder().generate();
                }

            } else {
                restartRequested = false;
                SaveService.deleteSave();
                this.game = new GameBuilder().generate();
            }

            this.game.addGameListener(this);
            this.view = new GameView(terminal);
            this.view.setModel(game);

            terminal.enterPrivateMode();
            terminal.setCursorVisible(false);
            view.render();

            inGame = true;

            try {
                while (inGame) {
                    KeyStroke key = terminal.readInput();
                    if (key == null) {
                        break;
                    }
                    handleInput(key);
                }
            } catch (IOException e) {
                System.err.println("IO error in game loop: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error in game loop: " + e.getMessage());
            }

            try {
                terminal.clearScreen();
                terminal.setCursorVisible(true);
            } catch (IOException e) {
                System.err.println("Failed to clear screen when returning to menu: " + e.getMessage());
            }
            inGame = false;
        }
    }

    private void handleInput(KeyStroke key) throws IOException {
        if (key.getKeyType() == KeyType.Escape) {
            inGame = false;
            return;
        }

        if (movePlayerHandler(key)) {
            return;
        }

        if (packMenuHandler(key)) {
            return;
        }
    }

    private boolean movePlayerHandler(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.Character) {
            switch (keyStroke.getCharacter()) {
                case 'a':
                    game.movePlayer(Direction.LEFT);
                    return true;
                case 's':
                    game.movePlayer(Direction.DOWN);
                    return true;
                case 'd':
                    game.movePlayer(Direction.RIGHT);
                    return true;
                case 'w':
                    game.movePlayer(Direction.UP);
                    return true;
                case '>':
                    if (game.getPlayer().getPosition().equals(
                            game.getLevelManager().getCurrentLocation().getExitPosition())) {
                        game.goToNextLevel();
                    }
                    return true;
                case 'v':
                    game.switchFog();
                    return true;
            }
        } else {
            switch (keyStroke.getKeyType()) {
                case ArrowUp:
                    game.movePlayer(Direction.UP);
                    return true;
                case ArrowDown:
                    game.movePlayer(Direction.DOWN);
                    return true;
                case ArrowLeft:
                    game.movePlayer(Direction.LEFT);
                    return true;
                case ArrowRight:
                    game.movePlayer(Direction.RIGHT);
                    return true;
            }
        }
        return false;
    }

    private boolean packMenuHandler(KeyStroke keyStroke) throws IOException {
        if (keyStroke.getKeyType() == KeyType.Character) {
            switch (keyStroke.getCharacter()) {
                case 'i':
                    packContentControl(null);
                    return true;
                case 'h':
                    packContentControl(Weapon.class);
                    return true;
                case 'j':
                    packContentControl(Food.class);
                    return true;
                case 'k':
                    packContentControl(Elixir.class);
                    return true;
                case 'e':
                    packContentControl(Scroll.class);
                    return true;
            }
        }
        return false;
    }

    private <T extends Item<?>> void packContentControl(Class<T> itemClass) throws IOException {
        PackView packView = new PackView(terminal, game);
        int selectedSlot;

        if (itemClass == null) {
            selectedSlot = packView.showFullContent();
        } else {
            selectedSlot = packView.showItemContent(itemClass);
        }

        if (selectedSlot == -1) {
            view.render();
            return;
        }

        if (selectedSlot == 0 && itemClass != null && itemClass.equals(Weapon.class)) {
            game.getPlayer().useItem(0, game);
            view.render();
            return;
        }

        if (selectedSlot > 0) {
            game.getPlayer().useItem(selectedSlot, game);
        }

        view.render();
    }
}