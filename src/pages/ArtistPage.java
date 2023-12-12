package pages;

import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.User;
import entitycolections.Album;
import lombok.Getter;
import lombok.Setter;
import misc.Date;
import tools.Command;

import java.util.ArrayList;

@Getter @Setter
public class ArtistPage implements Visitable{
    private ArrayList<Merch> merches = new ArrayList<>();
    private ArrayList<Album> albums;
    private ArrayList<Event> events = new ArrayList<>();

    private User owner;

    public ArtistPage(User owner, ArrayList<Album> albums) {
        this.albums = albums;
        this.owner = owner;
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

    public void addMerch(Command cmd, ObjectNode node) {
        if (hasMerch(cmd.getName())) {
            node.put("message", cmd.getUsername() + " has merchandise with the same name.");
        } else if (cmd.getPrice() < 0) {
            node.put("message", "Price for merchandise can not be negative.");
        } else {
            merches.add(new Merch(cmd.getName(), cmd.getDescription(), cmd.getPrice()));
            node.put("message", cmd.getUsername() + " has added new merchandise successfully.");
        }
    }

    public void addEvent(Command cmd, ObjectNode node) {
        if (this.hasEvent(cmd.getName())) {
            node.put("message", cmd.getUsername() + " has another event with the same name.");
        } else if (!Date.isDateOK(cmd.getDate())) {// check date validity
            node.put("message", "Event for " + cmd.getUsername() + " does not have a valid date.");
        } else {
            events.add(new Event(cmd.getName(), cmd.getDate(), cmd.getDescription()));
            node.put("message", cmd.getUsername() + " has added new event successfully.");
        }
    }

    @Override
    public String accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
