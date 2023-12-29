package pages;

import entities.Song;
import entitycolections.Playlist;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public final class Page implements Visitable {
    private ArrayList<Song> likedSongs = new ArrayList<>();
    private ArrayList<Playlist> followedPlaylists = new ArrayList<>();
    private ArrayList<Playlist> recomendedPLaylists = new ArrayList<>();
    private ArrayList<Song> recomendedSongs = new ArrayList<>();
    private final int five = 5;

    /** refreshed the liked songs and followed playlists from a user */
    public void refreshPage(final ArrayList<Song> songs, final ArrayList<Playlist> playlists,
                             final ArrayList<Song> recomendedSongs,
                            final ArrayList<Playlist> recomendedPLaylists) {
        likedSongs = new ArrayList<>(List.copyOf(songs));
        followedPlaylists = new ArrayList<>(List.copyOf(playlists));

        likedSongs.sort((s1, s2) -> s2.getNumLikes() - s1.getNumLikes());
        followedPlaylists.sort((p1, p2) -> p2.getAllLikes() - p1.getAllLikes());

        while (likedSongs.size() > five) {
            likedSongs.remove(five);
        }
        while (followedPlaylists.size() > five) {
            followedPlaylists.remove(five);
        }
        this.recomendedPLaylists.addAll(recomendedPLaylists);
        this.recomendedSongs.addAll(recomendedSongs);
    }

    @Override
    public String accept(final Visitor visitor) {
        return visitor.visit(this);
    }
}
