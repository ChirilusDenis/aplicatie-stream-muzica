package misc;

import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.Artist;
import entities.Host;
import entities.User;

public interface WrappedVisitor {
    void visit(User user, ObjectNode node);
    void visit(Artist artist, ObjectNode node);
    void visit(Host host, ObjectNode node);
}
