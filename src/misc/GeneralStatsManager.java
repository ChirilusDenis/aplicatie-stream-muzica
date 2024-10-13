package misc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.Artist;
import entities.Song;
import entities.User;
import entitycolections.Album;
import entitycolections.Playlist;
import tools.DataBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GeneralStatsManager {
    private static ArrayList<String> out = new ArrayList<>();
    private static int five = 5;

    private GeneralStatsManager() {
    }


    /** sorts the songs from the library and returns the names of the top 5
     * liked songs*/
    public static ArrayList<String> getTop5Songs() {

        ArrayList<Song> songs = new ArrayList<>(List.copyOf(DataBase.getDB().getAllSongs()));
        out.clear();

        songs.sort((s1, s2) -> s2.getNumLikes() - s1.getNumLikes());

        for (int i = 0; i < five; i++) {
            out.add(songs.get(i).getName());
        }

        return out;
    }

    /** composes an array of all public playlists and sorts them by number of followers
     * returns the names of the top 5 followed playlists*/
    public static ArrayList<String> getTop5Playlists() {

        ArrayList<Playlist> playlists = new ArrayList<>();
        out.clear();

        for (User u : DataBase.getDB().getUsers()) {
            for (Playlist p : u.getPlaylists()) {
                if (p.getVisibility().equals("public")) {
                    playlists.add(p);
                }
            }
        }

        playlists.sort((p1, p2) -> {
            if (p1.getFollowers() == p2.getFollowers()) {
                return p1.getTimeOfCreation() - p2.getTimeOfCreation();
            } else {
                return p2.getFollowers() - p1.getFollowers();
            }
        }
        );

        for (int i = 0; i < five && i < playlists.size(); i++) {
            out.add(playlists.get(i).getName());
        }
        return out;
    }

    /** composes an array of the 5 albums with the most number of liked songs */
    public static void getTop5Albums(final ObjectNode node, final ObjectMapper objectMapper) {
        ArrayList<Album> albums = new ArrayList<>();
        ArrayList<String> albumName = new ArrayList<>();
        for (Artist artist : DataBase.getDB().getArtists()) {
            albums.addAll(artist.getAlbums());
        }
        Collections.sort(albums);

        for (int i = 0; i < five && i < albums.size(); i++) {
            albumName.add(albums.get(i).getName());
        }

        ArrayNode nameNodes = objectMapper.valueToTree(albumName);
        node.remove("user");
        node.putArray("result").addAll(nameNodes);
    }

    /** creates a array with the 5 artists with the most number of liked songs */
    public static void getTop5Artists(final ObjectNode node, final ObjectMapper objectMapper) {
        ArrayList<Artist> artists = new ArrayList<>();
        ArrayList<String> artistName = new ArrayList<>();
        artists.addAll(DataBase.getDB().getArtists());
        Collections.sort(artists);

        for (int i = 0; i < five && i < artists.size(); i++) {
            artistName.add(artists.get(i).getUsername());
        }
        ArrayNode top5Names = objectMapper.valueToTree(artistName);
        node.remove("user");
        node.putArray("result").addAll(top5Names);
    }
}
