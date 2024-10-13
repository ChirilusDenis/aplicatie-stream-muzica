package tools;

import entities.PodcastEpisode;
import entities.Song;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter
public class Command {
    private String command;
    private String username;
    private int age;
    private String city;
    private String name;
    private int releaseYear;
    private String description;
    private ArrayList<Song> songs;
    private ArrayList<PodcastEpisode> episodes;
    private String nextPage;
    private String date;
    private int price;
    private String type;
    private Filter filters;
    private int timestamp;
    private int itemNumber;
    private int seed;
    private String playlistName;
    private int playlistId;
    private String recommendationType;
}
