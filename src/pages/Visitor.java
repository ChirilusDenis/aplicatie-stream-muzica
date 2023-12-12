package pages;


public interface Visitor {
    public String visit(Page page);
    public String visit(LikedContentPage page);
    public String visit(ArtistPage page);
    public String visit(HostPage page);
}
