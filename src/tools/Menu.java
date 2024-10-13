package tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.Artist;
import entities.Host;
import lombok.Getter;
import lombok.Setter;
import entities.User;
import misc.GeneralStatsManager;
import misc.Stats;
import misc.Status;

import java.util.ArrayList;

@Getter @Setter
public final class Menu {
    private ArrayList<Command> commands;
    private ObjectMapper objectMapper = new ObjectMapper();
    private String nullstr = null;

    /** executes the command passed to it by calling the right method */
    public ObjectNode execute(final Command cmd) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", cmd.getCommand());
        node.put("user", cmd.getUsername());
        node.put("timestamp", cmd.getTimestamp());
        User user = DataBase.findUser(cmd.getUsername());

        if (user == null && cmd.getUsername() != null && !cmd.getCommand().equals("addUser")
                                                        && !cmd.getCommand().equals("wrapped")) {
            node.put("message", "The username " + cmd.getUsername() + " doesn't exist.");
            return node;
        }

        switch (cmd.getCommand()) {

            case "search":
                user.doSearch(cmd, node, objectMapper);
                return node;

            case "select":
                user.doSelect(cmd, node);
                return node;

            case "load":
                user.doLoad(cmd, node);
                return node;

            case "playPause":
                user.getPlayer().doPlayPause(cmd, node);
                return node;

            case "status":
                user.getPlayer().updateTime(cmd.getTimestamp());
                ObjectNode statDetails = objectMapper.valueToTree(new Stats(user.getPlayer()));
                node.set("stats", statDetails);
                return node;

            case "createPlaylist":
                user.doCreatePlaylist(cmd, node);
                return node;

            case "addRemoveInPlaylist":
                user.doAddRemoveInPlaylist(cmd, node);
                return node;

            case "like":
                user.doLike(cmd, node);
                return node;

            case "showPlaylists":
                ArrayNode playListOut = objectMapper.valueToTree(user.getPlaylists());
                node.set("result", playListOut);
                return node;

            case "showPreferredSongs":
                ArrayNode preferredOut = objectMapper.valueToTree(user.getLikedStr());
                node.putArray("result").addAll(preferredOut);
                return node;

            case "repeat":
                user.getPlayer().doRepeat(cmd, node);
                return node;

            case "switchVisibility":
                user.doSwitchVisibility(cmd, node);
                return node;

            case "follow":
                user.doFollowPlaylist(node);
                return node;

            case "getTop5Songs":
                ArrayNode top5S = objectMapper.valueToTree(GeneralStatsManager.getTop5Songs());
                node.remove("user");
                node.put("result", top5S);
                return node;

            case "getTop5Playlists":
                ArrayNode top5P = objectMapper.valueToTree(GeneralStatsManager.getTop5Playlists());
                node.remove("user");
                node.put("result", top5P);
                return node;

            case "forward":
                user.getPlayer().doForward(cmd, node);
                return node;

            case "backward":
                user.getPlayer().doBackwards(cmd, node);
                return node;

            case "next":
                user.getPlayer().doNext(cmd, node);
                return node;

            case "prev":
                user.getPlayer().doPrev(cmd, node);
                return node;

            case "shuffle":
                user.getPlayer().doShuffle(cmd, node);
                return node;

            case "switchConnectionStatus":
                user.switchConnectionStatus(cmd, node);
                return node;

            case "getOnlineUsers":
                node.remove("user");
                ArrayNode onlineU = objectMapper.valueToTree(DataBase.getOnlineUsers());
                node.putArray("result").addAll(onlineU);
                return node;

            case "addUser":
                DataBase.addUser(cmd, node);
                return node;

            case "addAlbum":
                if (!DataBase.getDB().getArtists().contains(user)) {
                    node.put("message", user.getUsername() + " is not an artist.");
                } else {
                    ((Artist) user).addAlbum(cmd, node);
                }
                return node;

            case "showAlbums":
                Artist artist = (Artist) user;
                ArrayNode node1 = objectMapper.valueToTree(artist.getAlbums());
                node.set("result", node1);
                return node;

            case "printCurrentPage":
                if (user.getStatus().equals(Status.offline)) {
                    node.put("message", user.getUsername() + " is offline.");
                } else {
                    node.put("message", user.printPage());
                }
                return node;

            case "addMerch":
                if (!DataBase.getDB().getArtists().contains(user)) {
                    node.put("message", user.getUsername() + " is not an artist.");
                } else {
                    ((Artist) user).getArtistPage().addMerch(cmd, node);
                }
                return node;

            case "addEvent":
                if (!DataBase.getDB().getArtists().contains(user)) {
                    node.put("message", user.getUsername() + " is not an artist.");
                } else {
                    ((Artist) user).getArtistPage().addEvent(cmd, node);
                }
                return node;

            case "getAllUsers":
                node.remove("user");
                ArrayNode allUsers = objectMapper.valueToTree(DataBase.getAllUsers());
                node.putArray("result").addAll(allUsers);
                return node;

            case "deleteUser":
                DataBase.deleteUser(cmd, user, node);
                return node;

            case "addPodcast":
                if (!DataBase.getDB().getHosts().contains(user)) {
                    node.put("message", user.getUsername() + " is not a host.");
                } else {
                    ((Host) user).addPodcast(cmd, node);
                }
                return node;

            case "addAnnouncement":
                if (!DataBase.getDB().getHosts().contains(user)) {
                    node.put("message", user.getUsername() + " is not a host.");
                } else {
                    ((Host) user).addAnnouncement(cmd, node);
                }
                return node;

            case "removeAnnouncement":
                if (!DataBase.getDB().getHosts().contains(user)) {
                    node.put("message", user.getUsername() + " is not a host.");
                } else {
                    ((Host) user).removeAnnouncement(cmd, node);
                }
                return node;

            case "showPodcasts":
                ArrayNode node2 = objectMapper.valueToTree(((Host) user).getPodcasts());
                node.set("result", node2);
                return node;

            case "removeAlbum":
                if (!DataBase.getDB().getArtists().contains(user)) {
                    node.put("message", user.getUsername() + " is not an artist.");
                } else {
                    ((Artist) user).removeAlbum(cmd, node);
                }
                return node;

            case "changePage":
                user.changePage(cmd, node);
                return node;

            case "removePodcast":
                if (!DataBase.getDB().getHosts().contains(user)) {
                    node.put("message", user.getUsername() + " is not a host.");
                } else {
                    ((Host) user).removePodcast(cmd, node);
                }
                return node;

            case "getTop5Albums":
                GeneralStatsManager.getTop5Albums(node, objectMapper);
                return node;

            case "getTop5Artists":
                GeneralStatsManager.getTop5Artists(node, objectMapper);
                return node;

            case "removeEvent":
                if (!DataBase.getDB().getArtists().contains(user)) {
                    node.put("message", user.getUsername() + " is not an artist.");
                } else {
                    ((Artist) user).getArtistPage().removeEvent(cmd, node);
                }
                return node;

            case "wrapped":
                DataBase.getDB().getObervator().notifyAll(cmd.getTimestamp());
                DataBase.doWrapper(user, cmd.getUsername(), node);
                return node;

            case "endProgram":
                DataBase.endProgram(node);
                return node;

            case "buyPremium":
                user.buyPremium(node);
                return node;

            case "cancelPremium":
                user.cancelPremium(node);
                return node;

            case "buyMerch":
                user.buyMerch(cmd, node);
                return node;

            case "seeMerch":
                user.seeMerch(node);
                return node;

            case "subscribe":
                user.subscribe(node);
                return node;

            case "getNotifications":
                user.getNotif(node);
                return node;

            case "previousPage":
                user.previousPage(node);
                return node;

            case "nextPage":
                user.nextPage(node);
                return node;

            case "updateRecommendations":
                user.updateRecommendations(cmd, node);
                return node;

            default:
                return null;
        }
    }
}
