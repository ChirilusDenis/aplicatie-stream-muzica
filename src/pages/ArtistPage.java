package pages;

import com.fasterxml.jackson.databind.node.ObjectNode;
import entitycolections.Album;
import lombok.Getter;
import lombok.Setter;
import tools.Command;

import java.util.ArrayList;

@Getter @Setter
public class ArtistPage {
    private ArrayList<Merch> merches = new ArrayList<>();
    private ArrayList<Album> albums;
    private ArrayList<Event> events = new ArrayList<>();

    public ArtistPage(ArrayList<Album> albums) {
        this.albums = albums;
    }

    @Override
    public String toString() {
        String albs ="";
        for (Album a : this.albums) {
            albs = albs + a.getName() + ", ";
        }
        if(!albs.equals("")) {
            albs = albs.substring(0, albs.length() - 2);
        }

        String merch ="";
        for (Merch a : this.merches) {
            merch = merch + a.getName() + " - " + a.getPrice() + ":\n\t" + a.getDescription() + ", ";
        }
        if(!merch.equals("")) {
            merch = merch.substring(0, merch.length() - 2);
        }

        String event = "";
        for (Event e : this.events) {
            event = event + e.getName() + " - " + e.getDate() + ":\n\t" + e.getDescription() + ", ";
        }
        if(!event.equals("")) {
            event = event.substring(0, event.length() - 2);
        }
        return "Albums:\n\t[" + albs + "]\n\nMerch:\n\t[" + merch + "]\n\nEvents:\n\t[" + event + "]";
    }

    public boolean hasMerch(String name) {
        for (Merch m : merches) {
            if(m.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEvent(String name) {
        for (Event e : events) {
            if(e.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void removeEvent(Command cmd, ObjectNode node) {
        if (!this.hasEvent(cmd.getName())) {
            node.put("message", cmd.getUsername() + " doesn't have an event with the given name.");
        } else {
            node.put("message", cmd.getUsername() + " deleted the event successfully.");
            this.events.removeIf((e) -> e.getName().equals(cmd.getName()));
        }
    }
}
