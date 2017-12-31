package pl.klolo.pomodoro.logic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.klolo.pomodoro.MockFocus;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.util.*;

@Component
@Getter
@Setter
@Slf4j
@ToString
public class ApplicationSettings implements Serializable {

    private boolean autoContinue = false;

    private int workTime = 25;

    private int shortPauseTime = 5;

    private int longPauseTime = 15;

    private Set<Focus> focuses = new TreeSet<>();

    private void loadSettings(ApplicationSettings base) {
        autoContinue = base.autoContinue;
        workTime = base.workTime;
        shortPauseTime = base.shortPauseTime;
        longPauseTime = base.longPauseTime;
        focuses = base.focuses;
    }

    public void addFocus(final String newFocus) {
        final Focus focus = new Focus();
        focus.setName(newFocus);
        focuses.add(focus);
    }

    @PostConstruct
    public void postConstruct() {
        try {
            FileInputStream fout = new FileInputStream(".pomodorian.data");
            ObjectInputStream oos = new ObjectInputStream(fout);
            ApplicationSettings settings = (ApplicationSettings) oos.readObject();

            log.info("Loaded settings: {}", settings);
            loadSettings(settings);
        }
        catch (IOException | ClassNotFoundException e) {
            log.error("Cannot save settings", e);
        }
    }

    @PreDestroy
    public void preDestroy() {
        try {
            FileOutputStream fout = new FileOutputStream(".pomodorian.data");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(this);
        }
        catch (IOException e) {
            log.error("Cannot save settings", e);
        }
    }
}
