/*
 * Copyright 2018-2019 Karakun AG.
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

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import dev.rico.internal.logging.spi.LogMessage;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.slf4j.event.Level;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class LogListView extends HBox {

    private final ObjectProperty<LogMessage> logMessage = new SimpleObjectProperty<>();

    private final Label messageLabel = new Label();

    private final Label detailsLabel = new Label();

    private final Label timestampLabel = new Label();

    private final FontAwesomeIconView levelIconView = new FontAwesomeIconView();

    public LogListView() {
        levelIconView.setGlyphSize(28.0);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold");
        detailsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: darkslategray; -fx-font-weight: 200");
        timestampLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: lightslategray; -fx-font-weight: lighter; -fx-font-style: italic");

        final VBox centerBox = new VBox(messageLabel, detailsLabel);
        centerBox.setSpacing(6);
        centerBox.setPadding(new Insets(4.0, 0.0, 0.0, 0.0));
        VBox.setVgrow(messageLabel, Priority.NEVER);
        VBox.setVgrow(detailsLabel, Priority.ALWAYS);

        final VBox detailBox = new VBox(timestampLabel);

        getChildren().addAll(levelIconView, centerBox, detailBox);
        setPadding(new Insets(2.0));
        setSpacing(6.0);
        HBox.setHgrow(levelIconView, Priority.NEVER);
        HBox.setHgrow(centerBox, Priority.ALWAYS);
        HBox.setHgrow(detailBox, Priority.NEVER);


        logMessage.addListener(e -> update());
        update();
    }

    private void update() {
        final LogMessage message = logMessage.get();
        if (message == null) {
            messageLabel.setText(null);
            levelIconView.setIcon(FontAwesomeIcon.QUESTION);
            levelIconView.setFill(Color.TRANSPARENT);
            timestampLabel.setText(null);
            detailsLabel.setText(null);
            detailsLabel.setVisible(false);
            detailsLabel.setManaged(false);
        } else {
            messageLabel.setText(message.getMessage());

            final FontAwesomeIcon icon = Optional.ofNullable(message.getLevel()).
                    map(l -> {
                        if (l == Level.INFO) {
                            return FontAwesomeIcon.INFO_CIRCLE;
                        } else if (l == Level.DEBUG) {
                            return FontAwesomeIcon.BUG;
                        } else if (l == Level.ERROR) {
                            return FontAwesomeIcon.TIMES_CIRCLE;
                        } else if (l == Level.TRACE) {
                            return FontAwesomeIcon.MINUS_CIRCLE;
                        } else if (l == Level.WARN) {
                            return FontAwesomeIcon.WARNING;
                        } else {
                            return FontAwesomeIcon.QUESTION;
                        }
                    }).orElse(FontAwesomeIcon.QUESTION);
            levelIconView.setIcon(icon);

            final Paint iconFill = Optional.ofNullable(message.getLevel()).
                    map(l -> {
                        if (l == Level.INFO) {
                            return Color.LIGHTBLUE;
                        } else if (l == Level.DEBUG) {
                            return Color.LIGHTBLUE;
                        } else if (l == Level.ERROR) {
                            return Color.RED;
                        } else if (l == Level.TRACE) {
                            return Color.LIGHTBLUE;
                        } else if (l == Level.WARN) {
                            return Color.ORANGE;
                        } else {
                            return Color.LIGHTBLUE;
                        }
                    }).orElse(Color.LIGHTBLUE);
            levelIconView.setFill(iconFill);

            final String timestamp = Optional.ofNullable(message.getTimestamp()).
                    map(t -> DateTimeFormatter.ofPattern("dd/MM/yyyy - hh:mm:ss.SSS").format(t)).
                    orElse("unknown");
            timestampLabel.setText(timestamp);

            detailsLabel.setText(message.getExceptionDetail());
            if (Optional.ofNullable(detailsLabel.getText()).orElse("").length() == 0) {
                detailsLabel.setVisible(false);
                detailsLabel.setManaged(false);
            } else {
                detailsLabel.setVisible(true);
                detailsLabel.setManaged(true);
            }
        }
    }

    public LogMessage getLogMessage() {
        return logMessage.get();
    }

    public ObjectProperty<LogMessage> logMessageProperty() {
        return logMessage;
    }

    public void setLogMessage(final LogMessage logMessage) {
        this.logMessage.set(logMessage);
    }
}
