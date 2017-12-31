package pl.klolo.pomodoro.logic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DurationManagerTest {

    private DurationManager durationManager;

    @Before
    public void setUp() {
        durationManager = new DurationManager();
        durationManager.applicationSettings = new ApplicationSettings();
    }

    @Test
    public void shouldBCorrectlyInitialized() {
        Assert.assertEquals(durationManager.getStatus(), DurationManager.Status.IDLE);
    }

    @Test
    public void nextStatus() {
        for (int i = 1; i < durationManager.getMaxPomodoroInSeries(); i++) {
            durationManager.nextStatus();
            Assert.assertEquals(durationManager.getStatus(), DurationManager.Status.POMODORO);
            Assert.assertEquals(durationManager.getNextStatus(), DurationManager.Status.SHORT_BREAK);

            durationManager.nextStatus();
            Assert.assertEquals(durationManager.getStatus(), DurationManager.Status.SHORT_BREAK);
            Assert.assertEquals(durationManager.getNextStatus(), DurationManager.Status.POMODORO);
        }

        durationManager.nextStatus();
        Assert.assertEquals(durationManager.getStatus(), DurationManager.Status.POMODORO);
        Assert.assertEquals(durationManager.getNextStatus(), DurationManager.Status.LONG_BREAK);

        durationManager.nextStatus();
        Assert.assertEquals(durationManager.getStatus(), DurationManager.Status.LONG_BREAK);
        Assert.assertEquals(durationManager.getNextStatus(), DurationManager.Status.POMODORO);
    }
}