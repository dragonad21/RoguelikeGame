package core.domain;

import lombok.Getter;

public class Logger {
    private static Logger instance;
    @Getter
    private String lastEvent = "";

    private Logger() {
    }

    public static Logger getInstance() {
        if (instance == null)
            instance = new Logger();

        return instance;
    }

    public void addEvent(String event) {
        this.lastEvent = event;
    }

    public String getLastEvent() {
        String event = this.lastEvent;
        this.lastEvent = "";
        return event;
    }
}
