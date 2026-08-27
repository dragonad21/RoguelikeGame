package presentation.view;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.Terminal;
import core.domain.Game;
import core.domain.inventory.*;

import java.io.IOException;
import java.util.List;

public class PackView {
    private final Terminal terminal;
    private final Game game;
    private int scrollOffset = 0;
    private static final int PAGE_SIZE = 6;

    public PackView(Terminal terminal, Game game) {
        this.terminal = terminal;
        this.game = game;
    }

    private void drawFrame(int centerX, int startY, String title) throws IOException {
        int boxWidth = Math.max(title.length() + 8, 30);
        int startX = centerX - boxWidth / 2;

        terminal.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
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
        terminal.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
        terminal.putString(title);
    }

    private TextColor getColor(Item<?> item) {
        if (item instanceof Weapon) return TextColor.ANSI.RED_BRIGHT;
        if (item instanceof Food) return TextColor.ANSI.GREEN_BRIGHT;
        if (item instanceof Elixir) return TextColor.ANSI.BLUE_BRIGHT;
        if (item instanceof Scroll) return TextColor.ANSI.MAGENTA_BRIGHT;
        if (item instanceof Treasure) return TextColor.ANSI.YELLOW_BRIGHT;
        return TextColor.ANSI.WHITE_BRIGHT;
    }

    public int showFullContent() throws IOException {
        terminal.clearScreen();
        int centerX = terminal.getTerminalSize().getColumns() / 2;
        int centerY = terminal.getTerminalSize().getRows() / 2;

        drawFrame(centerX, centerY - 10, "INVENTORY");

        List<Item<?>> items = game.getPlayer().getPack().getAllItems();
        int startX = centerX - 15;
        int maxWidth = 26;
        int leftMargin = startX + 2;
        int row = centerY - 5;

        terminal.setCursorPosition(leftMargin, row);
        terminal.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
        String goldText = "Gold: " + game.getPlayer().getGold();
        terminal.putString(goldText);
        row++;

        row++;

        List<Item<?>> nonTreasureItems = new java.util.ArrayList<>();
        for (Item<?> item : items) {
            if (!(item instanceof Treasure)) {
                nonTreasureItems.add(item);
            }
        }

        int totalItems = nonTreasureItems.size();

        if (scrollOffset > totalItems - PAGE_SIZE) {
            scrollOffset = Math.max(0, totalItems - PAGE_SIZE);
        }
        if (scrollOffset < 0) scrollOffset = 0;

        if (totalItems == 0) {
            String msg = "No items";
            terminal.setCursorPosition(centerX - msg.length() / 2, row);
            terminal.setForegroundColor(TextColor.ANSI.WHITE);
            terminal.putString(msg);
            row++;
        } else {
            int endIndex = Math.min(scrollOffset + PAGE_SIZE, totalItems);

            for (int i = scrollOffset; i < endIndex; i++) {
                Item<?> item = nonTreasureItems.get(i);
                String text = String.format("%d) %s", i + 1, item.getDescription());
                TextColor color = getColor(item);

                if (text.length() > maxWidth) {
                    terminal.setCursorPosition(leftMargin, row);
                    terminal.setForegroundColor(color);
                    terminal.putString(text.substring(0, maxWidth));
                    row++;

                    int indent = String.format("%d) ", i + 1).length();
                    terminal.setCursorPosition(leftMargin + indent, row);
                    terminal.setForegroundColor(color);
                    terminal.putString(text.substring(maxWidth));
                } else {
                    terminal.setCursorPosition(leftMargin, row);
                    terminal.setForegroundColor(color);
                    terminal.putString(text);
                }
                row++;
            }
        }

        String hint = "w/s scroll, Choose item (1-9), ESC to exit";
        int hintX = centerX - hint.length() / 2;
        int hintRow = centerY - 5 + PAGE_SIZE + 7;
        terminal.setCursorPosition(hintX, hintRow);
        terminal.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        terminal.putString(hint);

        terminal.flush();

        while (true) {
            KeyStroke key = terminal.readInput();
            KeyType keyType = key.getKeyType();

            if (keyType == KeyType.Escape) {
                return -1;
            }

            if (keyType == KeyType.Character) {
                char c = key.getCharacter();
                if (c == 'w' || c == 'W') {
                    if (scrollOffset > 0) {
                        scrollOffset--;
                        return showFullContent();
                    }
                } else if (c == 's' || c == 'S') {
                    if (scrollOffset + PAGE_SIZE < totalItems) {
                        scrollOffset++;
                        return showFullContent();
                    }
                } else if (Character.isDigit(c)) {
                    int slot = Character.getNumericValue(c);
                    if (slot > 0 && slot <= totalItems) {
                        return slot;
                    }
                }
            }
        }
    }

    public <T extends Item<?>> int showItemContent(Class<T> itemClass) throws IOException {
        terminal.clearScreen();
        int centerX = terminal.getTerminalSize().getColumns() / 2;
        int centerY = terminal.getTerminalSize().getRows() / 2;

        String title = switch (itemClass.getSimpleName()) {
            case "Weapon" -> "WEAPONS";
            case "Food" -> "FOOD";
            case "Elixir" -> "ELIXIRS";
            case "Scroll" -> "SCROLLS";
            default -> "ITEMS";
        };

        drawFrame(centerX, centerY - 10, title);

        List<Item<?>> items = game.getPlayer().getPack().getAllItems();
        int startX = centerX - 15;
        int maxWidth = 26;
        int leftMargin = startX + 2;
        int row = centerY - 5;

        List<Item<?>> filtered = new java.util.ArrayList<>();
        for (Item<?> item : items) {
            if (itemClass.isInstance(item) && !(item instanceof Treasure)) {
                filtered.add(item);
            }
        }

        boolean hasEquipped = itemClass.equals(Weapon.class) && game.getPlayer().getEquippedWeapon() != null;
        int totalItems = filtered.size();
        int equippedOffset = hasEquipped ? 2 : 0;

        if (scrollOffset > totalItems - PAGE_SIZE) {
            scrollOffset = Math.max(0, totalItems - PAGE_SIZE);
        }
        if (scrollOffset < 0) scrollOffset = 0;

        if (hasEquipped) {
            Weapon equipped = game.getPlayer().getEquippedWeapon();
            String text = equipped.getDescription() + " [EQUIPPED]";
            terminal.setCursorPosition(leftMargin, row);
            terminal.setForegroundColor(TextColor.ANSI.GREEN_BRIGHT);
            if (text.length() > maxWidth) {
                terminal.putString(text.substring(0, maxWidth));
                row++;
                terminal.setCursorPosition(leftMargin, row);
                terminal.putString(text.substring(maxWidth));
            } else {
                terminal.putString(text);
            }
            row++;
            row++;
        }

        if (totalItems == 0 && !hasEquipped) {
            String msg = "No items of this type";
            terminal.setCursorPosition(centerX - msg.length() / 2, row);
            terminal.setForegroundColor(TextColor.ANSI.WHITE);
            terminal.putString(msg);
        } else {
            int endIndex = Math.min(scrollOffset + PAGE_SIZE, totalItems);

            for (int i = scrollOffset; i < endIndex; i++) {
                Item<?> item = filtered.get(i);
                int realIndex = items.indexOf(item) + 1;
                String text = String.format("%d) %s", realIndex, item.getDescription());
                TextColor color = getColor(item);

                if (text.length() > maxWidth) {
                    terminal.setCursorPosition(leftMargin, row);
                    terminal.setForegroundColor(color);
                    terminal.putString(text.substring(0, maxWidth));
                    row++;
                    int indent = String.format("%d) ", realIndex).length();
                    terminal.setCursorPosition(leftMargin + indent, row);
                    terminal.setForegroundColor(color);
                    terminal.putString(text.substring(maxWidth));
                } else {
                    terminal.setCursorPosition(leftMargin, row);
                    terminal.setForegroundColor(color);
                    terminal.putString(text);
                }
                row++;
            }
        }

        String hint;
        if (itemClass.equals(Weapon.class)) {
            hint = "w/s scroll, Choose weapon (1-9), ESC to exit";
        } else {
            hint = "w/s scroll, Choose item (1-9), ESC to exit";
        }
        int hintX = centerX - hint.length() / 2;
        int hintRow = centerY - 5 + PAGE_SIZE + equippedOffset + 5;
        terminal.setCursorPosition(hintX, hintRow);
        terminal.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        terminal.putString(hint);

        terminal.flush();

        while (true) {
            KeyStroke key = terminal.readInput();
            KeyType keyType = key.getKeyType();

            if (keyType == KeyType.Escape) {
                return -1;
            }

            if (keyType == KeyType.Character) {
                char c = key.getCharacter();
                if (c == 'w' || c == 'W') {
                    if (scrollOffset > 0) {
                        scrollOffset--;
                        return showItemContent(itemClass);
                    }
                } else if (c == 's' || c == 'S') {
                    if (scrollOffset + PAGE_SIZE < totalItems) {
                        scrollOffset++;
                        return showItemContent(itemClass);
                    }
                } else if (Character.isDigit(c)) {
                    int slot = Character.getNumericValue(c);
                    if (slot == 0) {
                        if (itemClass.equals(Weapon.class)) {
                            return 0;
                        }
                        continue;
                    }
                    if (slot > 0 && slot <= items.size()) {
                        return slot;
                    }
                }
            }
        }
    }
}