package entitycolections;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import entities.Song;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@JsonIgnoreProperties({"visibility", "followers"})
public final class Album extends Playlist implements Comparable {
    @JsonIgnore
    private int releaseYear;
    @JsonIgnore
    private String description = "";

    public Album(final String name, final int time,
                 final int releaseYear, final String artist,
                 final String description, final ArrayList<Song> songs) {
        super(name, time, artist);
        this.releaseYear = releaseYear;
        this.description = description;
        for (Song song : songs) {
            this.getSongs().add(song.getName());
            this.getSongsfull().add(song);
        }
    }

    /** checks if the album has 2 songs with the same name */
    public boolean hasSongTwice() {
        for (int i = 0; i < this.getSongsfull().size() - 1; i++) {
            for (int j = i + 1; j < this.getSongsfull().size(); j++) {
                if (this.getSongsfull().get(i).getName().equals(
                        this.getSongsfull().get(j).getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /** returns the number of likes of all songs from this album */
    @JsonIgnore
    public int getLikes() {
        int numLikes = 0;
        for (Song s : this.getSongsfull()) {
            numLikes = numLikes + s.getNumLikes();
        }
        return numLikes;
    }

    @Override
    public int compareTo(final Object o) {
        Album album = (Album) o;
        if (this.getLikes() == album.getLikes()) {
            return this.getName().compareTo(album.getName());
        }
        return album.getLikes() - this.getLikes();
    }

    /** checks if any songs are used by any user */
    public boolean areAnySongsUsed() {
        for (Song song : this.getSongsfull()) {
            if (song.getNumUsing() != 0) {
                return true;
            }
        }
        return false;
    }
}
