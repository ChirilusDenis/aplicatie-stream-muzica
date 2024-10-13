package entitycolections;

import com.fasterxml.jackson.annotation.JsonIgnore;
import entities.PodcastEpisode;
import fileio.input.PodcastInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter @Setter
public class Podcast {
    @JsonIgnore
    private String owner;
    private String name;
    @JsonIgnore
    private ArrayList<PodcastEpisode> episodesFull = new ArrayList<>();
    private ArrayList<String> episodes = new ArrayList<>();

    @JsonIgnore
    private int numUsingThis = 0;

    public Podcast(final PodcastInput podcastInput) {
        this.name = podcastInput.getName();
        this.owner = podcastInput.getOwner();
        for (int i = 0; i < podcastInput.getEpisodes().size(); i++) {
            episodesFull.add(new PodcastEpisode(podcastInput.getEpisodes().get(i)));
        }
    }

    public Podcast(final String owner, final String name,
                   final ArrayList<PodcastEpisode> episodes) {
        this.owner = owner;
        this.name = name;
        this.episodesFull = episodes;
        for (PodcastEpisode ep : episodes) {
            this.episodes.add(ep.getName());
        }
    }

    /** increments or decrements the number of users using this podcast */
    public void usingThis(final int usingValue) {
        numUsingThis = numUsingThis + usingValue;
    }

    /** checks if this podcast has 2 episodes with the same name */
    public boolean hasEpisodeTwice() {
        for (int i = 0; i < this.episodes.size(); i++) {
            for (int j = i + 1; j < this.episodes.size(); j++) {
                if (episodes.get(i).equals(episodes.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }
}
