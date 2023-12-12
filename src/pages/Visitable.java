package pages;

public interface Visitable {
    public String accept(Visitor visitor);
}
