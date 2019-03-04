package pl.edu.pwr.wordnetloom.client.events;

import java.awt.*;

public class UpdateCursorEvent {

    private final Cursor cursor;

    public UpdateCursorEvent(Cursor c) {
        this.cursor = c;
    }

    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public String toString() {
        return "UpdateCursorEvent{" +
                "cursor=" + cursor.getName() +
                '}';
    }
}
