package search;

import java.util.ArrayList;

import entities.Artist;
import entities.Host;
import entities.Song;
import entitycolections.Album;
import entitycolections.Playlist;
import entitycolections.Podcast;
import lombok.Getter;
import lombok.Setter;
import tools.Command;
import tools.DataBase;
import entities.User;
@Getter @Setter
public final class SearchBar {
    private ArrayList<String> searched = new ArrayList<>();
    private String selected = "";
    private int whatWasSearched = -1;
    private int whatWasSelected = -1;
    private int five = 5;

    private ArrayList<User> sourceUsers = new ArrayList<>();
    private User selectedSourceUser = null;

    /** clears the searching results */
    public void clear() {
        this.searched.clear();
        this.selected = "";
        this.whatWasSearched = -1;
        this.whatWasSelected = -1;
        this.sourceUsers.clear();
        this.selectedSourceUser = null;
    }

    /** searches songs, playlists, or podcasts, by the filters in the command passed*/
    public void search(final Command cmd) {
        this.clear();
        if (cmd.getType().equals("song")) {
            this.whatWasSearched = 0;
            this.searchSong(cmd, DataBase.getDB().getAllSongs());
//            for (Artist artist : DataBase.getDB().getArtists()) {
//                for (Album album : artist.getAlbums()) {
//                    this.searchSong(cmd, album.getSongsfull());
//                }
//            }
        }
        if (cmd.getType().equals("podcast")) {
            this.whatWasSearched = 2;
            this.searchPodcast(cmd, DataBase.getDB().getPodcasts());
            for (Host host : DataBase.getDB().getHosts()) {
                this.searchPodcast(cmd, host.getPodcasts());
            }
        }
        if (cmd.getType().equals("playlist")) {
            this.whatWasSearched = 1;
            this.searchPlaylist(cmd);
        }
        if (cmd.getType().equals("artist")) {
            this.whatWasSearched = 3;
            this.searchArtist(cmd);
        }
        if (cmd.getType().equals("album")) {
            this.whatWasSearched = 1;
            this.searchAlbum(cmd);
        }
        if (cmd.getType().equals("host")) {
            this.whatWasSearched = 4;
            this.searchHost(cmd);
        }
    }

    /** searches 5 songs with the applied filters passed byt the command*/
    public void searchSong(final Command cmd, final ArrayList<Song> songs) {
        for (Song song : songs) {
            boolean add = true;

//            if (song.getName().indexOf(cmd.getFilters().getName()) != 0) {
//                add = false;
//            }
            if (cmd.getFilters().getName().length() > song.getName().length()) {
                add = false;
            } else if (!cmd.getFilters().getName().equalsIgnoreCase(
                    song.getName().substring(0, cmd.getFilters().getName().length()))) {
                add = false;
            }

            if (!song.getAlbum().contains(cmd.getFilters().getAlbum())) {
                add = false;
            }

            if (!song.getLyrics().toLowerCase().contains(
                    cmd.getFilters().getLyrics().toLowerCase())) {
                add = false;
            }

            if (!cmd.getFilters().getGenre().isEmpty()) {
                if (!song.getGenre().equalsIgnoreCase(cmd.getFilters().getGenre())) {
                    add = false;
                }
            }

            if (!cmd.getFilters().getArtist().isEmpty()) {
                if (!song.getArtist().contentEquals(cmd.getFilters().getArtist())) {
                    add = false;
                }
            }

            if (!cmd.getFilters().getTags().isEmpty()) {
                if (!song.getTags().containsAll(cmd.getFilters().getTags())) {
                    add = false;
                }
            }

            if (cmd.getFilters().getReleaseYear().contains("<")) {
                if (song.getReleaseYear()
                        > Integer.parseInt(cmd.getFilters().getReleaseYear().substring(1))) {
                    add = false;
                }
            }

            if (cmd.getFilters().getReleaseYear().contains(">")) {
                if (song.getReleaseYear()
                        < Integer.parseInt(cmd.getFilters().getReleaseYear().substring(1))) {
                    add = false;
                }
            }

            if (add && searched.size() < five) {
                searched.add(song.getName());
            }
        }
    }

    /** searches 5 podcasts with the applied filters passed by command*/
    public void searchPodcast(final Command cmd, final ArrayList<Podcast> podcasts) {
        for (Podcast podcast : podcasts) {
            boolean add = true;

            if (!podcast.getName().contains(cmd.getFilters().getName())) {
                add = false;
            }

            if (!podcast.getOwner().contains(cmd.getFilters().getOwner())) {
                add = false;
            }

            if (add && searched.size() < five) {
                searched.add(podcast.getName());
            }
        }
    }

    /** searches 5 playlists with the applied filters passed by command*/
    public void searchPlaylist(final Command cmd) {
        searched.clear();
        selected = "";
        for (User user : DataBase.getDB().getUsers()) {
            for (Playlist playlist : user.getPlaylists()) {
                if (playlist.getVisibility().equalsIgnoreCase("public")
                        || user.getUsername().equals(cmd.getUsername())) {
                    boolean add = true;

                    if (playlist.getName().indexOf(cmd.getFilters().getName()) != 0) {
                        add = false;
                    }

                    if (!user.getUsername().equals(cmd.getFilters().getOwner())
                            && !cmd.getFilters().getOwner().equals("")) {
                        add = false;
                    }

                    if (add && searched.size() < five) {
                        sourceUsers.add(user);
                        searched.add(playlist.getName());
                    }
                }
            }
        }
    }

    /** searches 5 certain artists by name */
    public void searchArtist(final Command cmd) {
        for (Artist artist : DataBase.getDB().getArtists()) {
            boolean add = true;

            if (artist.getUsername().indexOf(cmd.getFilters().getName()) != 0) {
                add = false;
            }

            if (add && searched.size() < five) {
                sourceUsers.add(artist);
                searched.add(artist.getUsername());
            }
        }
    }

    /** searches 5 albums by the command filters */
    public void searchAlbum(final Command cmd) {
        for (Artist artist : DataBase.getDB().getArtists()) {
            for (Album album : artist.getAlbums()) {
                boolean add = true;
                if (album.getName().indexOf(cmd.getFilters().getName()) != 0) {
                    add = false;
                }
                if (artist.getUsername().indexOf(cmd.getFilters().getOwner()) != 0) {
                    add = false;
                }
                if (album.getDescription().indexOf(cmd.getFilters().getDescription()) != 0) {
                    add = false;
                }

                if (add && searched.size() < 5) {
                    sourceUsers.add(artist);
                    searched.add(album.getName());
                }
            }
        }
    }

    /** searches 5 hosts by name */
    public void searchHost(final Command cmd) {
        for (Host host : DataBase.getDB().getHosts()) {
            boolean add = true;

            if (host.getUsername().indexOf(cmd.getFilters().getName()) != 0) {
                add = false;
            }

            if (add && searched.size() < 5) {
                sourceUsers.add(host);
                searched.add(host.getUsername());
            }
        }
    }
}
