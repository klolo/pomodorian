package pl.klolo.pomodoro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import pl.klolo.pomodoro.engine.SpringFxmlLoader;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

@Slf4j
public class Launch extends Application {
    public static final double SCENE_WIDTH = 350;

    public static final double SCENE_HEIGHT = 500;

    public static void main(String[] args) {
        log.info("launch application");
        Application.launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        final SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader();
        final Scene scene = new Scene((Parent) springFxmlLoader.load("fxml/timer.fxml"), SCENE_WIDTH, SCENE_HEIGHT);
        scene.setFill(Color.TRANSPARENT);

        primaryStage.setScene(scene);
        initPrimaryStage(primaryStage);
    }

    private void initPrimaryStage(final Stage primaryStage) {
        primaryStage.setMinWidth(250);
        primaryStage.setMinHeight(350);
        primaryStage.setTitle("Podomorian");
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
    }
}
