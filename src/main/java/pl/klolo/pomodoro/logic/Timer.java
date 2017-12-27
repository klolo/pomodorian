package pl.klolo.pomodoro.logic;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
public class Timer {
    private Timeline timeline;

    private LocalTime pauseStartTime;

    private long pauseSeconds;

    private final LongBiConsumer onTickConsumer;

    private final Runnable onFinish;

    public Timer(LongBiConsumer onTickConsumer, Runnable onFinish) {
        this.onTickConsumer = onTickConsumer;
        this.onFinish = onFinish;
    }

    public void start(final Duration duration) {
        final LocalTime endTime = LocalTime.now().plus(duration);
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(javafx.util.Duration.seconds(1), e -> processTick(endTime)));
        timeline.playFromStart();
    }

    private void processTick(final LocalTime endTime) {
        final LocalTime now = LocalTime.now().minusSeconds(pauseSeconds);

        final long secondsToEnd = now.until(endTime, ChronoUnit.SECONDS);
        final long minutesToEnd = now.until(endTime, ChronoUnit.MINUTES);

        if (secondsToEnd <= 0) {
            timeline.stop();
            timeline = null;

            Optional.of(onFinish).ifPresent(Runnable::run);
            return;
        }

        Optional
                .ofNullable(onTickConsumer)
                .ifPresent(c -> c.accept(minutesToEnd, secondsToEnd));
    }

    public void pause() {
        log.info("pause");
        timeline.pause();
        pauseStartTime = LocalTime.now();
    }

    public void continueTimer() {
        log.info("continueTimer");
        timeline.play();
        pauseSeconds += pauseStartTime.until(LocalTime.now(), ChronoUnit.SECONDS);
    }

    public boolean isPause() {
        return timeline.getStatus() != Animation.Status.RUNNING;
    }

    public boolean isRun() {
        return timeline != null;
    }

    public void stop() {
        log.info("stop");
        Optional.of(onFinish).ifPresent(Runnable::run);
        Optional.of(timeline).ifPresent(Timeline::stop);
        timeline = null;
    }
}
