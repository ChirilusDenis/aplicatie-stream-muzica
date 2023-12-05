package pages;

import lombok.Getter;

@Getter
public final class Event {
    private String name;
    private String date;
    private String description;

    public Event(String name, String date, String description) {
        this.name = name;
        this.date = date;
        this.description = description;
    }
}
