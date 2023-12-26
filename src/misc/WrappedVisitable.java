package misc;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface WrappedVisitable {
    void accept(WrappedVisitor visitor, ObjectNode node);
}
