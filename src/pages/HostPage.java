package pages;

import entities.PodcastEpisode;
import entities.User;
import entitycolections.Podcast;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class HostPage implements  Visitable{
    private ArrayList<Announcement> announcements = new ArrayList<>();
    private ArrayList<Podcast> podcasts;

    private User owner;

    public HostPage(User owner, ArrayList<Podcast> podcasts) {
        this.podcasts = podcasts;
        this.owner = owner;
    }

    public boolean hasAnnouncement(String name) {
        for (Announcement announcement : this.announcements) {
            if (announcement.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
