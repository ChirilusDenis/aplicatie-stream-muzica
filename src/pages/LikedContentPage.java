package pages;

import entities.Song;
import entitycolections.Playlist;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class LikedContentPage implements Visitable {
    private ArrayList<Song> likedSongs = new ArrayList<>();
    private ArrayList<Playlist> followedPlaylists = new ArrayList<>();

    /** refreshes the liked songs and followed playlists from its user */
    public void refreshPage(final ArrayList<Song> songs, final ArrayList<Playlist> playlists,
                            final ArrayList<Song> recomendedSongs,
                            final ArrayList<Playlist> recomendedPLaylists) {
        likedSongs = new ArrayList<>(List.copyOf(songs));
        followedPlaylists = new ArrayList<>(List.copyOf(playlists));
    }

    @Override
    public String accept(final Visitor visitor) {
        return visitor.visit(this);
    }
}
