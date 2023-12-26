package pages;

import entities.User;
import entitycolections.Podcast;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public final class HostPage implements  Visitable {
    private ArrayList<Announcement> announcements = new ArrayList<>();
    private ArrayList<Podcast> podcasts;

    private User owner;

    public HostPage(final User owner, final ArrayList<Podcast> podcasts) {
        this.podcasts = podcasts;
        this.owner = owner;
    }

    /** checks if there is an announcement with the specified name in this page */
    public boolean hasAnnouncement(final String name) {
        for (Announcement announcement : this.announcements) {
            if (announcement.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /** accepts a visitor for page printing */
    @Override
    public String accept(final Visitor visitor) {
        return visitor.visit(this);
    }
}
