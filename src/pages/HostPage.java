package pages;

import entities.PodcastEpisode;
import entitycolections.Podcast;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class HostPage {
    private ArrayList<Announcement> announcements = new ArrayList<>();
    private ArrayList<Podcast> podcasts;

    public HostPage(ArrayList<Podcast> podcasts) {
        this.podcasts = podcasts;
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
    public String toString() {
        String podcastStr = "";
        for (Podcast p : this.podcasts) {
            podcastStr = podcastStr + p.getName() + ":\n\t[";
            for (PodcastEpisode e : p.getEpisodesFull()) {
                podcastStr = podcastStr + e.getName() + " - " + e.getDescription() + ", ";
            }
            if (!podcastStr.equals("")) {
                podcastStr = podcastStr.substring(0, podcastStr.length() - 2);
            }
            podcastStr = podcastStr + "]\n, ";
        }
        if (!podcastStr.equals("")) {
            podcastStr = podcastStr.substring(0, podcastStr.length() - 2);
        }

        String announcementStr = "";
        for (Announcement announcement : this.announcements) {
            announcementStr = announcementStr + announcement.getName() + ":\n\t" + announcement.getDescription() + ", ";
        }
        if (!announcementStr.equals("")) {
            announcementStr = announcementStr.substring(0, announcementStr.length() - 2);
        }
        return "Podcasts:\n\t[" + podcastStr + "]\n\nAnnouncements:\n\t[" + announcementStr + "\n]";
    }

}
