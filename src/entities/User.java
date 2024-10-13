package entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entitycolections.Album;
import entitycolections.Playlist;
import fileio.input.UserInput;
import lombok.Getter;
import lombok.Setter;
import misc.PremiumStatus;
import misc.Status;
import misc.WrappedVisitable;
import misc.WrappedVisitor;
import observer.Observer;
import pages.ArtistPage;
import pages.LikedContentPage;
import pages.Merch;
import pages.Page;
import pages.Visitable;
import pages.PagePrinterVisitor;
import search.SearchBar;
import tools.Command;
import tools.DataBase;
import tools.MusicPlayer;
import tools.NotifObservator;

import java.util.ArrayList;
import java.util.HashMap;

@Getter @Setter
public class User implements Observer, WrappedVisitable, NotifObservator {
    private String username;
    private String city;
    private int age;
    private ArrayList<Playlist> playlists = new ArrayList<>();
    private ArrayList<Song> liked = new ArrayList<>();
    private SearchBar searchBar = new SearchBar();
    private MusicPlayer player = new MusicPlayer(this);

    private Status status = Status.online;
    private ArrayList<Playlist> followedPlaylists = new ArrayList<>();
    private int numUsersUsingThis = 0;
    private String currentPage = "Home";
    private PagePrinterVisitor visitor = new PagePrinterVisitor();
    private User pageOrigin = null;
    private int five = 5;

    private HashMap<String, Integer> listenedSongs = new HashMap<>();
    private HashMap<String, Integer> listenedArtist = new HashMap<>();
    private HashMap<String, Integer> listenedGenres = new HashMap<>();
    private HashMap<String, Integer> listenedAlbums = new HashMap<>();
    private HashMap<String, Integer> listenedEpisodes = new HashMap<>();
    private PremiumStatus prStatus = PremiumStatus.normal;
    private ArrayList<Merch> boughtMerch = new ArrayList<>();
    private ArrayList<Notification> notifications = new ArrayList<>();

    private Visitable crtPage = new Page();
    private ArrayList<Visitable> back = new ArrayList<>();
    private ArrayList<Visitable> front = new ArrayList<>();
    private Visitable homePage = new Page();
    private Visitable likedContentPage = new LikedContentPage();
    private ArrayList<Playlist> recomendedPlaylists = new ArrayList<>();
    private ArrayList<Song> recomendedSongs = new ArrayList<>();


    /** decides if a song should be liked or unliked, taking into consideration if the
     * song was previously liked or not byt the user*/
    public boolean likeSong(final Song song) {
        if (this.liked.contains(song)) {
            song.addLike(-1);
            liked.remove(song);
            return false;
        } else {
            song.addLike(1);
            liked.add(song);
            return true;
        }
    }

    public User() {
    }

    public User(final String username, final String city, final int age) {
        this.username = username;
        this.city = city;
        this.age = age;
    }

    public User(final UserInput user) {
        this.username = user.getUsername();
        this.city = user.getCity();
        this.age = user.getAge();
    }

    /** checks if the user has the playlist with the passed name,
     * regardless if it is private or not */
    public boolean hasPlaylist(final String name) {
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /** turns the liked songs array into a string array for easier mapping to output*/
    public ArrayList<String> getLikedStr() {
        ArrayList<String> preferred = new ArrayList<>();
        for (Song s : liked) {
            preferred.add(s.getName());
        }
        return preferred;
    }

    /** follows or unfollows a playlist, taking into consideration if the user follows
     * that playlist or not*/
    public boolean followPlaylist(final Playlist playlist) {
        if (playlist.getFollowedBy().contains(username)) {
            playlist.getFollowedBy().remove(username);
            followedPlaylists.remove(playlist);
            playlist.follow(-1);
            return false;
        } else {
            playlist.getFollowedBy().add(username);
            playlist.follow(1);
            followedPlaylists.add(playlist);
            return true;
        }
    }

    /** switches the current connection status and returns the appropriate message */
    public void switchConnectionStatus(final Command cmd, final ObjectNode node) {
        if (DataBase.getDB().getArtists().contains(this)
                || DataBase.getDB().getHosts().contains(this)) {
            node.put("message", this.getUsername() + " is not a normal user.");
        } else {
            player.updateTime(cmd.getTimestamp());
            node.put("message", this.getUsername() + " has changed status successfully.");
            if (status == Status.online) {
                status = Status.offline;
            } else {
                status = Status.online;
            }
        }
    }

    /** prints the current page; be it home, liked content or another user's page */
    public String printPage() {
        crtPage.refreshPage(liked, followedPlaylists, recomendedSongs, recomendedPlaylists);
        return crtPage.accept(visitor);
    }

    /** increment or decrement the number of users using this */
    public void useThis(final int value) {
        this.numUsersUsingThis = this.numUsersUsingThis + value;
    }

    /** changes the current page to the specified one if possible
     * and returns the appropriate message */
    public void changePage(final Command cmd, final ObjectNode node) {
        if (!(cmd.getNextPage().equals("Home")) && !(cmd.getNextPage().equals("LikedContent"))
            && (!cmd.getNextPage().equals("Artist"))) {
            node.put("message", this.getUsername() + " is trying to access a non-existent page.");
            currentPage = "";
        } else {
            back.add(0, crtPage);
            front.clear();
            if (cmd.getNextPage().equals("Home")) {
                crtPage = homePage;
            } else if (cmd.getNextPage().equals("LikedContent")) {
                crtPage = likedContentPage;
            } else if (cmd.getNextPage().equals("Artist")) {
                crtPage = new ArtistPage(new User(), new ArrayList<Album>());
            }
            node.put("message", this.getUsername() + " accessed "
                    + cmd.getNextPage() + " successfully.");
            if (pageOrigin != null) {
                pageOrigin.useThis(-1);
                pageOrigin = null;
            }
            currentPage = cmd.getNextPage();
        }
    }

    /** handles the search result cases and returns the appropriate message */
    public void doSearch(final Command cmd, final ObjectNode node,
                         final ObjectMapper objectMapper) {
        ArrayList<String> result = new ArrayList<>();

        if (this.getStatus().equals(Status.offline)) {
            node.put("message", this.getUsername() + " is offline.");
            ArrayNode none = objectMapper.valueToTree(result);
            node.putArray("results").addAll(none);
        } else {
            this.getSearchBar().search(cmd);
            result = this.getSearchBar().getSearched();

            if (this.getPlayer().getWhatIsLoaded() == 2) {
                this.getPlayer().updateTime(cmd.getTimestamp());
                this.getPlayer().setLastEpTime(this.getPlayer().getRemainingTime());
            }

            this.getPlayer().updateTime(cmd.getTimestamp());
            this.getPlayer().unload();

            node.put("message", "Search returned " + result.size() + " results");
            ArrayNode idk = objectMapper.valueToTree(result);

            node.putArray("results").addAll(idk);
        }
    }

    /** handles the select cases and returns the appropriate message */
    public void doSelect(final Command cmd, final ObjectNode node) {
        if (this.getSearchBar().getSearched().isEmpty()
                && this.getSearchBar().getWhatWasSearched() == -1) {
            node.put("message", "Please conduct a search before making a selection.");
        } else if (cmd.getItemNumber() > this.getSearchBar().getSearched().size()) {
            node.put("message", "The selected ID is too high.");
        } else if (this.getSearchBar().getWhatWasSearched() == 3) {
            Artist artist = (Artist) DataBase.findUser(
                    this.getSearchBar().getSearched().get(cmd.getItemNumber() - 1));
            if (artist == null) {
                node.put("message", "no more such artist");
                return;
            }
            artist.useThis(1);
            node.put("message", "Successfully selected " + artist.getUsername() + "'s page.");
            if (pageOrigin != null) {
                pageOrigin.useThis(-1);
            }
//            this.pages.add(artist.getArtistPage());
            back.add(0, crtPage);
            crtPage = artist.getArtistPage();
            currentPage = "Artist";

            this.pageOrigin = artist;
            this.getSearchBar().clear();
        } else if (this.getSearchBar().getWhatWasSearched() == 4) {
            Host host = (Host) DataBase.findUser(
                    this.getSearchBar().getSearched().get(cmd.getItemNumber() - 1));
            host.useThis(1);
            node.put("message", "Successfully selected " + host.getUsername() + "'s page.");
            if (pageOrigin != null) {
                pageOrigin.useThis(-1);
            }
            this.pageOrigin = host;

            back.add(0, crtPage);
            crtPage = host.getHostPage();
            currentPage = "Host";
//            pages.add(host.getHostPage());
            this.getSearchBar().clear();
        } else {
            if (!this.getSearchBar().getSourceUsers().isEmpty()) {
                this.getSearchBar().setSelectedSourceUser(
                        this.getSearchBar().getSourceUsers().get(cmd.getItemNumber() - 1));
            }
            this.getSearchBar().setWhatWasSelected(
                    this.getSearchBar().getWhatWasSearched());
            node.put("message", "Successfully selected "
                    + this.getSearchBar().getSearched().get(cmd.getItemNumber() - 1) + ".");
            this.getSearchBar().setSelected(
                    this.getSearchBar().getSearched().get(cmd.getItemNumber() - 1));
        }
    }

    /** handles the load to player cases and returns the appropriate message */
    public void doLoad(final Command cmd, final ObjectNode node) {
        if (this.getSearchBar().getSelected().isEmpty()) {
            node.put("message", "Please select a source before attempting to load.");
        } else if (this.getSearchBar().getWhatWasSelected() == 1
                && DataBase.findPlaylist(
                this.getSearchBar().getSelected()).getSongsfull().isEmpty()) {
            node.put("message", "You can't load an empty audio collection!");
        } else {
            this.getPlayer().updateTime(cmd.getTimestamp());
            this.getPlayer().load(this.getSearchBar().getSelected(),
                    cmd.getTimestamp(),
                    this.getSearchBar().getWhatWasSelected());
            node.put("message", "Playback loaded successfully.");
            this.getSearchBar().clear();
        }
    }

    /** handles the create playlist cases and returns the appropriate message */
    public void doCreatePlaylist(final Command cmd, final ObjectNode node) {
        if (this.hasPlaylist(cmd.getPlaylistName())) {
            node.put("message", "A playlist with the same name already exists.");
        } else {
            node.put("message", "Playlist created successfully.");
            this.getPlaylists().add(new Playlist(cmd.getPlaylistName(),
                    cmd.getTimestamp(), this.getUsername()));
        }
    }

    /** handles the add or remove in playlist and returns the appropriate message */
    public void doAddRemoveInPlaylist(final Command cmd, final ObjectNode node) {
        if (this.getPlayer().getLoaded().isEmpty()) {
            node.put("message",
                    "Please load a source before adding to or removing from the playlist.");
        } else if (this.getPlayer().getWhatIsLoaded() == 2) {
            node.put("message", "The loaded source is not a song.");
        } else if (cmd.getPlaylistId() > this.getPlaylists().size()) {
            node.put("message", "The specified playlist does not exist.");
        } else {
            if (this.getPlaylists().get(cmd.getPlaylistId() - 1).addRemove(
                    this.getPlayer().getCrtSong())) {
                node.put("message", "Successfully added to playlist.");
            } else {
                node.put("message", "Successfully removed from playlist.");
            }
        }
    }

    /** handles the like song cases and returns the appropriate message */
    public void doLike(final Command cmd, final ObjectNode node) {
        if (this.getStatus().equals(Status.offline)) {
            node.put("message", this.getUsername() + " is offline.");
        } else {
            this.getPlayer().updateTime(cmd.getTimestamp());
            if (this.getPlayer().getLoaded().isEmpty()) {
                node.put("message", "Please load a source before liking or unliking.");
            } else if (this.getPlayer().getWhatIsLoaded() == 2) {
                node.put("message", "Loaded source is not a song.");
            } else if (this.likeSong(this.getPlayer().getCrtSong())) {
                node.put("message", "Like registered successfully.");
            } else {
                node.put("message", "Unlike registered successfully.");
            }
        }
    }

    /** handles the switch visibility cases and returns the appropriate message */
    public void doSwitchVisibility(final Command cmd, final ObjectNode node) {
        if (cmd.getPlaylistId() <= this.getPlaylists().size()) {
            if (this.getPlaylists().get(cmd.getPlaylistId() - 1).switchVis()) {
                node.put("message", "Visibility status updated successfully to public.");
            } else {
                node.put("message", "Visibility status updated successfully to private.");
            }
        } else {
            node.put("message", "The specified playlist ID is too high.");
        }
    }

    /** handles the follow playlist cases and returns the appropriate message */
    public void doFollowPlaylist(final ObjectNode node) {
        if (this.getSearchBar().getSelected().isEmpty()) {
            node.put("message", "Please select a source before following or unfollowing.");
        } else if (this.getSearchBar().getWhatWasSelected() != 1) {
            node.put("message", "The selected source is not a playlist.");
        } else {
            Playlist playlist = DataBase.findPlaylist(this.getSearchBar().getSelected());
            if (this.getPlaylists().contains(playlist)) {
                node.put("message", "You cannot follow or unfollow your own playlist.");
            } else {
                if (this.followPlaylist(playlist)) {
                    node.put("message", "Playlist followed successfully.");
                    DataBase.findUser(playlist.getOwner()).updateNotif("New Follower",
                            username + " has followed your playlist.");
                } else {
                    node.put("message", "Playlist unfollowed successfully.");
                }
            }
        }
    }

    /** updates the current player */
    public void update(final int crtTime) {
        this.player.updateTime(crtTime);
    }

    /** accepts a wrapper visitor to construct the right wrapped data **/
    public void accept(final WrappedVisitor visitor, final ObjectNode node) {
        visitor.visit(this, node);
    }

    /** checks if there's any data to wrap for this user **/
    public boolean noWrapper() {
        return listenedSongs.isEmpty() && listenedEpisodes.isEmpty();
    }

    /** switches the user status to premium **/
    public void buyPremium(final ObjectNode node) {
        if (prStatus.equals(PremiumStatus.premium)) {
            node.put("message", username + " is already a premium user.");
        } else {
            node.put("message", username + " bought the subscription successfully.");
            prStatus = PremiumStatus.premium;
        }
    }

    /** switches the user status to normal and pays the played artists **/
    public void cancelPremium(final ObjectNode node) {
        if (prStatus.equals(PremiumStatus.normal)) {
            node.put("message", username + " is not a premium user.");
        } else {
            node.put("message", username + " cancelled the subscription successfully.");
            DataBase.distributeProfits(this.player.getSongHistory(), 1000000.0);
            this.player.getSongHistory().clear();
            prStatus = PremiumStatus.normal;
        }
    }

    /** buys a merch if possible and pays the artist **/
    public void buyMerch(final Command cmd, final ObjectNode node) {
        if (pageOrigin != null && DataBase.getDB().getArtists().contains(pageOrigin)) {
            if (((ArtistPage) crtPage).hasMerch(cmd.getName())) {
                node.put("message", username + " has added new merch successfully.");
                for (Merch merch : ((ArtistPage) crtPage).getMerches()) {
                    if (merch.getName().equals(cmd.getName())) {
                        boughtMerch.add(new Merch(merch.getName(),
                                merch.getDescription(), merch.getPrice()));
                        DataBase.addMerchRev(pageOrigin, merch);
                    }
                }
            } else {
                node.put("message", "The merch " + cmd.getName() + " doesn't exist.");
            }
        } else {
            node.put("message", "Cannot buy merch from this page.");
        }
    }

    /** prints the current merchs owned by the user **/
    public void seeMerch(final ObjectNode node) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<String> merchNames = new ArrayList<>();
        for (Merch merch : boughtMerch) {
            merchNames.add(merch.getName());
        }
        ArrayNode merch = objectMapper.valueToTree(merchNames);
        node.putArray("result").addAll(merch);
    }

    /** subscribe or unsubscribes from an artist/host corresponding to the
     * current selected page **/
    public void subscribe(final ObjectNode node) {
        if (pageOrigin == null) {
            node.put("message", "To subscribe you need to be on the page of an artist or host.");
        } else {
            if (DataBase.getDB().getArtists().contains(pageOrigin)) {
                if (((Artist) pageOrigin).addSub(this)) {
                    node.put("message", username + " subscribed to "
                            + pageOrigin.getUsername() + " successfully.");
                } else {
                    node.put("message", username + " unsubscribed from "
                            + pageOrigin.getUsername() + " successfully.");
                }
            } else if (DataBase.getDB().getHosts().contains(pageOrigin)) {
                if (((Host) pageOrigin).addSub(this)) {
                    node.put("message", username + " subscribed to "
                            + pageOrigin.getUsername() + " successfully.");
                } else {
                    node.put("message", username + " unsubscribed from "
                            + pageOrigin.getUsername() + " successfully.");
                }
            }
        }
    }

    /** prints all the notifications received from the last get notifications call **/
    public void getNotif(final ObjectNode node) {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode notifs = objectMapper.createArrayNode();
        for (Notification notification : notifications) {
            ObjectNode oneNotif = objectMapper.valueToTree(notification);
            notifs.add(oneNotif);
        }
        node.putArray("notifications").addAll(notifs);
        notifications.clear();
    }
    /** switches to the previous page if possible **/
    public void previousPage(final ObjectNode node) {
        if (back.isEmpty()) {
            node.put("message", "There are no pages left to go back.");
        } else {
            front.add(0, crtPage);
            crtPage = back.get(0);
            back.remove(0);
            node.put("message", "The user " + username
                    + " has navigated successfully to the previous page.");
        }
    }

    /** switches to the next page if possible **/
    public void nextPage(final ObjectNode node) {
        if (front.isEmpty()) {
            node.put("message", "There are no pages left to go forward.");
        } else {
            back.add(0, crtPage);
            crtPage = front.get(0);
            front.remove(0);
            node.put("message", "The user " + username
                + " has navigated successfully to the next page.");
        }
    }

    /** updates the current user's recommendations **/
    public void updateRecommendations(final Command cmd, final ObjectNode node) {
        switch (cmd.getRecommendationType()) {
            case "fans_playlist":
            update(cmd.getTimestamp());
            recomendedPlaylists.add(DataBase.createFanPlaylist(player.getCrtSong().getArtist()));
                break;
            default:
                break;
        }
        node.put("message", "The recommendations for user " + username
                + " have been updated successfully.");
    }

    /** adds a notification to the user's notifications list **/
    @Override
    public void updateNotif(final String name, final String description) {
        notifications.add(new Notification(name, description));
    }
}
