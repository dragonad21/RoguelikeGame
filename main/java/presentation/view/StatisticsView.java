package presentation.view;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;
import datalayer.dto.StatisticsRecord;
import datalayer.serialization.StatisticsService;

import java.io.IOException;
import java.util.List;

public class StatisticsView {
    private final Terminal terminal;
    private int scrollOffset = 0;
    private static final int PAGE_SIZE = 6;

    public StatisticsView(Terminal terminal) {
        this.terminal = terminal;
    }

    public void show() throws IOException {
        terminal.clearScreen();
        terminal.setCursorVisible(false);

        List<StatisticsRecord> records = StatisticsService.loadSorted();
        int totalRecords = records.size();

        scrollOffset = 0;

        renderPage(records);

        while (true) {
            KeyStroke key = terminal.readInput();
            KeyType keyType = key.getKeyType();

            if (keyType == KeyType.Escape) {
                break;
            }

            if (keyType == KeyType.Character) {
                char c = key.getCharacter();
                if (c == 's' || c == 'S') {
                    if (scrollOffset + PAGE_SIZE < totalRecords) {
                        scrollOffset++;
                        renderPage(records);
                    }
                } else if (c == 'w' || c == 'W') {
                    if (scrollOffset > 0) {
                        scrollOffset--;
                        renderPage(records);
                    }
                }
            }
        }

        terminal.clearScreen();
        terminal.flush();
    }

    private void renderPage(List<StatisticsRecord> records) throws IOException {
        int termWidth = terminal.getTerminalSize().getColumns();
        int termHeight = terminal.getTerminalSize().getRows();

        int centerX = termWidth / 2;
        int centerY = termHeight / 2;

        int totalRecords = records.size();

        // ========== РАМКА ВОКРУГ ЗАГОЛОВКА ==========
        int boxWidth = 30;
        int startX = centerX - boxWidth / 2;
        int startY = centerY - 10;

        terminal.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);

        terminal.setCursorPosition(startX, startY);
        terminal.putString("╔" + "═".repeat(boxWidth - 2) + "╗");

        terminal.setCursorPosition(startX, startY + 1);
        terminal.putString("║");
        terminal.setCursorPosition(startX + boxWidth - 1, startY + 1);
        terminal.putString("║");

        terminal.setCursorPosition(startX, startY + 2);
        terminal.putString("╚" + "═".repeat(boxWidth - 2) + "╝");

        String title = "LEADERBOARD";
        int titleX = centerX - title.length() / 2;
        terminal.setCursorPosition(titleX, startY + 1);
        terminal.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
        terminal.putString(title);

        // ========== ТАБЛИЦА ==========
        int row = startY + 5;

        if (records.isEmpty()) {
            String msg = "No records yet. Play the game!";
            int msgX = centerX - msg.length() / 2;
            terminal.setCursorPosition(msgX, row);
            terminal.setForegroundColor(TextColor.ANSI.WHITE);
            terminal.putString(msg);
            row += 2;
        } else {
            // Заголовки колонок
            String header = String.format("%-3s %4s %4s %4s %4s %4s %4s %4s %4s %5s",
                    "#", "T", "L", "K", "F", "E", "S", "H", "M", "St");
            int headerX = centerX - header.length() / 2;
            terminal.setCursorPosition(headerX, row);
            terminal.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
            terminal.putString(header);
            row++;

            // Разделитель
            String separator = "─".repeat(header.length());
            int sepX = centerX - separator.length() / 2;
            terminal.setCursorPosition(sepX, row);
            terminal.setForegroundColor(TextColor.ANSI.WHITE);
            terminal.putString(separator);
            row++;

            // Определяем, какие записи показывать
            int startIndex = scrollOffset;
            int endIndex = Math.min(startIndex + PAGE_SIZE, totalRecords);

            for (int i = startIndex; i < endIndex; i++) {
                StatisticsRecord record = records.get(i);
                String line = String.format("%-3d %4d %4d %4d %4d %4d %4d %4d %4d %5d",
                        i + 1,
                        record.getTreasures(),
                        record.getLevel(),
                        record.getKills(),
                        record.getFoodApply(),
                        record.getElixirsApply(),
                        record.getScrollsApply(),
                        record.getHits(),
                        record.getMisses(),
                        record.getSteps()
                );

                int lineX = centerX - line.length() / 2;
                terminal.setCursorPosition(lineX, row);

                if (i == 0) {
                    terminal.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
                } else {
                    terminal.setForegroundColor(TextColor.ANSI.WHITE);
                }

                terminal.putString(line);
                row++;
            }

            while (row < startY + 5 + PAGE_SIZE + 2) {
                String emptyLine = " ".repeat(70);
                terminal.setCursorPosition(centerX - emptyLine.length() / 2, row);
                terminal.putString(emptyLine);
                row++;
            }
        }

        String pageInfo = String.format("Showing %d-%d of %d",
                scrollOffset + 1,
                Math.min(scrollOffset + PAGE_SIZE, totalRecords),
                totalRecords);
        int infoX = centerX - pageInfo.length() / 2;
        int infoRow = startY + 5 + PAGE_SIZE + 3;  // Отступ в 3 строки от последней строки таблицы
        terminal.setCursorPosition(infoX, infoRow);
        terminal.setForegroundColor(TextColor.ANSI.WHITE);
        terminal.putString(pageInfo);

        String hint = "ESC to exit";
        int hintX = centerX - hint.length() / 2;
        int hintRow = infoRow + 2;  // Ещё через 2 строки
        terminal.setCursorPosition(hintX, hintRow);
        terminal.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        terminal.putString(hint);

        terminal.flush();
    }
}