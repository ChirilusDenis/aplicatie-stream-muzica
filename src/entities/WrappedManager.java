package entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import misc.WrappedVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WrappedManager implements WrappedVisitor {
    @Override
    public void visit(Host host, ObjectNode node) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode stats = objectMapper.createObjectNode();

        stats.set("topEpisodes", mapToNode(host.getListenedEpisodes()));
        stats.put("listeners", host.getListeners());

        node.set("result", stats);
    }

    @Override
    public void visit(User user, ObjectNode node) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode stats = objectMapper.createObjectNode();
        stats.set("topArtists", mapToNode(user.getListenedArtist()));
        stats.set("topGenres", mapToNode(user.getListenedGenres()));
        stats.set("topSongs", mapToNode(user.getListenedSongs()));
        stats.set("topAlbums", mapToNode(user.getListenedAlbums()));
        stats.set("topEpisodes", mapToNode(user.getListenedEpisodes()));

        node.set("result", stats);
    }

    @Override
    public void visit(Artist artist, ObjectNode node) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode stats = objectMapper.createObjectNode();
//        ArrayNode fans = objectMapper.valueToTree(artist.getFans());
        stats.set("topAlbums", mapToNode(artist.getListenedAlbums()));
        stats.set("topSongs", mapToNode(artist.getListenedSongs()));
        stats.putArray("topFans").addAll(mapToNodeWithArray(artist.getFans()));
        stats.put("listeners", artist.getFans().size());

        node.set("result", stats);
    }

    public ObjectNode mapToNode(HashMap<String, Integer> map) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.createObjectNode();
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            list.add(entry);
        }
        list.sort((e1, e2) -> { if (e1.getValue().equals(e2.getValue())) {
                                    return e1.getKey().compareTo(e2.getKey());
                                } else {
                                    return e2.getValue() - e1.getValue();
        }});
        for (int i = 0; i < 5 && i < list.size(); i++) {
            node.put(list.get(i).getKey(), list.get(i).getValue());
        }
        return node;
    }

    public ArrayNode mapToNodeWithArray(HashMap<String, Integer> map) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.createObjectNode();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            list.add(entry);
        }
        list.sort((e1, e2) -> { if (e1.getValue().equals(e2.getValue())) {
                                    return e1.getKey().compareTo(e2.getKey());
                                } else {
                                    return e2.getValue() - e1.getValue();
                            }});
        for (int i = 0; i < 5 && i < list.size(); i++) {
            names.add(list.get(i).getKey());
        }
        ArrayNode arrayNode = objectMapper.valueToTree(names);
        return arrayNode;
    }
}
