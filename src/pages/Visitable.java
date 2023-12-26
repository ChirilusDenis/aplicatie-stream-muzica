package pages;

public interface Visitable {
    /** accepts a page printer visitor */
    String accept(Visitor visitor);
}
