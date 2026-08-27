package presentation.view;

import com.googlecode.lanterna.TextColor;
import lombok.Getter;

@Getter
public class Pixel {

    private char symbol;
    private TextColor color;

    public Pixel() {
        symbol = ' ';
        color = null;
    }

    public void set(char symbol, TextColor color) {
        this.symbol = symbol;
        this.color = color;
    }

    public void clear() {
        symbol = ' ';
        color = null;
    }
}
