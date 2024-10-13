package misc;

import lombok.Getter;
import lombok.Setter;
import tools.MusicPlayer;

@Getter @Setter
public class Stats {
    private String name;
    private int remainedTime;
    private String repeat;
    private boolean shuffle;
    private boolean paused;

    public Stats(final MusicPlayer player) {
        this.name = player.getLoaded();
        this.remainedTime = player.getRemainingTime();
        this.shuffle = player.isShuffle();
        this.paused = player.isPaused();
        switch (player.getRepeat()) {
            case 1:
                if (player.getWhatIsLoaded() == 1) {
                    repeat = "Repeat All";
                } else {
                    repeat = "Repeat Once";
                }
                break;

            case 2:
                if (player.getWhatIsLoaded() == 1) {
                    repeat = "Repeat Current Song";
                } else {
                    repeat = "Repeat Infinite";
                }
                break;

            default:
                repeat = "No Repeat";
                break;

        }
    }
}
