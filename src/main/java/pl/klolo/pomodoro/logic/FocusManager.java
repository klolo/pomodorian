package pl.klolo.pomodoro.logic;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Slf4j
public class FocusManager {
    private static DateTimeFormatter focusDateFormater = DateTimeFormatter.ofPattern("dd-MM-YYYY");

    public JFXTextField focusField;

    public Label targetLabel;

    private ApplicationSettings applicationSettings;

    public FontAwesomeIconView targetLabelIcon;

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

            final JFXComboBox<String> comboBox = new JFXComboBox<>(options);
            comboBox.setStyle("-jfx-focus-color: #2b8fff");
            comboBox.minWidth(100);

            Label lblName = new Label("Select your target: ");
            lblName.setPadding(new Insets(10, 0, 10, 0));

            Label focuTotalWorkTime = new Label("Totla work time:");

            HBox selectRow = new HBox(lblName, comboBox);
            HBox.setHgrow(comboBox, Priority.ALWAYS);
            selectRow.setAlignment(Pos.CENTER_LEFT);

            VBox vBox = new VBox(selectRow, focuTotalWorkTime, getChart());
            vBox.setPadding(new Insets(10, 10, 10, 10));
            //Create PopOver and add look and feel
            PopOver popOver = new PopOver(vBox);
            popOver.show(targetLabelIcon);

            comboBox.valueProperty().setValue(targetLabel.getText());
            focuTotalWorkTime.setText("Total time: " + getCurrentFocus().getTotalTime());

            comboBox.valueProperty().addListener((obervable, oldValue, newValue) -> {
                popOver.hide();

                targetLabel.setText(newValue);
                focuTotalWorkTime.setText("Total time: " + getCurrentFocus().getTotalTime());
            });
        });
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

        final AreaChart<String, Number> areaChart = new AreaChart<>(xAxis, yAxis);
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
        areaChart.getData().add(focusSerie);

        return areaChart;
    }
}
