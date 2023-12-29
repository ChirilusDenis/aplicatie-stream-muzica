package misc;

import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.Artist;
import entities.Host;
import entities.User;

public interface WrappedVisitor {
    /** does the wrapped printing for a user **/
    void visit(User user, ObjectNode node);

    /** does the wrapped printing for an artist **/
    void visit(Artist artist, ObjectNode node);

    /** does the wrapped printing for a host **/
    void visit(Host host, ObjectNode node);
}
