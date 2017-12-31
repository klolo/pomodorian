package pl.klolo.pomodoro.logic;

public enum Sound {
    START("sound/63907__robinhood76__gc001-spaced-chords.wav"),
    TIMEOUT("sound/201819__90russianchanson__shanson-gt.wav");

    final String fileName;

    Sound(final String fileName) {
        this.fileName = fileName;
    }
}
