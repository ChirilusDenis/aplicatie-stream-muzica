package misc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import entities.Song;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter
public final class ArtistMoneyStats {
    @JsonIgnore private String name;
    private Double songRevenue = 0.0;
    @JsonIgnore private Double songValue = 0.0;
    private Double merchRevenue = 0.0;
    private int ranking;
    private String mostProfitableSong = "N/A";
    @JsonIgnore private ArrayList<Song> profitableSongs = new ArrayList<>();
    @JsonIgnore private Double hundred = 100.0;

    public ArtistMoneyStats(final String name) {
        this.name = name;
    }

    /** increments the profit made by songs **/
    public void addSongProfit(final Double value) {
        songValue = songValue + value;
    }

    /** does the final 2 decimal approximation of the song profits **/
    public void update() {
        if (!profitableSongs.isEmpty()) {
            songRevenue = Math.round(songValue * hundred) / hundred;

            profitableSongs.sort((s1, s2) -> {
                                            if (Math.abs(s1.getProfit() - s2.getProfit()) < 1) {
                                                return s1.getName().compareTo(s2.getName());
                                            } else {
                                                return (int) (s2.getProfit() - s1.getProfit());
                                            }
            });
            mostProfitableSong = profitableSongs.get(0).getName();
        }
    }

}
