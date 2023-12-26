package pages;


public interface Visitor {
    /** visits a home page for printing */
    String visit(Page page);
    /** visits a liked content page for printing */
    String visit(LikedContentPage page);
    /** visits an artist page for printing */
    String visit(ArtistPage page);
    /** visits a host page for printing */
    String visit(HostPage page);
}
