package pl.klolo.pomodoro.controller;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.reactfx.util.FxTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.klolo.pomodoro.logic.DurationManager;
import pl.klolo.pomodoro.logic.Timer;

@Component
@Slf4j
public class FullScreenInfoController {
    @FXML
    public Label header;

    @FXML
    public HBox counterContainer;

    @Autowired
    private DurationManager durationManager;

    @FXML
    public VBox root;

    private Tile minutes;
    private Tile seconds;

    private Timer timer;

    private void initAfterFinishTimer() {

    }

    private void onTimerTick(long minutesToEnd, long secondsToEnd) {
        minutes.setDescription(String.format("%02d", minutesToEnd));
        seconds.setDescription(normalizeTime(secondsToEnd % 60));
    }

    private String normalizeTime(final long time) {
        return (time > 9 ? "" : "0") + time;
    }


    public void initialize() {
        timer = new Timer(this::onTimerTick, this::initAfterFinishTimer);
        header.setText("It is time for " + durationManager.getNextStatus().toString());
        root.requestFocus();
        FxTimer.runLater(java.time.Duration.ofMillis(2_000), () -> root.setOnMouseClicked(e -> closeScene()));
        FxTimer.runLater(java.time.Duration.ofMillis(60_000), this::closeScene);

        minutes = createTile("MINUTES", "0");
        seconds = createTile("SECONDS", "0");

        timer.start(durationManager.getDurationForStatus(durationManager.getNextStatus()));

        final Region region = new Region();
        region.setMinWidth(10);
        counterContainer.getChildren().addAll(minutes, region, seconds);
    }

    private void closeScene() {
        Stage stage = (Stage) root.getScene().getWindow();

        stage.hide();
        stage.setFullScreen(false);

        FxTimer.runLater(java.time.Duration.ofMillis(50), stage::close);
    }

    private Tile createTile(final String TITLE, final String TEXT) {
        return TileBuilder.create().skinType(Tile.SkinType.CHARACTER)
                .prefSize(200, 200)
                .title(TITLE)
                .titleAlignment(TextAlignment.CENTER)
                .description(TEXT)
                .build();
    }
}
