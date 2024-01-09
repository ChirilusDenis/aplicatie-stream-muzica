package entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entitycolections.Podcast;
import lombok.Getter;
import lombok.Setter;
import misc.WrappedVisitor;
import pages.Announcement;
import pages.HostPage;
import tools.Command;
import tools.DataBase;

import java.util.ArrayList;
import java.util.HashMap;

@Getter @Setter
public final class Host extends User {
    @JsonIgnore
    private ArrayList<Podcast> podcasts = new ArrayList<>();
    @JsonIgnore
    private HostPage hostPage = new HostPage(this, podcasts);

    private final HashMap<String, Integer> listenedEpisodes = new HashMap<>();
    private final ArrayList<User> fans = new ArrayList<>();
    private int listeners = 0;
    private ArrayList<User> subscribers = new ArrayList<>();

    public Host(final String username, final String city, final int age) {
        super(username, city, age);
    }

    /** checks if the host has a specified podcast */
    public boolean hasPodcast(final Podcast podcast) {
        for (Podcast p : podcasts) {
            if (p.getName().equals(podcast.getName())) {
                return true;
            }
        }
        return false;
    }

    /** adds a podcast if possible and returns the appropriate message */
    public void addPodcast(final Command cmd, final ObjectNode node) {
        Podcast newPodcast = new Podcast(this.getUsername(), cmd.getName(), cmd.getEpisodes());
        if (this.hasPodcast(newPodcast)) {
            node.put("message", this.getUsername()
                    + " has another podcast with the same name.");
        } else if (newPodcast.hasEpisodeTwice()) {
            node.put("message", this.getUsername() + " has the same episode in this podcast.");
        } else {
            this.podcasts.add(newPodcast);
            node.put("message", this.getUsername() + " has added new podcast successfully.");
            for (User user : subscribers) {
                user.updateNotif("New Podcast", "New Podcast from "
                        + getUsername() + ".");
//                user.getNotifications().add(new Notification("New Podcast", "New Podcast from "
//                        + getUsername() + "."));
            }
        }
    }

    /** adds an announcement if possible and returns the appropriate message */
    public void addAnnouncement(final Command cmd, final ObjectNode node) {
        if (this.hostPage.hasAnnouncement(cmd.getName())) {
            node.put("message", this.getUsername()
                    + " has already added an announcement with this name");
        } else {
            node.put("message", this.getUsername() + " has successfully added new announcement.");
            this.hostPage.getAnnouncements().add(
                    new Announcement(cmd.getName(), cmd.getDescription()));
            for (User user : subscribers) {
                user.updateNotif("New Announcement",
                        "New Announcement from " + getUsername() + ".");
//                user.getNotifications().add(new Notification("New Announcement",
//                        "New Announcement from " + getUsername() + "."));
            }
        }
    }

    /** removes a certain podcast if possible and returns the appropriate message */
    public void removeAnnouncement(final Command cmd, final ObjectNode node) {
        if (!this.hostPage.hasAnnouncement(cmd.getName())) {
            node.put("message", this.getUsername() + " has no announcement with the given name.");
        } else {
            node.put("message", this.getUsername()
                    + " has successfully deleted the announcement.");
            this.hostPage.getAnnouncements().removeIf((A) -> A.getName().equals(cmd.getName()));
        }
    }

    /** checks if the host has a podcast with the specified name */
    public boolean hasPodcast(final String name) {
        for (Podcast podcast : this.podcasts) {
            if (podcast.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /** removes a podcast and returns the appropriate message */
    public void removePodcast(final Command cmd, final ObjectNode node) {
        if (!this.hasPodcast(cmd.getName())) {
            node.put("message", this.getUsername()
                    + " doesn't have a podcast with the given name.");
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

    /** checks if this host has any content in use by other users */
    public boolean canBeDeleted() {
        int sumOfUses = 0;
        for (Podcast podcast : podcasts) {
            sumOfUses = sumOfUses + podcast.getNumUsingThis();
        }
        if (sumOfUses == 0) {
            return true;
        }
        return false;
    }

    /** accepts a visitor to construct the wrapped output for this host **/
    public void accept(final WrappedVisitor visitor, final ObjectNode node) {
        visitor.visit(this, node);
    }

    /** adds a listened episode or increments its number of listens **/
    public void addListenedEpisodes(final PodcastEpisode episode) {
        if (listenedEpisodes.containsKey(episode.getName())) {
            Integer numListens = listenedEpisodes.get(episode.getName());
            listenedEpisodes.replace(episode.getName(), numListens + 1);
        } else {
            listenedEpisodes.put(episode.getName(), 1);
        }
    }

    /** checks if there's any data for this host to wrap **/
    @Override
    public boolean noWrapper() {
        return listenedEpisodes.isEmpty();
    }

    /** adds a user that listened to an episode from this host
     * or increases its listen count **/
    public void addFan(final User user) {
        if (!fans.contains(user)) {
            fans.add(user);
        }
    }

    /** adds or removes a subscriber to this host **/
    public boolean addSub(final User user) {
        if (subscribers.contains(user)) {
            subscribers.remove(user);
            return false;
        }
        subscribers.add(user);
        return true;
    }
}
