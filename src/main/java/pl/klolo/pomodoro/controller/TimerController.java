package pl.klolo.pomodoro.controller;

import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.textfield.TextFields;
import org.reactfx.util.FxTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.klolo.pomodoro.Launch;
import pl.klolo.pomodoro.component.progress.fill.FillProgressIndicator;
import pl.klolo.pomodoro.engine.SpringFxmlLoader;
import pl.klolo.pomodoro.logic.ApplicationSettings;
import pl.klolo.pomodoro.logic.DurationManager;
import pl.klolo.pomodoro.logic.Focus;
import pl.klolo.pomodoro.logic.Timer;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

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
    public StackPane root;

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

    private Timer timer = new Timer(this::onTimerTick, this::initAfterFinishTimer);

    public void initialize() {
        timerLabel.setText(normalizeTime(durationManager.getDuration().getSeconds() / 60) + ":00");
        pane.setPinnedSide(Side.TOP);
        drawer.setMaxWidth(Launch.SCENE_WIDTH * 0.8);

        initializeMenuDrawer();

        stopButton.setOnMouseClicked(e -> onStopButton());
        stopButton.setGlyphStyle("-fx-fill: #777777");

        focusField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                afterSetTarget();
            }
        });

        startButton.setOnMouseClicked(e -> onStartPauseButton());
        progressTodo.setText(
                IntStream.range(0, durationManager.getMaxPomodoroInSeries())
                        .mapToObj(i -> "\u2022")
                        .collect(joining(" "))
        );

        TextFields.bindAutoCompletion(focusField, applicationSettings
                .getLatestFocus()
                .stream()
                .map(Focus::getName)
                .collect(toList())
        );
    }

    private void initializeMenuDrawer() {
        TranslateTransition openNav = new TranslateTransition(new Duration(350), drawer);
        openNav.setToX(-(drawer.getWidth()));
        TranslateTransition closeNav = new TranslateTransition(new Duration(350), drawer);

        StackPane.setAlignment(drawer, Pos.TOP_LEFT);
        menuButton.setOnMouseClicked(e -> onShowMenu(openNav, closeNav));

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
        initAfterFinishTimer();
        durationManager.nextStatus();
    }

    private void initAfterFinishTimer() {
        getCurrentFocus().setTotalTime(getCurrentFocus().getTotalTime() + durationManager.getDuration().toMinutes());

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

    private void resetDisplay() {
        stopButton.setGlyphStyle("-fx-fill: #777777");
        startButton.setGlyphName("PLAY");
        timerLabel.setText(normalizeTime(durationManager.getDuration().getSeconds() / 60) + ":00");
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
        if (applicationSettings.isAutoContinue()) {
            return;
        }

        final VBox root = (VBox) springFxmlLoader.load("fxml/fullscreen-info.fxml");
        final Stage stage = new Stage();

        stage.setTitle("Done");
        stage.setScene(new Scene(root));
        stage.show();
        stage.setAlwaysOnTop(true);
        stage.setFullScreen(true);
    }

    public void afterSetTarget() {
        pane.setVisible(true);
        focus.setVisible(false);
        targetLabel.setText(focusField.getText());

        applicationSettings.addFocus(focusField.getText());

        targetLabelIcon.setOnMouseClicked(e -> {
            ObservableList<String> options =
                    FXCollections.observableArrayList(
                            applicationSettings.getLatestFocus()
                                    .stream()
                                    .map(Focus::getName)
                                    .collect(Collectors.toList())
                    );

            final ComboBox<String> comboBox = new ComboBox<>(options);

            Label lblName = new Label("Select your target:");
            lblName.setPadding(new Insets(10, 0, 10, 0));

            Label focuTotalWorkTime = new Label("Totla work time:");

            VBox vBox = new VBox(lblName, comboBox, focuTotalWorkTime);
            vBox.setPadding(new Insets(10, 10, 10, 10));

            //Create PopOver and add look and feel
            PopOver popOver = new PopOver(vBox);
            popOver.show(targetLabelIcon);

            comboBox.valueProperty().setValue(targetLabel.getText());
            focuTotalWorkTime.setText(getCurrentFocus().getTotalTime() + "");

            comboBox.valueProperty().addListener((obervable, oldValue, newValue) -> {
                popOver.hide();

                targetLabel.setText(newValue);
                focuTotalWorkTime.setText(getCurrentFocus().getTotalTime() + "");
            });
        });
    }

    private Focus getCurrentFocus() {
        return applicationSettings.getLatestFocus()
                .stream()
                .filter(focus1 -> focus1.getName().equals(targetLabel.getText()))
                .findFirst()
                .get();
    }
}