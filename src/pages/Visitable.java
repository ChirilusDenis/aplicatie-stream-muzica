package pages;

import entities.Song;
import entitycolections.Playlist;

import java.util.ArrayList;

public interface Visitable {
    /** accepts a page printer visitor */
    String accept(Visitor visitor);
    /** refreshes the content of the page to the most recent one **/
    void refreshPage(ArrayList<Song> songs, ArrayList<Playlist> playlists,
                ArrayList<Song> recomendedSongs, ArrayList<Playlist> recomendedPLaylists);
}
