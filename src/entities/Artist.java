package entities;

import com.fasterxml.jackson.databind.node.ObjectNode;
import entitycolections.Album;
import lombok.Getter;
import lombok.Setter;
import misc.WrappedVisitor;
import pages.ArtistPage;
import tools.Command;
import tools.DataBase;

import java.util.ArrayList;
import java.util.HashMap;

@Getter @Setter
public final class Artist extends User implements Comparable {
    private ArrayList<Album> albums = new ArrayList<>();

    private ArtistPage artistPage = new ArtistPage(this, this.albums);

    private HashMap<String, Integer> listenedAlbums = new HashMap<>();
    private HashMap<String, Integer> listenedSongs = new HashMap<>();
    private HashMap<String, Integer> fans = new HashMap<>();
    private int listeners = 0;
    private ArrayList<User> subscribers = new ArrayList<>();


    public Artist(final String username, final String city, final int age) {
        super(username, city, age);
    }

    /** checks if this artist has an album with the specified name */
    public boolean hasAlbum(final String name) {
        for (Album album : this.albums) {
            if (album.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /** removes an album if possible and returns the appropriate message */
    public void removeAlbum(final Command cmd, final ObjectNode node) {
        Album searchedAlbum = null;
        for (Album album : this.albums) {
            if (album.getName().equals(cmd.getName())) {
                searchedAlbum = album;
            }
        }
        if (searchedAlbum == null) {
            node.put("message", this.getUsername()
                    + " doesn't have an album with the given name.");
        } else if (searchedAlbum.getNumUsersPlaying() != 0 || searchedAlbum.areAnySongsUsed()) {
            node.put("message", this.getUsername() + " can't delete this album.");
        } else {
            node.put("message", this.getUsername() + " deleted the album successfully.");
            albums.remove(searchedAlbum);
        }
    }

    /** get the number of likes from all the songs from all albums */
    public int allLikes() {
        int numLikes = 0;
        for (Album album : this.albums) {
            numLikes = numLikes + album.getLikes();
        }
        return numLikes;
    }

    @Override
    public int compareTo(final Object o) {
        return ((Artist) o).allLikes() - this.allLikes();
    }

    /** adds an album to this artist and returns the appropriate message */
    public void addAlbum(final Command cmd, final ObjectNode node) {
        Album album = new Album(cmd.getName(), cmd.getTimestamp(), cmd.getReleaseYear(),
                this.getUsername(), cmd.getDescription(), cmd.getSongs());
        if (this.hasAlbum(album.getName())) {
            node.put("message", this.getUsername() + " has another album with the same name.");
        } else if (album.hasSongTwice()) {
            node.put("message", this.getUsername()
                    + " has the same song at least twice in this album.");
        } else {
            albums.add(album);
            DataBase.getDB().getAllSongs().addAll(album.getSongsfull());
            node.put("message", this.getUsername() + " has added new album successfully.");
            for (User user : subscribers) {
                user.getNotifications().add(new Notification("New Album", "New Album from "
                        + getUsername() + "."));
            }
        }
    }

    /** checks if the artist has any content in use by other users */
    public boolean canBeDeleted() {
        int sum = 0;
        for (Album album : albums) {
            sum = sum + album.getNumUsersPlaying();
            for (Song song : album.getSongsfull()) {
                sum = sum + song.getNumUsing();
            }
        }
        if (sum == 0) {
            return true;
        }
        return false;
    }

    /** accepts a wrapped visitor to put the correct wrapped output in node **/
    public void accept(final WrappedVisitor visitor, final ObjectNode node) {
        visitor.visit(this, node);
    }

    /** adds a listened song or increments its number of listens **/
    public void addListenedSong(final Song song) {
        if (listenedSongs.containsKey(song.getName())) {
            Integer numListens = listenedSongs.get(song.getName());
            listenedSongs.replace(song.getName(), numListens + 1);
        } else {
            listenedSongs.put(song.getName(), 1);
        }
    }

    /** adds a listened album or increments its number of listens **/
    public void addListenedAlbum(final Album album) {
        if (listenedAlbums.containsKey(album.getName())) {
            Integer numListens = listenedAlbums.get(album.getName());
            listenedAlbums.replace(album.getName(), numListens + 1);
        } else {
            listenedAlbums.put(album.getName(), 1);
        }
    }

    /** adds a user that listened to something from this artist
     * or increments its number of listens **/
    public void addFan(final User user) {
        if (fans.containsKey(user.getUsername())) {
            Integer numListens = fans.get(user.getUsername());
            fans.replace(user.getUsername(), numListens + 1);
        } else {
            fans.put(user.getUsername(), 1);
        }
    }

    /** checks if this artist has any data to be wrapped **/
    @Override
    public boolean noWrapper() {
        return listenedSongs.isEmpty();
    }

    /** adds or removes a user that subscribed to this artist **/
    public boolean addSub(final User user) {
        if (subscribers.contains(user)) {
            subscribers.remove(user);
            return false;
        }
        subscribers.add(user);
        return true;
    }
}
