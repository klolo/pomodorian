package pl.klolo.pomodoro.logic;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class Focus implements Serializable, Comparable<Focus> {

    private String name;

    private Map<LocalDate, Integer> workDate = new HashMap<>();

    public void addTotalTime(final long time) {
        addTotalTime(time, LocalDate.now());
    }

    public void addTotalTime(final long time, final LocalDate localDate) {
        if (!workDate.containsKey(localDate)) {
            workDate.put(localDate, 0);
        }

        workDate.put(localDate, (int) (workDate.get(localDate) + time));
    }


    public long getTotalTime() {
        return workDate.values()
                .stream()
                .mapToInt(i -> i)
                .sum();
    }

    public int compareTo(Focus o) {
        return name.compareTo(o.getName());
    }
}
