package pl.klolo.pomodoro.logic;

import lombok.Data;

import java.io.Serializable;

@Data
public class Focus implements Serializable, Comparable<Focus> {

    private String name;

    private long totalTime;

    @Override
    public int compareTo(Focus o) {
        return name.compareTo(o.getName());
    }
}
