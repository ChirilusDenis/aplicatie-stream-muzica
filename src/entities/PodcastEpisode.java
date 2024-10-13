package entities;

import fileio.input.EpisodeInput;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PodcastEpisode extends AudioFile {
    private String description;

    public PodcastEpisode() { }
    public PodcastEpisode(final EpisodeInput episodeInput) {
        this.setName(episodeInput.getName());
        this.setDuration(episodeInput.getDuration());
        this.description = episodeInput.getDescription();
    }
}
