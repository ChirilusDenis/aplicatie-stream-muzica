package entities;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public final class Notification {
    private String name;
    private String description;

    public Notification(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
