package pages;


import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class Merch {
    private String name;
    private String description;
    private int price;

    public Merch(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
