package entities;


import fileio.input.SongInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter @Setter
public final class Song extends AudioFile {
    private String album;
    private ArrayList<String> tags;
    private String lyrics;
    private String genre;
    private int releaseYear;
    private String artist;
    private int numLikes = 0;

    private int numUsing = 0;

    private Double profit = 0.0;


    /** add like adds or removes a like, depending on the value passed(1 or -1)*/
    public void addLike(final int likeValue) {
        this.numLikes += likeValue;
    }

    public Song() {
    }

    public Song(final SongInput song) {
        this.setName(song.getName());
        this.tags = new ArrayList<>(song.getTags());
        this.album = song.getAlbum();
        this.artist = song.getArtist();
        this.lyrics = song.getLyrics();
        this.genre = song.getGenre();
        this.releaseYear = song.getReleaseYear();
        this.setDuration(song.getDuration());
    }

    /** increments or decrements the number of users using this */
    public void usingThis(final int usingValue) {
        this.numUsing = this.numUsing + usingValue;
    }
}
