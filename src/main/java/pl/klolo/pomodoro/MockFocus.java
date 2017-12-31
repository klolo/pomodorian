package pl.klolo.pomodoro;

import pl.klolo.pomodoro.logic.Focus;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static java.time.temporal.ChronoUnit.DAYS;

public class MockFocus {
    /**
     *   FXCollections.observableList(applicationSettings
     .getFocuses()
     .stream()
     .map(Focus::getName)
     .collect(toList())
     )
     */
    public static Set<Focus> getMock() {
        final LocalDate now = LocalDate.now();
        Set<Focus> result = new HashSet<>();
        final Focus ocp = new Focus();
        ocp.setName("OCP");
        ocp.addTotalTime(25, now.minus(30, DAYS));
        ocp.addTotalTime(40, now.minus(20, DAYS));
        ocp.addTotalTime(40, now.minus(15, DAYS));
        ocp.addTotalTime(25, now.minus(12, DAYS));
        ocp.addTotalTime(15, now.minus(7, DAYS));
        ocp.addTotalTime(40, now.minus(5, DAYS));
        result.add(ocp);

        final Focus test = new Focus();
        test.setName("Niemiecki");
        test.addTotalTime(25, now.minus(40, DAYS));
        test.addTotalTime(60, now.minus(27, DAYS));
        test.addTotalTime(120, now.minus(10, DAYS));
        test.addTotalTime(15, now.minus(8, DAYS));
        test.addTotalTime(25, now.minus(3, DAYS));
        test.addTotalTime(25, now.minus(1, DAYS));
        result.add(test);

        return result;
    }
}
