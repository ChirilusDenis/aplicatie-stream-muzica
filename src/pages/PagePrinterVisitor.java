package pages;

import entities.PodcastEpisode;
import entities.Song;
import entitycolections.Album;
import entitycolections.Playlist;
import entitycolections.Podcast;

public final class PagePrinterVisitor implements Visitor {

    @Override
    public String visit(final Page page) {
        String songs = "";
        for (Song s : page.getLikedSongs()) {
            songs = songs + s.getName() + ", ";
        }
        if (!songs.equals("")) {
            songs = songs.substring(0, songs.length() - 2);
        }
        String play = "";
        for (Playlist p : page.getFollowedPlaylists()) {
            play = play + p.getName() + ", ";
        }
        if (!play.equals("")) {
            play = play.substring(0, play.length() - 2);
        }
        String recomPLay = "";
        for (Playlist p : page.getRecomendedPLaylists()) {
            recomPLay = recomPLay + p.getName() + ", ";
        }
        if (!recomPLay.equals("")) {
            recomPLay = recomPLay.substring(0, recomPLay.length() - 2);
        }
        String recomSongs = "";
        for (Song s : page.getRecomendedSongs()) {
            recomSongs = recomSongs + s.getName() + ", ";
        }
        if (!recomSongs.equals("")) {
            recomSongs = recomSongs.substring(0, recomSongs.length() - 2);
        }
        return "Liked songs:\n\t[" + songs + "]\n\nFollowed playlists:\n\t[" + play + "]"
                + "\n\nSong recommendations:\n\t[" + recomSongs
                + "]\n\nPlaylists recommendations:\n\t[" + recomPLay + "]";
    }

    @Override
    public String visit(final LikedContentPage page) {
        String songs = "";
        for (Song s : page.getLikedSongs()) {
            songs = songs + s.getName() + " - " + s.getArtist() + ", ";
        }
        if (!songs.equals("")) {
            songs = songs.substring(0, songs.length() - 2);
        }
        String play = "";
        for (Playlist p : page.getFollowedPlaylists()) {
            play = play + p.getName() + " - " + p.getOwner() + ", ";
        }
        if (!play.equals("")) {
            play = play.substring(0, play.length() - 2);
        }
        return "Liked songs:\n\t[" + songs + "]\n\nFollowed playlists:\n\t[" + play + "]";
    }

    @Override
    public String visit(final HostPage page) {
        String podcastStr = "";
        for (Podcast p : page.getPodcasts()) {
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
        for (Announcement announcement : page.getAnnouncements()) {
            announcementStr = announcementStr + announcement.getName() + ":\n\t"
                    + announcement.getDescription() + ", ";
        }
        if (!announcementStr.equals("")) {
            announcementStr = announcementStr.substring(0, announcementStr.length() - 2);
        }
        return "Podcasts:\n\t[" + podcastStr + "]\n\nAnnouncements:\n\t[" + announcementStr + "\n]";
    }

    @Override
    public String visit(final ArtistPage page) {
        String albs = "";
        for (Album a : page.getAlbums()) {
            albs = albs + a.getName() + ", ";
        }
        if (!albs.equals("")) {
            albs = albs.substring(0, albs.length() - 2);
        }

        String merch = "";
        for (Merch a : page.getMerches()) {
            merch = merch + a.getName() + " - " + a.getPrice()
                    + ":\n\t" + a.getDescription() + ", ";
        }
        if (!merch.equals("")) {
            merch = merch.substring(0, merch.length() - 2);
        }

        String event = "";
        for (Event e : page.getEvents()) {
            event = event + e.getName() + " - " + e.getDate()
                    + ":\n\t" + e.getDescription() + ", ";
        }
        if (!event.equals("")) {
            event = event.substring(0, event.length() - 2);
        }
        return "Albums:\n\t[" + albs + "]\n\nMerch:\n\t["
                + merch + "]\n\nEvents:\n\t[" + event + "]";
    }
}
