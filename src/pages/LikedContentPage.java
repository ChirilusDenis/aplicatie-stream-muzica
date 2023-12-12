package pages;

import entities.Song;
import entitycolections.Playlist;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
public class LikedContentPage implements Visitable{
    private ArrayList<Song> likedSongs = new ArrayList<>();
    private ArrayList<Playlist> followedPlaylists = new ArrayList<>();

    public void refreshPage(ArrayList<Song> songs, ArrayList<Playlist> playlists) {
        likedSongs = new ArrayList<>(List.copyOf(songs));
        followedPlaylists = new ArrayList<>(List.copyOf(playlists));

//        likedSongs.sort((s1, s2) -> {if (s1.getNumLikes() == s2.getNumLikes()) {
//            return s2.getName().compareTo(s1.getName());
//        }
//            return s2.getNumLikes() - s1.getNumLikes();});
//        followedPlaylists.sort((p1, p2) -> p2.getAllLikes() - p1.getAllLikes());
    }

    @Override
    public String accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
