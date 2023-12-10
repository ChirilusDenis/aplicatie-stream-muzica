package pages;

import entities.Song;
import entitycolections.Playlist;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Set;

@Getter
public class Page {
    private ArrayList<Song> likedSongs = new ArrayList<>();
    private ArrayList<Playlist> followedPlaylists = new ArrayList<>();

    public void refreshPage(ArrayList<Song> songs, ArrayList<Playlist> playlists) {
        likedSongs = new ArrayList<>(Set.copyOf(songs));
        followedPlaylists = new ArrayList<>(Set.copyOf(playlists));

        likedSongs.sort((s1, s2) -> s2.getNumLikes() - s1.getNumLikes());
        followedPlaylists.sort((p1, p2) -> p2.getAllLikes() - p1.getAllLikes());
    }

    @Override
    public String toString() {
        String songs = "";
        for (Song s : likedSongs) {
            songs = songs + s.getName() + ", ";
        }
        if (!songs.equals("")) {
            songs = songs.substring(0, songs.length() - 2);
        }
        String play = "";
        for (Playlist p : followedPlaylists) {
            play = play + p.getName() + ", ";
        }
        if (!play.equals("")) {
            play = play.substring(0, play.length() - 2);
        }
        return "Liked songs:\n\t[" + songs + "]\n\nFollowed playlists:\n\t[" + play + "]";
    }

    public String fullPageToString() {
        String songs = "";
        for (Song s : likedSongs) {
            songs = songs + s.getName() + " - " + s.getArtist() + ", ";
        }
        if (!songs.equals("")) {
            songs = songs.substring(0, songs.length() - 2);
        }
        String play = "";
        for (Playlist p : followedPlaylists) {
            play = play + p.getName() + " - " + p.getOwner() + ", ";
        }
        if (!play.equals("")) {
            play = play.substring(0, play.length() - 2);
        }
        return "Liked songs:\n\t[" + songs + "]\n\nFollowed playlists:\n\t[" + play + "]";
    }
}
