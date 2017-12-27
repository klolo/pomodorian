package pl.klolo.pomodoro.engine;


import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.klolo.pomodoro.Launch;

/**
 * Klasa pomocnicza do zmieniania scen z wykorzystaniem springa.
 */
@Slf4j
@Component
public class StageSwitch {
    @Autowired
    private SpringFxmlLoader loader;

    public void load(final String scenePathToLoad, final ActionEvent event) {
        final Scene oldScene = ((Node) event.getSource()).getScene();
        final Stage window = (Stage) oldScene.getWindow();
        final Scene scene = loadScene(scenePathToLoad);

        window.setScene(scene);
    }

    private Scene loadScene(final String scenePathToLoad) {
        log.info("Change stage to: {}", scenePathToLoad);

        return new Scene(
                (Parent) loader.load(scenePathToLoad),
                Launch.SCENE_WIDTH,
                Launch.SCENE_HEIGHT
        );
    }
}