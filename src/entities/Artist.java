package entities;

import entitycolections.Album;
import lombok.Getter;
import lombok.Setter;
import pages.ArtistPage;
import pages.Event;
import pages.Merch;
import tools.Command;

import java.util.ArrayList;

@Getter @Setter
public class Artist extends User {
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

}
