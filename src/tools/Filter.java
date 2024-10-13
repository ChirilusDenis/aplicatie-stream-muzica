package tools;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter
public class Filter {
    private String name = new String();
    private String description = new String();
    private String album = new String();
    private ArrayList<String> tags = new ArrayList<>();
    private String lyrics = new String();
    private String genre = new String();
    private String releaseYear = new String();
    private String artist = new String();
    private String owner = new String();
}
