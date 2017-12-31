package pl.klolo.pomodoro.logic;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.textfield.TextFields;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
public class FocusManager {
    public JFXTextField focusField;

    public Label targetLabel;

    private ApplicationSettings applicationSettings;

    public FontAwesomeIconView targetLabelIcon;

    private AreaChart<String, Number> chart;

    public FocusManager(JFXTextField focusField, Label targetLabel, ApplicationSettings applicationSettings, FontAwesomeIconView targetLabelIcon) {
        this.focusField = focusField;
        this.targetLabel = targetLabel;
        this.applicationSettings = applicationSettings;
        this.targetLabelIcon = targetLabelIcon;
    }

    public void init() {
        TextFields.bindAutoCompletion(focusField, applicationSettings
                .getFocuses()
                .stream()
                .map(Focus::getName)
                .collect(toList())
        );
    }

    public void afterSetTarget() {
        targetLabel.setText(focusField.getText());
        applicationSettings.addFocus(focusField.getText());

        targetLabelIcon.setOnMouseClicked(e -> {
            ObservableList<String> options =
                    FXCollections.observableArrayList(
                            applicationSettings.getFocuses()
                                    .stream()
                                    .map(Focus::getName)
                                    .collect(Collectors.toList())
                    );

            final JFXComboBox<String> comboBox = createSelectFocusCombo(options);

            Label lblName = new Label("Select your target: ");
            lblName.setPadding(new Insets(10, 0, 10, 0));

            Label focuTotalWorkTime = new Label("Totla work time:");

            HBox selectRow = new HBox(lblName, comboBox);
            HBox.setHgrow(comboBox, Priority.ALWAYS);
            selectRow.setAlignment(Pos.CENTER_LEFT);

            chart = getChart();
            fillChart();

            VBox vBox = new VBox(selectRow, focuTotalWorkTime, chart);
            vBox.setPadding(new Insets(10, 10, 10, 10));

            PopOver popOver = new PopOver(vBox);
            popOver.show(targetLabelIcon);

            comboBox.valueProperty().setValue(targetLabel.getText());
            focuTotalWorkTime.setText("Total time: " + getCurrentFocus().getTotalTime());

            comboBox.valueProperty().addListener((obervable, oldValue, newValue) -> {
                targetLabel.setText(newValue);
                focuTotalWorkTime.setText("Total time: " + getCurrentFocus().getTotalTime());
                fillChart();
            });
        });
    }

    private void fillChart() {
        chart.getData().clear();
        final Focus currentFocus = getCurrentFocus();

        XYChart.Series<String, Number> focusSerie = new XYChart.Series<>();
        focusSerie.setName(currentFocus.getName() + " spend time");

        final List<Map.Entry<LocalDate, Integer>> entries = currentFocus
                .getWorkDate()
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .collect(toList());

        long focusTimeSum = 0;
        for (val currentDate : entries) {
            final LocalDate focusDate = currentDate.getKey();
            focusTimeSum += currentDate.getValue();

            focusSerie.getData().add(new XYChart.Data<>(focusDate.toString(), focusTimeSum));
        }

        focusSerie.getData().sort(Comparator.comparing(XYChart.Data::getXValue));
        chart.getData().add(focusSerie);
    }

    private JFXComboBox<String> createSelectFocusCombo(ObservableList<String> options) {
        final JFXComboBox<String> comboBox = new JFXComboBox<>(options);
        comboBox.setStyle("-jfx-focus-color: #2b8fff");
        comboBox.minWidth(100);
        return comboBox;
    }

    private Focus getCurrentFocus() {
        return applicationSettings.getFocuses()
                .stream()
                .filter(focus1 -> focus1.getName().equals(targetLabel.getText()))
                .findFirst()
                .get();
    }

    private AreaChart<String, Number> getChart() {
        final Axis<String> xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("minutes");
        return new AreaChart<>(xAxis, yAxis);
    }
}
