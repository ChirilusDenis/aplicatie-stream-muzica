package misc;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface WrappedVisitable {

    /** accepts a wrapped visitor for wrapped data printing **/
    void accept(WrappedVisitor visitor, ObjectNode node);
}
