import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import core.controller.GameController;

import java.io.IOException;

public class Program {
    public static void main(String[] args) throws IOException {
        Terminal terminal = new DefaultTerminalFactory()
                .setInitialTerminalSize(new TerminalSize(80, 26))
                .createTerminal();

        GameController controller = new GameController(terminal);
        controller.run();

        terminal.close();
    }
}
