package entitycolections;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import entities.Song;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@JsonIgnoreProperties({"visibility", "followers"})
public class Album extends Playlist implements Comparable{
    @JsonIgnore
    private int releaseYear;
    @JsonIgnore
    private String description = "";

    public Album(String name, int time, int releaseYear, String artist , String description, ArrayList<Song> songs) {
        super(name, time, artist);
        this.releaseYear = releaseYear;
        this.description = description;
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

    @JsonIgnore
    public int getLikes() {
        int numLikes = 0;
        for (Song s : this.getSongsfull()) {
            numLikes = numLikes + s.getNumLikes();
        }
        return numLikes;
    }

    @Override
    public int compareTo(Object o) {
        Album album = (Album) o;
        if (this.getLikes() == album.getLikes()) {
            return this.getName().compareTo(album.getName());
        }
        return this.getLikes() - album.getLikes();
    }
}
