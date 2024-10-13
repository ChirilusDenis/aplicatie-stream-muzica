package pages;

import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.Artist;
import entities.Notification;
import entities.Song;
import entities.User;
import entitycolections.Album;
import entitycolections.Playlist;
import lombok.Getter;
import lombok.Setter;
import misc.Date;
import tools.Command;

import java.util.ArrayList;

@Getter @Setter
public class ArtistPage implements Visitable {
    private ArrayList<Merch> merches = new ArrayList<>();
    private ArrayList<Album> albums;
    private ArrayList<Event> events = new ArrayList<>();

    private User owner;

    public ArtistPage(final User owner, final ArrayList<Album> albums) {
        this.albums = albums;
        this.owner = owner;
    }

    /** checks if this page has merch with the specified name */
    public boolean hasMerch(final String name) {
        for (Merch merch : merches) {
            if (merch.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /** checks if this page has an event with the specified name */
    public boolean hasEvent(final String name) {
        for (Event event : events) {
            if (event.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /** removes a specified event and returns the appropriate message */
    public void removeEvent(final Command cmd, final ObjectNode node) {
        if (!this.hasEvent(cmd.getName())) {
            node.put("message", cmd.getUsername() + " doesn't have an event with the given name.");
        } else {
            node.put("message", cmd.getUsername() + " deleted the event successfully.");
            this.events.removeIf((e) -> e.getName().equals(cmd.getName()));
        }
    }

    /** adds a new merch if possible and returns the appropriate message */
    public void addMerch(final Command cmd, final ObjectNode node) {
        if (hasMerch(cmd.getName())) {
            node.put("message", cmd.getUsername() + " has merchandise with the same name.");
        } else if (cmd.getPrice() < 0) {
            node.put("message", "Price for merchandise can not be negative.");
        } else {
            merches.add(new Merch(cmd.getName(), cmd.getDescription(), cmd.getPrice()));
            node.put("message", cmd.getUsername() + " has added new merchandise successfully.");
            ((Artist) owner).getSubject().notifyAll("New Merchandise",
                        "New Merchandise from " + owner.getUsername() + ".");
        }
    }

    /** adds a new event if possible and returns the appropriate message */
    public void addEvent(final Command cmd, final ObjectNode node) {
        if (this.hasEvent(cmd.getName())) {
            node.put("message", cmd.getUsername() + " has another event with the same name.");
        } else if (!Date.isDateOK(cmd.getDate())) {
            node.put("message", "Event for " + cmd.getUsername() + " does not have a valid date.");
        } else {
            events.add(new Event(cmd.getName(), cmd.getDate(), cmd.getDescription()));
            node.put("message", cmd.getUsername() + " has added new event successfully.");
            ((Artist) owner).getSubject().notifyAll("New Event", "New Event from "
                    + owner.getUsername() + ".");
        }
    }

    /** accepts a visitor for page printing */
    @Override
    public String accept(final Visitor visitor) {
        return visitor.visit(this);
    }

    /** used for refresh page generalization for all page types **/
    public void refreshPage(final ArrayList<Song> songs, final ArrayList<Playlist> playlists,
                            final ArrayList<Song> recomendedSongs,
                            final ArrayList<Playlist> recomendedPLaylists) {

    }
}
