package entitycolections;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import entities.Song;

import java.util.ArrayList;

@JsonIgnoreProperties({"visibility", "followers"})
public class Album extends Playlist {
    @JsonIgnore
    private int releaseYear;
    @JsonIgnore
    private String descrition;

    public Album(String name, int time, int releaseYear, String descrition, ArrayList<Song> songs) {
        super(name, time);
        this.releaseYear = releaseYear;
        this.descrition = descrition;
        for (Song s : songs) {
            this.getSongs().add(s.getName());
            this.getSongsfull().add(s);
        }
    }

    public boolean hasSongTwice() {
        for (int i = 0; i < this.getSongsfull().size() - 1; i++) {
            for (int j = i + 1; j < this.getSongsfull().size(); j++) {
                if(this.getSongsfull().get(i).getName().equals(this.getSongsfull().get(j).getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
