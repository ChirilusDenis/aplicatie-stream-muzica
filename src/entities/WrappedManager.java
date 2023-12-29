package entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import misc.WrappedVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class WrappedManager implements WrappedVisitor {
    private final int five = 5;

    /** constructs the wrapped data for a host **/
    @Override
    public void visit(final Host host, final ObjectNode node) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode stats = objectMapper.createObjectNode();

        stats.set("topEpisodes", mapToNode(host.getListenedEpisodes()));
        stats.put("listeners", host.getListeners());

        node.set("result", stats);
    }

    /** constructs the wrapped data for a user **/
    @Override
    public void visit(final User user, final ObjectNode node) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode stats = objectMapper.createObjectNode();
        stats.set("topArtists", mapToNode(user.getListenedArtist()));
        stats.set("topGenres", mapToNode(user.getListenedGenres()));
        stats.set("topSongs", mapToNode(user.getListenedSongs()));
        stats.set("topAlbums", mapToNode(user.getListenedAlbums()));
        stats.set("topEpisodes", mapToNode(user.getListenedEpisodes()));

        node.set("result", stats);
    }

    /** constructs the wrapped data for an artist **/
    @Override
    public void visit(final Artist artist, final ObjectNode node) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode stats = objectMapper.createObjectNode();
        stats.set("topAlbums", mapToNode(artist.getListenedAlbums()));
        stats.set("topSongs", mapToNode(artist.getListenedSongs()));
        ArrayNode arrayNode = objectMapper.valueToTree(mapToArray(artist.getFans()));
        stats.putArray("topFans").addAll(arrayNode);
        stats.put("listeners", artist.getFans().size());

        node.set("result", stats);
    }

    /** transforms a map into an array node of fields in the form of
     * "key" : value **/
    public ObjectNode mapToNode(final HashMap<String, Integer> map) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.createObjectNode();
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            list.add(entry);
        }
        list.sort((e1, e2) -> {
                                if (e1.getValue().equals(e2.getValue())) {
                                    return e1.getKey().compareTo(e2.getKey());
                                } else {
                                    return e2.getValue() - e1.getValue();
                                }
        });
        for (int i = 0; i < five && i < list.size(); i++) {
            node.put(list.get(i).getKey(), list.get(i).getValue());
        }
        return node;
    }

    /** transforms a map into a sorted array node **/
    public ArrayList<String> mapToArray(final HashMap<String, Integer> map) {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            list.add(entry);
        }
        list.sort((e1, e2) -> {
                                if (e1.getValue().equals(e2.getValue())) {
                                    return e1.getKey().compareTo(e2.getKey());
                                } else {
                                    return e2.getValue() - e1.getValue();
                            }
        });
        for (int i = 0; i < five && i < list.size(); i++) {
            names.add(list.get(i).getKey());
        }
        return names;
    }
}
