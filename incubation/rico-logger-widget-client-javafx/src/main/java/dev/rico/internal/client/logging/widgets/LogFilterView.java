/*
 * Copyright 2018 Karakun AG.
 * Copyright 2015-2018 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.rico.internal.client.logging.widgets;

import dev.rico.internal.core.Assert;
import dev.rico.internal.logging.LoggerSearchRequest;
import dev.rico.internal.logging.spi.LogMessage;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.textfield.CustomTextField;
import org.slf4j.event.Level;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class LogFilterView extends HBox {

    private final Property<LoggerSearchRequest> loggerSearchRequest;

    private final ComboBox<Level> levelComboBox;

    private final Spinner<Integer> maxResultsSpinner;

    private final CustomTextField startDateTimeLabel;

    private final DatePicker startDatePicker;

    private final Spinner<Integer> startHourSpinner;

    private final Spinner<Integer> startMinuteSpinner;

    private final Spinner<Integer> startSecondSpinner;

    private final Spinner<Integer> startMilliSpinner;

    private final CustomTextField endDateTimeLabel;

    private final DatePicker endDatePicker;

    private final Spinner<Integer> endHourSpinner;

    private final Spinner<Integer> endMinuteSpinner;

    private final Spinner<Integer> endSecondSpinner;

    private final Spinner<Integer> endMilliSpinner;

    public LogFilterView() {
        loggerSearchRequest = new SimpleObjectProperty<>();
        loggerSearchRequest.addListener(e -> updateUI());

        levelComboBox = new ComboBox<>();
        levelComboBox.getItems().addAll(Level.values());
        levelComboBox.getItems().add(null);
        levelComboBox.setOnAction(e -> updateItem());

        maxResultsSpinner = new Spinner<>(0, Integer.MAX_VALUE, 1_000);
        maxResultsSpinner.getEditor().setPrefColumnCount(8);
        maxResultsSpinner.valueProperty().addListener(e -> updateItem());

        final FontAwesomeIconView startTimeIconView = new FontAwesomeIconView(FontAwesomeIcon.CLOCK_ALT);
        startDateTimeLabel = new CustomTextField();
        startDateTimeLabel.setRight(startTimeIconView);
        startDateTimeLabel.setPrefColumnCount(15);
        startDateTimeLabel.setEditable(false);

        final FontAwesomeIconView endTimeIconView = new FontAwesomeIconView(FontAwesomeIcon.CLOCK_ALT);
        endDateTimeLabel = new CustomTextField();
        endDateTimeLabel.setRight(endTimeIconView);
        endDateTimeLabel.setPrefColumnCount(15);
        endDateTimeLabel.setEditable(false);

        startDatePicker = new DatePicker(LocalDate.now());
        startDatePicker.getEditor().setPrefColumnCount(8);
        startDatePicker.valueProperty().addListener(e -> updateItem());
        startDatePicker.valueProperty().addListener(e -> updateStartDateLabel());
        startHourSpinner = new Spinner<>(0, 23, LocalTime.now().getHour());
        startHourSpinner.getEditor().setPrefColumnCount(4);
        startHourSpinner.valueProperty().addListener(e -> updateItem());
        startHourSpinner.valueProperty().addListener(e -> updateStartDateLabel());
        startMinuteSpinner = new Spinner<>(0, 59, LocalTime.now().getMinute());
        startMinuteSpinner.getEditor().setPrefColumnCount(4);
        startMinuteSpinner.valueProperty().addListener(e -> updateItem());
        startMinuteSpinner.valueProperty().addListener(e -> updateStartDateLabel());
        startSecondSpinner = new Spinner<>(0, 59, 0);
        startSecondSpinner.getEditor().setPrefColumnCount(4);
        startSecondSpinner.valueProperty().addListener(e -> updateItem());
        startSecondSpinner.valueProperty().addListener(e -> updateStartDateLabel());
        startMilliSpinner = new Spinner<>(0, 999, 0);
        startMilliSpinner.getEditor().setPrefColumnCount(4);
        startMilliSpinner.valueProperty().addListener(e -> updateItem());
        startMilliSpinner.valueProperty().addListener(e -> updateStartDateLabel());

        updateStartDateLabel();

        endDatePicker = new DatePicker(LocalDate.now());
        endDatePicker.getEditor().setPrefColumnCount(8);
        endDatePicker.valueProperty().addListener(e -> updateItem());
        endDatePicker.valueProperty().addListener(e -> updateEndDateLabel());
        endHourSpinner = new Spinner<>(0, 23, LocalTime.now().getHour());
        endHourSpinner.getEditor().setPrefColumnCount(4);
        endHourSpinner.valueProperty().addListener(e -> updateItem());
        endHourSpinner.valueProperty().addListener(e -> updateEndDateLabel());
        endMinuteSpinner = new Spinner<>(0, 59, LocalTime.now().getMinute());
        endMinuteSpinner.getEditor().setPrefColumnCount(4);
        endMinuteSpinner.valueProperty().addListener(e -> updateItem());
        endMinuteSpinner.valueProperty().addListener(e -> updateEndDateLabel());
        endSecondSpinner = new Spinner<>(0, 59, 0);
        endSecondSpinner.getEditor().setPrefColumnCount(4);
        endSecondSpinner.valueProperty().addListener(e -> updateItem());
        endSecondSpinner.valueProperty().addListener(e -> updateEndDateLabel());
        endMilliSpinner = new Spinner<>(0, 999, 0);
        endMilliSpinner.getEditor().setPrefColumnCount(4);
        endMilliSpinner.valueProperty().addListener(e -> updateItem());
        endMilliSpinner.valueProperty().addListener(e -> updateEndDateLabel());

        updateEndDateLabel();

        startDateTimeLabel.setOnMouseClicked(e -> {
            final HBox content = new HBox(startDatePicker, startHourSpinner, startMinuteSpinner, startSecondSpinner, startMilliSpinner);
            content.setPadding(new Insets(0, 14, 0, 14));
            content.setAlignment(Pos.CENTER);
            final PopOver popOver = new PopOver(content);
            popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
            popOver.show(startDateTimeLabel);
        });

        endDateTimeLabel.setOnMouseClicked(e -> {
            final HBox content = new HBox(endDatePicker, endHourSpinner, endMinuteSpinner, endSecondSpinner, endMilliSpinner);
            content.setPadding(new Insets(0, 14, 0, 14));
            content.setAlignment(Pos.CENTER);
            final PopOver popOver = new PopOver(content);
            popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
            popOver.show(endDateTimeLabel);
        });


        final HBox levelBox = new HBox(new Label("Level:"), levelComboBox);
        levelBox.setAlignment(Pos.BASELINE_LEFT);
        levelBox.setSpacing(4);

        final HBox maxResultsBox = new HBox(new Label("max results:"), maxResultsSpinner);
        maxResultsBox.setAlignment(Pos.BASELINE_LEFT);
        maxResultsBox.setSpacing(4);

        final HBox startDateBox = new HBox(new Label("Start:"), startDateTimeLabel);
        startDateBox.setAlignment(Pos.BASELINE_LEFT);
        startDateBox.setSpacing(4);


        final HBox endDateBox = new HBox(new Label("End:"), endDateTimeLabel);
        endDateBox.setAlignment(Pos.BASELINE_LEFT);
        endDateBox.setSpacing(4);

        getChildren().addAll(levelBox, maxResultsBox, startDateBox, endDateBox);
        setAlignment(Pos.BASELINE_LEFT);
        setPadding(new Insets(8));
        setSpacing(18);
    }

    private void updateStartDateLabel() {
        final LocalDate startDate = startDatePicker.getValue();
        final LocalDateTime localDateTime = LocalDateTime.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(), startHourSpinner.getValue(), startMinuteSpinner.getValue(), startSecondSpinner.getValue(), startMilliSpinner.getValue() * 1_000_000);
        final String timestamp = DateTimeFormatter.ofPattern("dd/MM/yyyy - hh:mm:ss.SSS").format(localDateTime);
        startDateTimeLabel.setText(timestamp);
    }

    private void updateEndDateLabel() {
        final LocalDate endDate = endDatePicker.getValue();
        final LocalDateTime localDateTime = LocalDateTime.of(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth(), endHourSpinner.getValue(), endMinuteSpinner.getValue(), endSecondSpinner.getValue(), endMilliSpinner.getValue() * 1_000_000);
        final String timestamp = DateTimeFormatter.ofPattern("dd/MM/yyyy - hh:mm:ss.SSS").format(localDateTime);
        endDateTimeLabel.setText(timestamp);
    }

    private void updateUI() {
        final LoggerSearchRequest currentValue = loggerSearchRequest.getValue();

        //TODO: List instead of item
        levelComboBox.setValue(Optional.ofNullable(currentValue).map(v -> Level.WARN).orElse(Level.TRACE));

        maxResultsSpinner.getEditor().setText(Optional.ofNullable(currentValue).map(v -> v.getMaxResults()).orElse(0) + "");

        //TODO: Handle null value
        final ZonedDateTime startDateTime = Optional.ofNullable(currentValue).map(v -> v.getStartDate()).orElse(ZonedDateTime.now());
        final LocalDate startLocalDate = startDateTime.toLocalDate();
        final LocalTime startLocalTime = startDateTime.toLocalTime();
        startDatePicker.setValue(startLocalDate);
        startHourSpinner.getEditor().setText(startLocalTime.getHour() + "");
        startMinuteSpinner.getEditor().setText(startLocalTime.getMinute() + "");
        startSecondSpinner.getEditor().setText(startLocalTime.getSecond() + "");
        startMilliSpinner.getEditor().setText(startLocalTime.getNano() / 1_000_000 + "");

        //TODO: Handle null value
        final ZonedDateTime endDateTime = Optional.ofNullable(currentValue).map(v -> v.getEndDateTime()).orElse(ZonedDateTime.now());
        final LocalDate endLocalDate = endDateTime.toLocalDate();
        final LocalTime endLocalTime = endDateTime.toLocalTime();
        endDatePicker.setValue(endLocalDate);
        endHourSpinner.getEditor().setText(endLocalTime.getHour() + "");
        endMinuteSpinner.getEditor().setText(endLocalTime.getMinute() + "");
        endSecondSpinner.getEditor().setText(endLocalTime.getSecond() + "");
        endMilliSpinner.getEditor().setText(endLocalTime.getNano() / 1_000_000 + "");
    }

    private void updateItem() {
        //TODO: Handle null value
        final LocalDate startDate = Optional.ofNullable(startDatePicker.getValue()).orElse(LocalDate.now());
        final LocalTime startTime = LocalTime.of(startHourSpinner.getValue(), startMinuteSpinner.getValue(), startSecondSpinner.getValue(), startMilliSpinner.getValue() * 1_000_000);
        final ZonedDateTime startDateTime = ZonedDateTime.of(LocalDateTime.of(startDate, startTime), ZoneId.systemDefault());

        final LocalDate endDate = Optional.ofNullable(endDatePicker.getValue()).orElse(LocalDate.now());
        final LocalTime endTime = LocalTime.of(endHourSpinner.getValue(), endMinuteSpinner.getValue(), endSecondSpinner.getValue(), endMilliSpinner.getValue() * 1_000_000);
        final ZonedDateTime endDateTime = ZonedDateTime.of(LocalDateTime.of(endDate, endTime), ZoneId.systemDefault());

        final Set<Level> selectedLevels = Optional.ofNullable(levelComboBox.getSelectionModel().getSelectedItem()).
                map(l -> Collections.singleton(l)).orElse(Collections.emptySet());

        final int maxResults = maxResultsSpinner.getValue();

        final LoggerSearchRequest newRequest = new LoggerSearchRequest(startDateTime, endDateTime, selectedLevels, maxResults);
        loggerSearchRequest.setValue(newRequest);
    }

    public boolean filter(final LogMessage message) {
        Assert.requireNonNull(message, "message");
        final LocalDate startDate = Optional.ofNullable(startDatePicker.getValue()).orElse(LocalDate.now());
        final LocalTime startTime = LocalTime.of(startHourSpinner.getValue(), startMinuteSpinner.getValue(), startSecondSpinner.getValue(), startMilliSpinner.getValue() * 1_000_000);
        final ZonedDateTime startDateTime = ZonedDateTime.of(LocalDateTime.of(startDate, startTime), ZoneId.systemDefault());

        final LocalDate endDate = Optional.ofNullable(endDatePicker.getValue()).orElse(LocalDate.now());
        final LocalTime endTime = LocalTime.of(endHourSpinner.getValue(), endMinuteSpinner.getValue(), endSecondSpinner.getValue(), endMilliSpinner.getValue() * 1_000_000);
        final ZonedDateTime endDateTime = ZonedDateTime.of(LocalDateTime.of(endDate, endTime), ZoneId.systemDefault());

        final Set<Level> selectedLevels = Optional.ofNullable(levelComboBox.getSelectionModel().getSelectedItem()).
                map(l -> Collections.singleton(l)).orElse(Collections.emptySet());

        if(!selectedLevels.contains(message.getLevel())) {
            return false;
        }

        if(message.getTimestamp().isBefore(startDateTime)) {
            return false;
        }

        if(message.getTimestamp().isAfter(endDateTime)) {
            return false;
        }
        return true;
    }

    public int getMaxResults() {
        return maxResultsSpinner.getValue();
    }

    public LoggerSearchRequest getLoggerSearchRequest() {
        return loggerSearchRequest.getValue();
    }

    public Property<LoggerSearchRequest> loggerSearchRequestProperty() {
        return loggerSearchRequest;
    }

    public void setLoggerSearchRequest(final LoggerSearchRequest loggerSearchRequest) {
        Assert.requireNonNull(loggerSearchRequest, "loggerSearchRequest");
        this.loggerSearchRequest.setValue(loggerSearchRequest);
    }
}
