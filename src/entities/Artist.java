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

    public void addMerch(Command cmd) {
        artistPage.getMerches().add(new Merch(cmd.getName(), cmd.getDescription(), cmd.getPrice()));
    }

    public void addEvent(Command cmd) {
        artistPage.getEvents().add(new Event(cmd.getName(), cmd.getDate(), cmd.getDescription()));
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
        } else if (searchedAlbum.getNumUsersPlaying() != 0) {
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
}
