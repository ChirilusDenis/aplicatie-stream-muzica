package entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entitycolections.Podcast;
import lombok.Getter;
import pages.Announcement;
import pages.HostPage;
import tools.Command;
import tools.DataBase;

import java.util.ArrayList;

@Getter
public class Host extends User{
    @JsonIgnore
    private ArrayList<Podcast> podcasts= new ArrayList<>();
    @JsonIgnore
    private HostPage hostPage = new HostPage(podcasts);
    public Host(String username, String city, int age) {
        super(username, city, age);
    }

    public boolean hasPodcast(Podcast podcast) {
        for (Podcast p : podcasts) {
            if (p.getName().equals(podcast.getName())) {
                return true;
            }
        }
        return false;
    }

    public void addPodcast(Command cmd, ObjectNode node) {
        Podcast newPodcast = new Podcast(this.getUsername(), cmd.getName(), cmd.getEpisodes());
        if (this.hasPodcast(newPodcast)) {
            node.put("message", this.getUsername() + " has another podcast with the same name.");
        } else if (false) { // check if double episodes
            node.put("message", this.getUsername() + " has the same episode in this podcast.");
        } else {
            this.podcasts.add(newPodcast);
            node.put("message", this.getUsername() + " has added new podcast successfully.");
        }
    }

    public void addAnnouncement(Command cmd, ObjectNode node) {
        if (this.hostPage.hasAnnouncement(cmd.getName())) {
            node.put("message", this.getUsername() + " has already added an announcement with this name");
        } else {
            node.put("message", this.getUsername() + " has successfully added new announcement.");
            this.hostPage.getAnnouncements().add(new Announcement(cmd.getName(), cmd.getDescription()));
        }
    }

    public void removeAnnouncement(Command cmd, ObjectNode node) {
        if (!this.hostPage.hasAnnouncement(cmd.getName())) {
            node.put("message", this.getUsername() + " has no announcement with the given name.");
        } else {
            node.put("message", this.getUsername() + " has successfully deleted the announcement.");
            this.hostPage.getAnnouncements().removeIf((A) -> A.getName().equals(cmd.getName()));
        }
    }

    public boolean hasPodcast(String name) {
        for (Podcast podcast : this.podcasts) {
            if (podcast.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void removePodcast(Command cmd, ObjectNode node) {
        if (!this.hasPodcast(cmd.getName())) {
            node.put("message", this.getUsername() + " doesn't have a podcast with the given name.");
        } else {
            Podcast podcast = DataBase.findPodcast(cmd.getName());
            if (podcast.getNumUsingThis() != 0) {
                node.put("message", this.getUsername() + " can't delete this podcast.");
            } else {
                node.put("message", this.getUsername() + " deleted the podcast successfully.");
                this.podcasts.remove(podcast);
            }
        }
    }

    public boolean canBeDeleted() {
        int sum = 0;
        for (Podcast podcast : podcasts) {
            sum = sum + podcast.getNumUsingThis();
        }
        if (sum == 0) {
            return true;
        }
        return false;
    }
}
