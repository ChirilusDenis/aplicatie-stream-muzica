package entities;

import com.fasterxml.jackson.databind.node.ObjectNode;
import entitycolections.Album;
import lombok.Getter;
import lombok.Setter;
import pages.ArtistPage;
import pages.Event;
import pages.Merch;
import tools.Command;

import java.util.ArrayList;

@Getter @Setter
public class Artist extends User implements Comparable{
    private ArrayList<Album> albums = new ArrayList<>();

    private ArtistPage artistPage = new ArtistPage(this.albums);

    public Artist(String username, String city, int age) {
        super(username, city, age);
    }

    public boolean hasAlbum(String name) {
        for (Album album : this.albums) {
            if (album.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String printPage() {
        return artistPage.toString();
    }

    public void removeAlbum(Command cmd, ObjectNode node) {
        Album searchedAlbum = null;
        for (Album album : this.albums) {
            if (album.getName().equals(cmd.getName())) {
                searchedAlbum = album;
            }
        }
        if (searchedAlbum == null) {
            node.put("message", this.getUsername() + " doesn't have an album with the given name.");
        } else if (searchedAlbum.getNumUsersPlaying() != 0 || searchedAlbum.areAnySongsUsed()) {
            node.put("message", this.getUsername() + " can't delete this album.");
        } else {
            node.put("message", this.getUsername() + " deleted the album successfully.");
            albums.remove(searchedAlbum);
        }
    }

    public int allLikes() {
        int numLikes = 0;
        for (Album a : this.albums) {
            numLikes = numLikes + a.getLikes();
        }
        return numLikes;
    }

    @Override
    public int compareTo(Object o) {
        return this.allLikes() - ((Artist) o).allLikes();
    }

    public void addAlbum(Command cmd, ObjectNode node) {
        Album album = new Album(cmd.getName(), cmd.getTimestamp(), cmd.getReleaseYear(), this.getUsername(),
                cmd.getDescription(),cmd.getSongs());
        if (this.hasAlbum(album.getName())) {
            node.put("message", this.getUsername() + " has another album with the same name.");
        } else if (album.hasSongTwice()) {
            node.put("message", this.getUsername()
                    + " has the same song at least twice in this album.");
        } else {
            albums.add(album);
            node.put("message", this.getUsername() + " has added new album successfully.");
        }
    }

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
        } else { return false;}
    }
}
