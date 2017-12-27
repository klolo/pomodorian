package pl.klolo.pomodoro.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.reactfx.util.FxTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.klolo.pomodoro.logic.DurationManager;

@Component
@Slf4j
public class FullScreenInfoController {
    @FXML
    public FontAwesomeIconView closeButton;

    @FXML
    public Label header;

    @Autowired
    private DurationManager durationManager;

    @FXML
    public VBox root;

    public void initialize() {
        header.setText("It is time for " + durationManager.getNextStatus().toString());
        FxTimer.runLater(java.time.Duration.ofMillis(3_000), () -> root.setOnMouseClicked(e -> closeScene()));
    }

    private void closeScene() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setFullScreen(false);
        FxTimer.runLater(java.time.Duration.ofMillis(50), stage::close);
    }
}
