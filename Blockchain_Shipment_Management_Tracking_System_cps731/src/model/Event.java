package model;

import java.util.Date;

public class Event {

    private final Date timestamp;
    private final String message;

    // CONSTRUCTOR
    public Event(Date timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }

    // GETTERS
    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + message;
    }
}
