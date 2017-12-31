package pl.klolo.pomodoro.controller;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.image.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.klolo.pomodoro.engine.StageSwitch;
import pl.klolo.pomodoro.logic.ApplicationSettings;

@Component
public class SettingsController {
    @FXML
    public JFXSlider workTime;

    @FXML
    public JFXSlider shortPauseTime;

    @FXML
    public JFXSlider longPauseTime;

    @Autowired
    public ApplicationSettings applicationSettings;

    @FXML
    public JFXToggleButton autoContinueCheckBox;

    public void initialize() {
        autoContinueCheckBox.setSelected(false);
        autoContinueCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> applicationSettings.setAutoContinue(newValue));

        workTime.valueProperty().addListener((observable, oldValue, newValue) -> applicationSettings.setWorkTime(newValue.intValue()));
        workTime.setValue(applicationSettings.getWorkTime());

        shortPauseTime.valueProperty().addListener((observable, oldValue, newValue) -> applicationSettings.setShortPauseTime(newValue.intValue()));
        shortPauseTime.setValue(applicationSettings.getShortPauseTime());

        longPauseTime.valueProperty().addListener((observable, oldValue, newValue) -> applicationSettings.setLongPauseTime(newValue.intValue()));
        longPauseTime.setValue(applicationSettings.getLongPauseTime());
    }
}
