package pages;

import lombok.Getter;

@Getter
public class Announcement {
    private String name;
    private String description;

    public Announcement(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
