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
package dev.rico.internal.client.logging;

import dev.rico.internal.logging.spi.LogMessage;
import dev.rico.internal.client.logging.util.LogClientUtil;
import dev.rico.internal.client.logging.widgets.LogFilterView;
import dev.rico.internal.client.logging.widgets.LogListCell;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.slf4j.event.Level;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ViewSample extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(ViewSample.class);

    @Override
    public void start(final Stage primaryStage) throws Exception {

        final ListView<LogMessage> listView = new ListView<>();
        listView.setCellFactory(v -> new LogListCell());
        listView.setItems(LogClientUtil.createObservableListFromLocalCache());

        Executors.newSingleThreadExecutor().execute(() -> {
            while (true) {
                try {
                    Thread.sleep(2_000);
                } catch (InterruptedException e) {}

                LOG.info(MarkerFactory.getMarker("MyMarker"), "System " + UUID.randomUUID() + " starting");
                LOG.info("Found 3 modules");

                LOG.debug("Starting module 'Base'");
                if(Math.random() > 0.5) {
                    LOG.trace("Module 'Base' started in 45 ms");
                } else {
                    LOG.trace("Module 'Base' is using default configuration");
                    LOG.trace("Module 'Base' started in 67 ms");
                }

                LOG.debug("Starting module 'Platform'");
                if(Math.random() > 0.5) {
                    LOG.trace("Module 'Platform' started in 35 ms");
                } else {
                    LOG.warn("Module 'Platform' is using default configuration! Please configure it!");
                    LOG.trace("Module 'Platform' started in 67 ms");
                }

                LOG.debug("Starting module 'Security'");
                if(Math.random() > 0.5) {
                    LOG.trace("Module 'Security' started in 35 ms");
                } else {
                    LOG.error("Module 'Security' can not be started!", new RuntimeException("Error in param foo"));
                }
            }
        });

        Executors.newSingleThreadExecutor().execute(() -> {
            while (true) {
                try {
                    Thread.sleep(1_500);
                } catch (InterruptedException e) {}
                LOG.trace("Will ping server");
                LOG.debug("Server ping returned in 42 ms");
            }
        });

        final LogFilterView filterView = new LogFilterView();
        final VBox main = new VBox(filterView, listView);
        main.setFillWidth(true);
        VBox.setVgrow(filterView, Priority.NEVER);
        VBox.setVgrow(listView, Priority.ALWAYS);

        primaryStage.setScene(new Scene(main));
        primaryStage.show();
    }

    private LogMessage createMessage(final Level level, final String message) {
        final LogMessage logMessage = new LogMessage();
        logMessage.setTimestamp(ZonedDateTime.now());
        logMessage.setLevel(level);
        logMessage.setMessage(message);
        return logMessage;
    }

    private LogMessage createMessage(final Level level, final String message, final Throwable throwable) {
        final LogMessage logMessage = createMessage(level, message);
        logMessage.setThrowable(throwable);
        return logMessage;
    }

    public static void main(String[] args) {
        launch();
    }
}
