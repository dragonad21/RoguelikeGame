package presentation.view;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;
import datalayer.serialization.SaveService;

import java.io.IOException;

public class MainMenuView {
    private final Terminal terminal;
    private int selectedIndex = 0;

    public static final int EXIT = -1;
    public static final int START_NEW_GAME = 0;
    public static final int LOAD_GAME = 1;
    public static final int LEADERBOARD = 2;

    private String[] getOptions() {
        boolean hasSave = SaveService.hasSave();
        if (hasSave) {
            return new String[]{"▶  Start New Game", "▶  Load Game", "▶  Leaderboard", "▶  Exit"};
        } else {
            return new String[]{"▶  Start New Game", "▶  Leaderboard", "▶  Exit"};
        }
    }

    public MainMenuView(Terminal terminal) {
        this.terminal = terminal;
    }

    public int show() throws IOException {
        while (true) {
            terminal.clearScreen();
            terminal.setCursorVisible(false);

            int termWidth = terminal.getTerminalSize().getColumns();
            int termHeight = terminal.getTerminalSize().getRows();

            int centerX = termWidth / 2;
            int centerY = termHeight / 2;

            // ========== ЗАГОЛОВОК В РАМКЕ ==========
            int boxWidth = 40;
            int startX = centerX - boxWidth / 2;
            int startY = centerY - 8;

            terminal.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);

            terminal.setCursorPosition(startX, startY);
            terminal.putString("╔" + "═".repeat(boxWidth - 2) + "╗");

            terminal.setCursorPosition(startX, startY + 1);
            terminal.putString("║");
            terminal.setCursorPosition(startX + boxWidth - 1, startY + 1);
            terminal.putString("║");

            terminal.setCursorPosition(startX, startY + 2);
            terminal.putString("╚" + "═".repeat(boxWidth - 2) + "╝");

            String title = "Rogue Game";
            int titleX = centerX - title.length() / 2;
            terminal.setCursorPosition(titleX, startY + 1);
            terminal.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
            terminal.putString(title);

            // ========== РАЗДЕЛИТЕЛЬ ==========
            String separator = "═".repeat(40);
            int sepX = centerX - separator.length() / 2;
            terminal.setCursorPosition(sepX, startY + 4);
            terminal.setForegroundColor(TextColor.ANSI.WHITE);
            terminal.putString(separator);

            // ========== РИСУЕМ МЕНЮ ==========
            int menuStartY = startY + 6;
            drawMenu(menuStartY, centerX);

            // ========== ПОДСКАЗКА ==========
            String hint = "Use w/s to navigate, Enter to select";
            int hintX = centerX - hint.length() / 2;
            terminal.setCursorPosition(hintX, startY + 16);
            terminal.setForegroundColor(TextColor.ANSI.WHITE);
            terminal.putString(hint);

            terminal.flush();

            // ========== ОБРАБОТКА ВВОДА ==========
            while (true) {
                KeyStroke key = terminal.readInput();
                KeyType keyType = key.getKeyType();

                if (keyType == KeyType.Character) {
                    char c = key.getCharacter();
                    if (c == 'w') {
                        moveSelection(-1);
                        drawMenu(menuStartY, centerX);
                        continue;
                    } else if (c == 's') {
                        moveSelection(1);
                        drawMenu(menuStartY, centerX);
                        continue;
                    }
                }

                if (keyType == KeyType.Enter) {
                    String[] options = getOptions();
                    String selected = options[selectedIndex];
                    if (selected.contains("Start New Game")) {
                        return START_NEW_GAME;
                    } else if (selected.contains("Load Game")) {
                        if (SaveService.hasSave()) {
                            return LOAD_GAME;
                        }
                    } else if (selected.contains("Leaderboard")) {
                        return LEADERBOARD;
                    } else if (selected.contains("Exit")) {
                        return EXIT;
                    }
                }
            }
        }
    }

    private void moveSelection(int direction) {
        String[] options = getOptions();
        selectedIndex += direction;
        if (selectedIndex < 0) selectedIndex = options.length - 1;
        if (selectedIndex >= options.length) selectedIndex = 0;
    }

    private void drawMenu(int startY, int centerX) throws IOException {
        String[] options = getOptions();

        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            int optionX = centerX - option.length() / 2;
            terminal.setCursorPosition(optionX, startY + i * 2);

            if (i == selectedIndex) {
                terminal.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
                terminal.putString(option);
            } else {
                terminal.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
                terminal.putString(option);
            }
        }

        terminal.flush();
    }
}