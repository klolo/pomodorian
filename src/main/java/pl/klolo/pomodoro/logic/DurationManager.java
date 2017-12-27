package pl.klolo.pomodoro.logic;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
public class DurationManager {

    @Autowired
    private ApplicationSettings applicationSettings;

    private Status status = Status.IDLE;

    @Getter
    private int pomodoroCounter = 0;

    @Getter
    private final int maxPomodoroInSeries = 4;

    public enum Status {
        IDLE,
        POMODORO {
            @Override
            public String toString() {
                return "work";
            }
        },
        SHORT_BREAK {
            @Override
            public String toString() {
                return "short break";
            }
        },
        LONG_BREAK {
            @Override
            public String toString() {
                return "long break";
            }
        }
    }

    public void resetStatus() {
        status = Status.IDLE;
    }

    public Status getNextStatus() {
        switch (status) {
            case IDLE:
                return Status.POMODORO;
            case POMODORO:
                return Status.SHORT_BREAK;
            case SHORT_BREAK:
                return Status.POMODORO;
            case LONG_BREAK:
                return Status.POMODORO;
            default:
                return Status.IDLE;
        }
    }

    public void nextStatus() {
        switch (status) {
            case IDLE:
                status = Status.POMODORO;
                break;
            case POMODORO:
                pomodoroCounter++;
                if (pomodoroCounter == maxPomodoroInSeries) {
                    status = Status.LONG_BREAK;
                    break;
                }

                status = Status.SHORT_BREAK;
                break;
            case SHORT_BREAK:
                status = Status.POMODORO;
                break;
            case LONG_BREAK:
                pomodoroCounter = 0;
                status = Status.POMODORO;
                break;
            default:
                status = Status.IDLE;
                break;
        }
    }

    public Status getStatus() {
        return status;
    }

    public Duration getDuration() {
        final ChronoUnit unit = ChronoUnit.MINUTES;

        switch (status) {
            case IDLE:
            case POMODORO:
                return Duration.of(applicationSettings.getWorkTime(), unit);
            case SHORT_BREAK:
                return Duration.of(applicationSettings.getShortPauseTime(), unit);
            case LONG_BREAK:
                return Duration.of(applicationSettings.getLongPauseTime(), unit);
            default:
                return Duration.of(applicationSettings.getWorkTime(), unit);
        }
    }
}
