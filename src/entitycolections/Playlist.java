package entitycolections;

import com.fasterxml.jackson.annotation.JsonIgnore;
import entities.Song;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter @Setter
public class Playlist {
    private String name;
    @JsonIgnore
    private String owner;
    @JsonIgnore
    private ArrayList<Song> songsfull = new ArrayList<>();
    private ArrayList<String> songs = new ArrayList<>();
    private String visibility;
    @JsonIgnore
    private ArrayList<String> followedBy = new ArrayList<>();
    @JsonIgnore
    private int timeOfCreation;
    private int followers = 0;

    @JsonIgnore
    private int numUsersPlaying = 0;


    public Playlist(final String name, final int time, final String owner) {
        this.name = name;
        this.owner = owner;
        timeOfCreation = time;
        visibility = "public";
    }

    /** adds a song in playlist if it doesn't contain it already, else it removes it*/
    public boolean addRemove(final Song song) {
        if (songsfull.contains(song)) {
            songsfull.remove(song);
            songs.remove(song.getName());
            return false;
        } else {
            songsfull.add(song);
            songs.add(song.getName());
            return true;
        }
    }

    /** switches the visibility of the current playlist*/
    public boolean switchVis() {
        if (visibility.equals("public")) {
            visibility = "private";
            return false;
        } else {
            visibility = "public";
            return true;
        }
    }

    /** increments or decrements the follow field depending on the passed value, either 1 or -1 */
    public void follow(final int followValue) {
        this.followers = this.followers + followValue;
    }

    /** returns the number of likes of all the songs from this playlist */
    @JsonIgnore
    public int getAllLikes() {
        int numLikes = 0;
        for (Song s : songsfull) {
            numLikes = numLikes + s.getNumLikes();
        }
        return numLikes;
    }

    /** increments or decrements the number of users using this playlist */
    public void usingThis(final int usingValue) {
        numUsersPlaying = numUsersPlaying + usingValue;
    }
}
