package pl.klolo.pomodoro.controller;

import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.Notifications;
import org.reactfx.util.FxTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.klolo.pomodoro.Launch;
import pl.klolo.pomodoro.component.progress.fill.FillProgressIndicator;
import pl.klolo.pomodoro.engine.SpringFxmlLoader;
import pl.klolo.pomodoro.logic.*;

import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

@Component
@Slf4j
public class TimerController {
    @FXML
    public FontAwesomeIconView menuButton;

    @FXML
    public VBox focus;

    @FXML
    public JFXTextField focusField;

    @FXML
    public Label targetLabel;

    @FXML
    public Label progressDone;

    @FXML
    public Label progressTodo;

    @FXML
    public FontAwesomeIconView targetLabelIcon;

    @Autowired
    private ApplicationSettings applicationSettings;

    @FXML
    public FillProgressIndicator indicator;

    @FXML
    public FontAwesomeIconView startButton;

    @FXML
    public FontAwesomeIconView stopButton;

    @FXML
    public VBox root;

    @FXML
    public Label statusLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private HiddenSidesPane pane;

    @FXML
    private VBox drawer;

    @Autowired
    private DurationManager durationManager;

    @Autowired
    private SpringFxmlLoader springFxmlLoader;

    @Autowired
    private SoundManager soundManager;

    private FocusManager focusManager;

    private Timer timer = new Timer(this::onTimerTick, this::initAfterFinishTimer);

    public void initialize() {
        focusManager = new FocusManager(focusField, targetLabel, applicationSettings, targetLabelIcon);
        focusManager.init();

        timerLabel.setText(normalizeTime(durationManager.getDuration().getSeconds() / 60) + ":00");
        pane.setPinnedSide(Side.TOP);
        drawer.setMaxWidth(Launch.SCENE_WIDTH * 0.8);

        initializeMenuDrawer();

        stopButton.setOnMouseClicked(e -> onStopButton());
        stopButton.setGlyphStyle("-fx-fill: #777777");

        focusField.setOnKeyPressed(ke -> {
            if (!ke.getCode().equals(KeyCode.ENTER)) {
                return;
            }

            pane.setVisible(true);
            focus.setVisible(false);
            focusManager.afterSetTarget();
        });

        startButton.setOnMouseClicked(e -> onStartPauseButton());
        progressTodo.setText(
                IntStream.range(0, durationManager.getMaxPomodoroInSeries())
                        .mapToObj(i -> "\u2022")
                        .collect(joining(" "))
        );
    }

    private void initializeMenuDrawer() {
        TranslateTransition openNav = new TranslateTransition(new Duration(350), drawer);
        openNav.setToX(-(drawer.getWidth()));
        TranslateTransition closeNav = new TranslateTransition(new Duration(350), drawer);

        StackPane.setAlignment(drawer, Pos.TOP_LEFT);
        menuButton.setOnMouseClicked(e -> onShowMenu(openNav, closeNav));
        root.setOnMouseClicked(e -> closeMenu(closeNav));

        FxTimer.runLater(java.time.Duration.ofMillis(250), () -> closeMenu(closeNav));
    }

    private void onShowMenu(TranslateTransition openNav, TranslateTransition closeNav) {
        if (drawer.getTranslateX() != 0) {
            openNav.play();
        }
        else {
            closeMenu(closeNav);
        }
    }

    private void closeMenu(TranslateTransition closeNav) {
        closeNav.setToX(-(drawer.getWidth()));
        closeNav.play();
    }

    public void handleMouseClicked() {
        if (pane.getPinnedSide() != null) {
            pane.setPinnedSide(null);
        }
        else {
            pane.setPinnedSide(Side.TOP);
        }
    }

    public void onStartPauseButton() {
        stopButton.setGlyphStyle("-fx-fill: #f3f3f3");
        durationManager.nextStatus();

        // start
        if (!timer.isRun()) {
            start();
            return;
        }

        // continue
        if (timer.isPause()) {
            timer.continueTimer();
            startButton.setGlyphName("PAUSE");
        }
        else {
            timer.pause();
            startButton.setGlyphName("PLAY");
        }
    }

    private void start() {
        timer.start(durationManager.getDuration());
        indicator.setProgress(0);

        startButton.setGlyphName("PAUSE");
        showMessage("It is time for ", durationManager.getStatus().toString());
        statusLabel.setText(durationManager.getStatus().toString());

        soundManager.play(Sound.START);
        changeProgressDots();
    }

    private void changeProgressDots() {
        if (!durationManager.getStatus().equals(DurationManager.Status.POMODORO)) {
            return;
        }

        progressDone.setText(
                IntStream.range(0, durationManager.getPomodoroCounter() + 1)
                        .mapToObj(i -> "\u2022")
                        .collect(joining(" ", "", " "))
        );
        progressTodo.setText(
                IntStream.range(0, durationManager.getMaxPomodoroInSeries() - durationManager.getPomodoroCounter() - 1)
                        .mapToObj(i -> "\u2022")
                        .collect(joining(" "))
        );
    }

    private void onTimerTick(final long minutesToEnd, final Long secondsToEnd) {
        timerLabel.setText(normalizeTime(minutesToEnd) + ":" + normalizeTime(secondsToEnd % 60));
        indicator.setProgress(calculateProgress(secondsToEnd));
    }

    private String normalizeTime(final long time) {
        return (time > 9 ? "" : "0") + time;
    }

    private int calculateProgress(float seconds) {
        return 100 - (int) ((seconds / (float) durationManager.getDuration().getSeconds()) * 100);
    }

    public void onStopButton() {
        if (!timer.isRun()) {
            return;
        }

        timer.stop();
        durationManager.resetStatus();
    }

    private void initAfterFinishTimer() {
        if (!timer.isBroken()) {
            soundManager.play(Sound.TIMEOUT);
            remeberFocusWorkTime();
        }

        showFullScreenInfo();
        resetDisplay();

        statusLabel.setText(durationManager.getNextStatus().toString());

        if (applicationSettings.isAutoContinue()) {
            onStartPauseButton();
        }
        else {
            showMessage("Time for break", "Press start");
        }
    }

    private void remeberFocusWorkTime() {
        if (durationManager.getStatus() == DurationManager.Status.POMODORO) {
            getCurrentFocus().addTotalTime(durationManager.getDuration().toMinutes());
        }
    }

    private void resetDisplay() {
        stopButton.setGlyphStyle("-fx-fill: #777777");
        startButton.setGlyphName("PLAY");
        final java.time.Duration nextDuration = durationManager.getDurationForStatus(durationManager.getNextStatus());
        timerLabel.setText(normalizeTime(nextDuration.getSeconds() / 60) + ":00");
        indicator.setProgress(0);
    }

    private void showMessage(final String title, final String msg) {
        if (!applicationSettings.isAutoContinue()) {
            return;
        }

        Notifications.create()
                .title("Pomodorian")
                .text(title + msg)
                .show();
    }

    private void showFullScreenInfo() {
        if (applicationSettings.isAutoContinue() || timer.isBroken()) {
            return;
        }

        final VBox root = (VBox) springFxmlLoader.load("fxml/fullscreen-info.fxml");
        final Stage stage = new Stage();

        stage.setTitle("Done");
        stage.setScene(new Scene(root));
        stage.show();
        stage.setAlwaysOnTop(true);
        stage.setFullScreen(true);
        stage.requestFocus();
    }

    private Focus getCurrentFocus() {
        return applicationSettings.getFocuses().stream()
                .filter(focus1 -> focus1.getName().equals(targetLabel.getText()))
                .findFirst()
                .get();
    }

    public void afterSetTarget() {
        pane.setVisible(true);
        focus.setVisible(false);
        focusManager.afterSetTarget();
    }
}
