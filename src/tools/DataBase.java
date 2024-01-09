package tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.Artist;
import entities.Host;
import entities.Song;
import entities.User;
import entities.WrappedManager;
import entitycolections.Album;
import entitycolections.Playlist;
import entitycolections.Podcast;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import lombok.Getter;
import misc.ArtistMoneyStats;
import misc.PremiumStatus;
import misc.Status;
import pages.Merch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
public final class DataBase {
    private static DataBase db = null;
    private ArrayList<Song> songs = new ArrayList<>();
    private ArrayList<Podcast> podcasts = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Artist> artists = new ArrayList<>();
    private ArrayList<Host> hosts = new ArrayList<>();
    private ArrayList<User> newUsers = new ArrayList<>();

    private ArrayList<User> usersFromStart;
    private ArrayList<Song> allSongs = new ArrayList<>();
    private Obervator obervator = new Obervator();

    private ArrayList<Host> tempHosts = new ArrayList<>();
    private ArrayList<ArtistMoneyStats> artistStats = new ArrayList<>();
    private UserFactory userFactory = new UserFactory();
    private static final Double MILLION = 1000000.0;

    private DataBase(final ArrayList<SongInput> songs,
                     final ArrayList<PodcastInput> podcasts,
                     final ArrayList<UserInput> users) {
        for (SongInput s : songs) {
            this.songs.add(new Song(s));
        }
        for (PodcastInput p : podcasts) {
            this.podcasts.add(new Podcast(p));
        }
        for (UserInput u : users) {
            this.users.add(new User(u));
        }
        this.usersFromStart = new ArrayList<>(List.copyOf(this.users));
        this.allSongs.addAll(this.songs);
        obervator.getUsers().addAll(this.users);
    }

    /** returns an array with all the normal users in the database */
    public ArrayList<User> getUsers() {
        ArrayList<User> allNormalUsers = new ArrayList<>(List.copyOf(this.users));
        for (User u : newUsers) {
            allNormalUsers.add(u);
        }
        return allNormalUsers;
    }

    public static DataBase getDB() {
        return db;
    }

    /** moves all the information from the input library to the database */
    public static void buildDB(final ArrayList<SongInput> songs,
                               final ArrayList<PodcastInput> podcasts,
                               final ArrayList<UserInput> users) {
        if (db == null) {
            db = new DataBase(songs, podcasts, users);
        }
    }

    /** searches for the user with the specified username*/
    public static User findUser(final String name) {
        for (User u : db.users) {
            if (u.getUsername().equals(name)) {
                return u;
            }
        }
        for (User u : db.newUsers) {
            if (u.getUsername().equals(name)) {
                return u;
            }
        }
        for (Artist a : db.artists) {
            if (a.getUsername().equals(name)) {
                return a;
            }
        }
        for (Host h : db.hosts) {
            if (h.getUsername().equals(name)) {
                return h;
            }
        }
        return null;
    }

    /** searches for the song with the specified name*/
    public static Song findSong(final String name) {
        for (Song s : db.songs) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        for (Artist artist : db.getArtists()) {
            for (Album album : artist.getAlbums()) {
                for (Song s : album.getSongsfull()) {
                    if (s.getName().equals(name)) {
                        return s;
                    }
                }
            }
        }
        return null;
    }

    /** searches for the podcast with the specified name*/
    public static Podcast findPodcast(final String name) {
        for (Podcast p : db.podcasts) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        for (Host h : db.hosts) {
            for (Podcast p : h.getPodcasts()) {
                if (p.getName().equals(name)) {
                    return p;
                }
            }
        }
        return null;
    }

    /** searches for the playlist with the specified name*/
    public static Playlist findPlaylist(final String name) {
        for (User u : db.getUsers()) {
            for (Playlist p : u.getPlaylists()) {
                if (p.getName().equalsIgnoreCase(name)) {
                    return p;
                }
            }
        }
        for (Artist a : db.artists) {
            for (Album al : a.getAlbums()) {
                if (al.getName().equalsIgnoreCase(name)) {
                    return (Playlist) al;
                }
            }
        }
        return null;
    }

    /** resets all the changes made by users from test to test*/
    public static void reset() {
        db.artists.clear();
        db.newUsers.clear();
        db.hosts.clear();
        db.tempHosts.clear();
        db.artistStats.clear();
        db.users = new ArrayList<>(List.copyOf(db.usersFromStart));
        db.allSongs.clear();
        db.allSongs.addAll(db.songs);
        db.obervator.getUsers().clear();
        db.obervator.getUsers().addAll(db.users);
        for (User user : db.users) {
            user.getListenedArtist().clear();
            user.getBoughtMerch().clear();
            user.getListenedAlbums().clear();
            user.getListenedGenres().clear();
            user.getListenedSongs().clear();
            user.getListenedEpisodes().clear();
            user.getPlaylists().clear();
            user.getSearchBar().clear();
            user.getLiked().clear();
            user.setStatus(Status.online);
            user.setPrStatus(PremiumStatus.normal);
            user.getNotifications().clear();
            user.getPlayer().getSongHistory().clear();
            user.getPlayer().getSongHistoryAdd().clear();
            user.setNumUsersUsingThis(0);
            user.getPlayer().setCrtEp(null);
            user.getPlayer().setLastEpTime(0);
            user.setCurrentPage("Home");
            user.setPageOrigin(null);
            user.getFollowedPlaylists().clear();
            user.getBack().clear();
            user.getFront().clear();
        }
        for (Song song : db.songs) {
            song.setProfit(0.0);
            song.setNumLikes(0);
            song.setNumUsing(0);
        }
    }

    /** returns an array with all users with the status set as online */
    public static ArrayList<String> getOnlineUsers() {
        ArrayList<String> onlineUsers = new ArrayList<>();
        for (User user : db.users) {
            if (user.getStatus().equals(Status.online)) {
               onlineUsers.add(user.getUsername());
            }
        }

        return onlineUsers;
    }

    /** adds a user is possible and returns the appropriate message */
    public static void addUser(final Command cmd, final ObjectNode node) {
        if (DataBase.findUser(cmd.getUsername()) != null) {
            node.put("message", "The username " + cmd.getUsername() + " is already taken.");
        } else {
            node.put("message", "The username " + cmd.getUsername()
                    + " has been added successfully.");
            User newUser = db.userFactory.createUser(cmd);
            db.obervator.getUsers().add(newUser);
        }
    }

    /** returns an array with all users in the database */
    public static ArrayList<String> getAllUsers() {
        ArrayList<String> all = new ArrayList<>();
        for (User user : db.getUsers()) {
            all.add(user.getUsername());
        }
        for (Artist artist : db.artists) {
            all.add(artist.getUsername());
        }
        for (Host host : db.hosts) {
            all.add(host.getUsername());
        }
        return all;
    }

    /** removes a user from the databse */
    public static void removeUser(final User user) {
        if (db.users.contains(user)) {
            db.users.remove(user);
        } else if (db.newUsers.contains(user)) {
            db.newUsers.remove(user);
        } else if (db.artists.contains(user)) {
            db.artists.remove(user);
        } else if (db.hosts.contains(user)) {
            db.hosts.remove(user);
        }
    }

    /** removes the contents of a deleted users from the arrays of the rest of the users */
    public static void updateAfterDeleteUser(final User user) {
        for (Playlist playlist : user.getPlaylists()) {
            for (User user1 : db.getUsers()) {
                user1.getFollowedPlaylists().remove(playlist);
                for (Song s : playlist.getSongsfull()) {
                    user1.getLiked().remove(s);
                }
            }
        }

        for (Playlist playlist : user.getFollowedPlaylists()) {
            playlist.getFollowedBy().remove(user);
            playlist.follow(-1);
        }

        if (db.artists.contains(user)) {
            Artist artist = (Artist) user;
            for (Album album : artist.getAlbums()) {
                for (Song song : album.getSongsfull()) {
                    for (User user1 : db.getUsers()) {
                        user1.getLiked().remove(song);
                    }
                }
            }
        }
    }

    /** removes certain user if possible and returns the appropriate message */
    public static void deleteUser(final Command cmd, final User user, final ObjectNode node) {
        db.obervator.notifyAll(cmd.getTimestamp());
        boolean canDelete = true;
        if (DataBase.getDB().artists.contains(user)) {
            canDelete = ((Artist) user).canBeDeleted();
        } else if (DataBase.getDB().hosts.contains(user)) {
            canDelete = ((Host) user).canBeDeleted();
        }

        if (user.getNumUsersUsingThis() == 0 && canDelete) {
            node.put("message", user.getUsername() + " was successfully deleted.");
            updateAfterDeleteUser(user);
            removeUser(user);
        } else {
            node.put("message", user.getUsername() + " can't be deleted.");
        }
    }

    /** checks if a playlist is an album **/
    public static boolean isAlbum(final Playlist playlist) {
        for (Artist artist : db.artists) {
            if (artist.getAlbums().contains(playlist)) {
                return true;
            }
        }
        return false;
    }

    /** finds a temporary host with the given name **/
    public static Host getTempHost(final String name) {
        for (Host host : db.tempHosts) {
            if (host.getUsername().equals(name)) {
                return host;
            }
        }
        return null;
    }

    /** does the wrapper for a user **/
    public static void doWrapper(User user, final String name, final ObjectNode node) {
        String userType = "user";
        if (db.artists.contains(user)) {
            userType = "artist";
        } else if (db.hosts.contains(user)) {
            userType = "host";
        }
        if (user == null) {
            user = getTempHost(name);
            if (user == null) {
                node.put("message", "No data to show for " + userType + " " + name + ".");
            } else {
                userType = "host";
            }
        }
        if (user != null && !user.noWrapper()) {
            WrappedManager visitor = new WrappedManager();
            user.accept(visitor, node);
        } else {
            node.put("message", "No data to show for " + userType + " " + name + ".");
        }
    }

    /** adds an artist that had something played or bought **/
    public static void addListenedArtist(final String name) {
        boolean add = true;
        for (ArtistMoneyStats artist : db.artistStats) {
           if (artist.getName().equals(name)) {
               add = false;
           }
       }
        if (add) {
            db.artistStats.add(new ArtistMoneyStats(name));
        }
    }

    /** executes the printing of the played artists and their profits **/
    public static void endProgram(final ObjectNode node) {
        node.remove("user");
        node.remove("timestamp");

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode results = objectMapper.createObjectNode();
        for (User user : db.users) {
            distributeProfits(user.getPlayer().getSongHistory(), MILLION);
        }
        for (User user : db.newUsers) {
            distributeProfits(user.getPlayer().getSongHistory(), MILLION);
        }

        db.artistStats.sort((a1, a2) -> (int) (a2.getMerchRevenue() + a2.getSongValue()
                - a1.getMerchRevenue() - a1.getSongValue()));

        for (int i = 0; i < db.artistStats.size(); i++) {
            db.artistStats.get(i).update();
            db.artistStats.get(i).setRanking(i + 1);
            results.set(db.artistStats.get(i).getName(),
                    objectMapper.valueToTree(db.artistStats.get(i)));
        }

        node.set("result", results);
    }

    /** adds a song and its profit to an artist stat **/
    public static void addProfitableSong(final Song song, final String artist) {
        for (ArtistMoneyStats artistMoneyStats : db.artistStats) {
            if (artistMoneyStats.getName().equals(artist)
                    && !artistMoneyStats.getProfitableSongs().contains(song)) {
                artistMoneyStats.getProfitableSongs().add(song);
            }
        }
    }

    /** distributes equal portions of profit to the artists that owe the songs
     * from the song list **/
    public static void distributeProfits(final ArrayList<Song> songHistory, final Double val) {
        if (!songHistory.isEmpty()) {
            Double value = val / songHistory.size();

            for (Song song : songHistory) {
                song.setProfit(song.getProfit() + value);
                addProfitableSong(song, song.getArtist());

                for (ArtistMoneyStats artist : db.artistStats) {
                    if (artist.getName().equals(song.getArtist())) {
                        artist.addSongProfit(value);
                    }
                }
            }
        }
    }

    /** finds the stats of an artist with the gives name **/
    public static ArtistMoneyStats getTmpArtist(final String name) {
        for (ArtistMoneyStats artist : db.artistStats) {
            if (artist.getName().equals(name)) {
                return artist;
            }
        }
        return null;
    }

    /** adds merch revenue to the artist **/
    public static void addMerchRev(final User artist, final Merch merch) {
        addListenedArtist(artist.getUsername());
        ArtistMoneyStats tmpArtist = getTmpArtist(artist.getUsername());
        Double merchRev = tmpArtist.getMerchRevenue();
        tmpArtist.setMerchRevenue(merchRev + (double) merch.getPrice());
    }

    /** creates a fan playlist recommendation **/
    public static Playlist createFanPlaylist(final String name) {
        ArrayList<Song> liked = new ArrayList<>();
        for (Artist artist : db.artists) {
            if (artist.getUsername().equals(name)) {
                ArrayList<String> fans = new WrappedManager().mapToArray(artist.getFans());
                for (String names : fans) {
                    User user = DataBase.findUser(names);
                    if (user != null) {
                        ArrayList<Song> songs1 = new ArrayList<>(Set.copyOf(user.getLiked()));
                        songs1.sort((s1, s2) -> s2.getNumLikes() - s1.getNumLikes());
                        for (int i = 0; i < artist.getFive() && i < songs1.size(); i++) {
                            liked.add(user.getLiked().get(i));
                        }
                    }
                    liked.sort((s1, s2) -> s2.getNumLikes() - s1.getNumLikes());
                    while (liked.size() > artist.getFive()) {
                        liked.remove(artist.getFive());
                    }
                }
                break;
            }
        }
        Playlist playlist = new Playlist(name + " Fan Club recommendations", 0, name);
        for (Song song : liked) {
            playlist.addRemove(song);
        }
        return playlist;
    }
}
