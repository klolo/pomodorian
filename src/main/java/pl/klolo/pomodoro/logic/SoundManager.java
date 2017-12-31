package pl.klolo.pomodoro.logic;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
public class SoundManager {

    @Autowired
    private ApplicationSettings applicationSettings;

    private MediaPlayer mediaPlayer;

    public void play(final Sound sound) {
        if (!applicationSettings.isPlaySounds()) {
            return;
        }

        stop();

        final URL songURL = getClass().getClassLoader().getResource(sound.fileName);

        Media media = new Media(songURL.toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
