package tools;

import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.User;
import entities.Artist;
import entities.Host;
import entities.PodcastEpisode;
import entities.Song;
import entitycolections.Album;
import entitycolections.Playlist;
import entitycolections.Podcast;
import lombok.Getter;
import lombok.Setter;
import misc.PremiumStatus;
import misc.Status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

@Getter @Setter
public final class MusicPlayer {
    private boolean paused;
    private boolean shuffle;
    private int repeat = 0;
    private String loaded = "";
    private int remainingTime = 0;
    private int lastRecordedTime = 0;
    private Podcast crtPd;
    private PodcastEpisode crtEp;
    private int whatIsLoaded = -1;
    private Song crtSong;
    private Playlist crtPlaylist;
    private int lastEpTime;
    private ArrayList<Integer> shuffleList = new ArrayList<>();
    private int nowInPlaylist;
    private int ninety = 90;

    private User ownedBy;
    private User audioSourceUser = null;

    private ArrayList<Song> songHistory = new ArrayList<>();
    private ArrayList<Song> songHistoryAdd = new ArrayList<>();
    private ArrayList<Integer> adds = new ArrayList<>();
    private HashMap<PodcastEpisode, Integer> pastEp = new HashMap<>();

    public MusicPlayer(final User user) {
        this.ownedBy = user;
    }

    /** loads the audio sources specified by name, be it song, playlist or podcast*/
    public void load(final String name, final int ldTime, final int whatIsLoaded) {

        this.whatIsLoaded = whatIsLoaded;
        this.paused = false;
        this.shuffle = false;
        this.lastRecordedTime = ldTime;
        this.repeat = 0;

        if (whatIsLoaded == 0) {
            this.loaded = name;
            crtSong = DataBase.findSong(name);
            listenToSong();
            listenToAlbum(DataBase.findPlaylist(crtSong.getAlbum()));
            crtSong.usingThis(1);
            audioSourceUser = DataBase.findUser(crtSong.getArtist());
            remainingTime = crtSong.getDuration();
            crtPlaylist = null;
        }
        if (whatIsLoaded == 1) {
            crtPlaylist = DataBase.findPlaylist(name);
            crtPlaylist.usingThis(1);
            audioSourceUser = DataBase.findUser(crtPlaylist.getOwner());
            for (Song song : crtPlaylist.getSongsfull()) {
                song.usingThis(1);
            }
            crtSong = crtPlaylist.getSongsfull().get(0);
            listenToAlbum(crtPlaylist);
            listenToSong();
            remainingTime = crtSong.getDuration();
            loaded = crtSong.getName();
            for (int i = 0; i < crtPlaylist.getSongsfull().size(); i++) {
                shuffleList.add(i);
            }
            nowInPlaylist = 0;
        }
        if (whatIsLoaded == 2) {
            crtPd = DataBase.findPodcast(name);
            crtPd.usingThis(1);
            audioSourceUser = DataBase.findUser(crtPd.getOwner());

            boolean found = false;
            for (PodcastEpisode episode : crtPd.getEpisodesFull()) {
                if (pastEp.containsKey(episode)) {
                    crtEp = episode;
                    remainingTime = pastEp.get(crtEp);
                    pastEp.remove(crtEp);
                    found = true;
                    break;
                }
            }

//            if (crtPd.getEpisodesFull().contains(crtEp)) {
//                remainingTime = lastEpTime;
//            } else {
            if (!found) {
                crtEp = crtPd.getEpisodesFull().get(0);
                remainingTime = crtEp.getDuration();
            }

            listenToEpisode();
            loaded = crtEp.getName();
        }
        if (audioSourceUser != null) {
            audioSourceUser.useThis(1);
        }
    }

    private void listenToEpisode() {
        if (crtEp != null) {
            if (ownedBy.getListenedEpisodes().containsKey(crtEp.getName())) {
                Integer numListens = ownedBy.getListenedEpisodes().get(crtEp.getName());
                ownedBy.getListenedEpisodes().replace(crtEp.getName(), numListens + 1);
            } else {
                ownedBy.getListenedEpisodes().put(crtEp.getName(), 1);
            }

            Host host = (Host) DataBase.findUser(crtPd.getOwner());
            if (host == null) {
                host = DataBase.getTempHost(crtPd.getOwner());
                if (host == null) {
                    host = new Host(crtPd.getOwner(), "", 0);
                    DataBase.getDB().getTempHosts().add(host);
                }
            }
            host.addListenedEpisodes(crtEp);
            host.addFan(ownedBy);
            host.setListeners(host.getFans().size());
        }
    }

    private void listenToSong() {
        if (crtSong != null) {
            if (ownedBy.getListenedSongs().containsKey(crtSong.getName())) {
                Integer numListens = ownedBy.getListenedSongs().get(crtSong.getName());
                ownedBy.getListenedSongs().replace(crtSong.getName(), numListens + 1);
            } else {
                ownedBy.getListenedSongs().put(crtSong.getName(), 1);
            }
            if (ownedBy.getListenedGenres().containsKey(crtSong.getGenre())) {
                Integer numListens = ownedBy.getListenedGenres().get(crtSong.getGenre());
                ownedBy.getListenedGenres().replace(crtSong.getGenre(), numListens + 1);
            } else {
                ownedBy.getListenedGenres().put(crtSong.getGenre(), 1);
            }
            if (ownedBy.getListenedArtist().containsKey(crtSong.getArtist())) {
                Integer numListens = ownedBy.getListenedArtist().get(crtSong.getArtist());
                ownedBy.getListenedArtist().replace(crtSong.getArtist(), numListens + 1);
            } else {
                ownedBy.getListenedArtist().put(crtSong.getArtist(), 1);
            }

            DataBase.addListenedArtist(crtSong.getArtist());
            Artist artist = (Artist) DataBase.findUser(crtSong.getArtist());
            if (artist != null) {
                artist.addListenedSong(crtSong);
                artist.addFan(ownedBy);
            }

            if (ownedBy.getPrStatus().equals(PremiumStatus.premium)) {
                songHistory.add(crtSong);
            } else {
                songHistoryAdd.add(crtSong);
            }
        }
    }

    private void listenToAlbum(final Playlist playlist) {
        if (playlist != null) {
            if (DataBase.isAlbum(playlist)) {
                if (ownedBy.getListenedAlbums().containsKey(playlist.getName())) {
                    Integer numListens = ownedBy.getListenedAlbums().get(playlist.getName());
                    ownedBy.getListenedAlbums().replace(playlist.getName(), numListens + 1);
                } else {
                    ownedBy.getListenedAlbums().put(playlist.getName(), 1);
                }

                Artist artist = (Artist) DataBase.findUser(playlist.getOwner());
                if (artist != null) {
                    artist.addListenedAlbum((Album) playlist);
                    artist.addFan(ownedBy);
                }
            }
        }
    }

    /** continues the functionality of the player from the last time it was called*/
    public void updateTime(final int crtTime) {
        run(crtTime);
        while (remainingTime <= 0 && ownedBy.getStatus().equals(Status.online)
                            && whatIsLoaded != -1) {
            run(crtTime);
        }
    }

    /** pauses or resumes playing of the player*/
    public void playPause(final int crtTime) {
        updateTime(crtTime);
        if (paused) {
            lastRecordedTime = crtTime;
        } else {
            updateTime(crtTime);
        }
        paused = !paused;
    }

    /** unloads the audio files from the player*/
    public void unload() {
        if (audioSourceUser != null) {
            audioSourceUser.useThis(-1);
        }
        if (whatIsLoaded == 0) {
            crtSong.usingThis(-1);
        }
        if (whatIsLoaded == 1) {
            crtPlaylist.usingThis(-1);
            for (Song song : crtPlaylist.getSongsfull()) {
                song.usingThis(-1);
            }
        }
        if (whatIsLoaded == 2) {
            crtPd.usingThis(-1);
            pastEp.put(crtEp, remainingTime);

        }
        audioSourceUser = null;
        remainingTime = 0;
        repeat = 0;
        paused = true;
        loaded = "";
        crtSong = null;
        whatIsLoaded = -1;
        crtPlaylist = null;
        nowInPlaylist = 0;
        shuffleList.clear();
        shuffle = false;

        crtEp = null;
    }

    /** represents the player running for a single action and
     * decides what should be done if an audio fie has finished playing*/
    private void run(final int crtTime) {
        if (!paused && ownedBy.getStatus().equals(Status.online)) {
            remainingTime = remainingTime - crtTime + lastRecordedTime;
            if (remainingTime <= 0) {
                if (!adds.isEmpty()) {
                    loaded = "Ad Break";
                    remainingTime = remainingTime + 10;
                    int price = adds.get(0);
                    adds.remove(0);
                    DataBase.distributeProfits(songHistoryAdd, (double) price);
                    if (songHistoryAdd.size() > 0) {
                        songHistoryAdd.remove(songHistoryAdd.size() - 1);
                    }
                    songHistoryAdd.clear();
                } else {
                    switch (whatIsLoaded) {
                        case 0:
                            switch (repeat) {
                                case 0:
                                    unload();
                                    break;

                                case 1:
                                    remainingTime = remainingTime + crtSong.getDuration();
                                    loaded = crtSong.getName();
                                    repeat = 0;
                                    break;

                                case 2:
                                    remainingTime = remainingTime + crtSong.getDuration();
                                    loaded = crtSong.getName();
                                    break;

                                default:
                                    break;
                            }
                            listenToSong();
                            if (crtSong != null) {
                                listenToAlbum(DataBase.findPlaylist(crtSong.getAlbum()));
                            }
                            break;

                        case 1:
                            switch (repeat) {
                                case 0:
                                    if (nowInPlaylist == crtPlaylist.getSongsfull().size() - 1) {
                                        unload();
                                    } else {
                                        nowInPlaylist++;
                                        crtSong = crtPlaylist.getSongsfull().get(
                                                shuffleList.get(nowInPlaylist));
                                        remainingTime = remainingTime + crtSong.getDuration();
                                        loaded = crtSong.getName();
                                    }
                                    break;

                                case 1:
                                    if (nowInPlaylist == crtPlaylist.getSongsfull().size() - 1) {
                                        nowInPlaylist = 0;
                                        crtSong = crtPlaylist.getSongsfull().get(
                                                shuffleList.get(nowInPlaylist));
                                        remainingTime = remainingTime + crtSong.getDuration();
                                        loaded = crtSong.getName();
                                    } else {
                                        nowInPlaylist++;
                                        crtSong = crtPlaylist.getSongsfull().get(
                                                shuffleList.get(nowInPlaylist));
                                        remainingTime = remainingTime + crtSong.getDuration();
                                        loaded = crtSong.getName();
                                    }
                                    break;

                                case 2:
                                    remainingTime = remainingTime + crtSong.getDuration();
                                    loaded = crtSong.getName();

                                default:
                                    break;
                            }
                            listenToAlbum(crtPlaylist);
                            listenToSong();
                            break;

                        case 2:
                            switch (repeat) {
                                case 0:
                                    if (crtEp.equals(crtPd.getEpisodesFull().get(
                                            crtPd.getEpisodesFull().size() - 1))) {
                                        unload();
                                    } else {
                                        crtEp = crtPd.getEpisodesFull().get(
                                                crtPd.getEpisodesFull().indexOf(crtEp) + 1);
                                        remainingTime = remainingTime + crtEp.getDuration();
                                        loaded = crtEp.getName();
                                    }
                                    break;

                                case 1:
                                    remainingTime = remainingTime + crtEp.getDuration();
                                    repeat = 0;
                                    break;

                                case 2:
                                    remainingTime = remainingTime + crtEp.getDuration();
                                    break;

                                default:
                                    break;
                            }
                            listenToEpisode();
                            break;

                        default:
                            unload();
                            break;
                    }
                }
            }
        }
            lastRecordedTime = crtTime;
    }

    /** cycles through the repeat states */
    public void switchRepeat() {
        switch (repeat) {
            case 0:
                repeat = 1;
                break;

            case 1:
                repeat = 2;
                break;

            case 2:
                repeat = 0;
                break;

            default:
                break;
        }
    }

    /** moves the time forward by 90 seconds*/
    public void forward() {
        remainingTime = remainingTime - ninety;
        if (remainingTime < 0) {
            remainingTime = 0;
            crtEp = crtPd.getEpisodesFull().get(crtPd.getEpisodesFull().indexOf(crtEp) + 1);
            loaded = crtEp.getName();
        }
    }

    /** moves the time backwards by 90 seconds*/
    public void backwards() {
        remainingTime = remainingTime + ninety;
        if (remainingTime > crtEp.getDuration()) {
            remainingTime = crtEp.getDuration();
        }
    }

    /** skips to the next audio file*/
    public void next(final int crtTime) {
        paused = false;
        remainingTime = -1;
        updateTime(crtTime);
        if (!loaded.isEmpty()) {
            remainingTime = remainingTime + 1;
        }
    }

    /** moves to the previous audio file or plays the current one from the start,
     * depending on the situation*/
    public void prev() {
        paused = false;
        switch (whatIsLoaded) {
            case 0:
                remainingTime = crtSong.getDuration();
                break;

            case 1:
                if (nowInPlaylist == 0 || crtSong.getDuration() >= remainingTime + 1) {
                    remainingTime = crtSong.getDuration();
                } else {
                    nowInPlaylist--;
                    crtSong = crtPlaylist.getSongsfull().get(shuffleList.get(nowInPlaylist));
                    remainingTime = crtSong.getDuration();
                    loaded = crtSong.getName();
                }
                break;

            case 2:
                if (crtEp.equals(crtPd.getEpisodes().get(0))
                        || crtEp.getDuration() >= remainingTime + 1) {
                    remainingTime = crtEp.getDuration();
                } else {
                    crtEp = crtPd.getEpisodesFull().get(
                            (crtPd.getEpisodesFull().indexOf(crtEp) - 1));
                    remainingTime = crtEp.getDuration();
                    loaded = crtEp.getName();
                }
                break;

            default:
                break;
        }
    }

    /** shuffles the current playlist*/
    public boolean shuffle(final int seed) {
        shuffle = !shuffle;
        if (shuffle) {
            Collections.shuffle(shuffleList, new Random(seed));
            nowInPlaylist = shuffleList.indexOf(nowInPlaylist);
        } else {
            nowInPlaylist = shuffleList.get(nowInPlaylist);
            shuffleList.clear();
            for (int i = 0; i < crtPlaylist.getSongsfull().size(); i++) {
                shuffleList.add(i);
            }
        }
        return shuffle;
    }

    /** handles the play pause cases and returns the appropriate message */
    public void doPlayPause(final Command cmd, final ObjectNode node) {
        this.updateTime(cmd.getTimestamp());
        if (this.getLoaded().isEmpty()) {
            node.put("message",
                    "Please load a source before attempting to pause or resume playback.");
        } else {
            if (this.isPaused()) {
                node.put("message", "Playback resumed successfully.");
            } else {
                node.put("message", "Playback paused successfully.");
            }
            this.playPause(cmd.getTimestamp());
        }
    }

    /** handles the repeat status change cases and returns the appropriate message */
    public void doRepeat(final Command cmd, final ObjectNode node) {
        String repeatStatus = "";
        this.updateTime(cmd.getTimestamp());
        if (this.getLoaded().isEmpty()) {
            node.put("message", "Please load a source before setting the repeat status.");
        } else {
            this.switchRepeat();
            switch (this.getRepeat()) {
                case 0:
                    repeatStatus = "no repeat.";
                    break;

                case 1:
                    if (this.getWhatIsLoaded() == 1) {
                        repeatStatus = "repeat all.";
                    } else {
                        repeatStatus = "repeat once.";
                    }
                    break;

                case 2:
                    if (this.getWhatIsLoaded() == 1) {
                        repeatStatus = "repeat current song.";
                    } else {
                        repeatStatus = "repeat infinite.";
                    }
                    break;
                default:
                    break;
            }
            node.put("message", "Repeat mode changed to " + repeatStatus);
        }
    }

    /** handles the forward cases and returns the appropriate message */
    public void doForward(final Command cmd, final ObjectNode node) {
        updateTime(cmd.getTimestamp());
        if (this.getWhatIsLoaded() == -1) {
            node.put("message", "Please load a source before attempting to forward.");
        } else if (this.getWhatIsLoaded() != 2) {
            node.put("message", "The loaded source is not a podcast.");
        } else {
            node.put("message", "Skipped forward successfully.");
            this.forward();
        }
    }

    /** handles the backwards cases and returns the appropriate message */
    public void doBackwards(final Command cmd, final ObjectNode node) {
        updateTime(cmd.getTimestamp());
        if (this.getWhatIsLoaded() == -1) {
            node.put("message", "Please load a source before skipping forward.");
        } else if (this.getWhatIsLoaded() != 2) {
            node.put("message", "The loaded source is not a podcast.");
        } else {
            node.put("message", "Rewound successfully.");
            this.backwards();
        }
    }

    /** handles the next command cases and returns the appropriate message */
    public void doNext(final Command cmd, final ObjectNode node) {
        updateTime(cmd.getTimestamp());
        if (loaded.equals("Ad Break")) {
            node.put("message", "AD");
        } else {
            this.next(cmd.getTimestamp());
            if (this.getWhatIsLoaded() == -1) {
                node.put("message", "Please load a source before skipping to the next track.");
            } else {
                node.put("message",
                        "Skipped to next track successfully. The current track is "
                                + this.getLoaded() + ".");
            }
        }
    }

    /** handles the prev command cases and returns the appropriate message */
    public void doPrev(final Command cmd, final ObjectNode node) {
        updateTime(cmd.getTimestamp());
        if (this.getWhatIsLoaded() == -1) {
            node.put("message",
                    "Please load a source before returning to the previous track.");
        } else {
            this.prev();
            node.put("message",
                    "Returned to previous track successfully. The current track is "
                            + this.getLoaded() + ".");
        }
    }

    /** handles the shuffle cases and returns the appropriate message */
    public void doShuffle(final Command cmd, final ObjectNode node) {
        this.updateTime(cmd.getTimestamp());
        if (this.getWhatIsLoaded() == -1) {
            node.put("message", "Please load a source before using the shuffle function.");
        } else if (this.getWhatIsLoaded() != 1) {
            node.put("message", "The loaded source is not a playlist or an album.");
        } else {
            if (this.shuffle(cmd.getSeed())) {
                node.put("message", "Shuffle function activated successfully.");
            } else {
                node.put("message", "Shuffle function deactivated successfully.");
            }
        }
    }

    /** loads an ad into the player **/
    public void loadAdd(final Integer price, final int crtTime, final ObjectNode node) {
        updateTime(crtTime);
        if (whatIsLoaded == 1 || whatIsLoaded == 0) {
            adds.add(price);
            node.put("message", "Ad inserted successfully.");
        } else {
            node.put("message", ownedBy.getUsername() + " is not playing any music.");
        }
    }
}
