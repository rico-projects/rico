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
package dev.rico.internal.client.logging.util;

import dev.rico.internal.logging.RicoLoggerFactory;
import dev.rico.internal.logging.spi.LogMessage;
import dev.rico.core.functional.Subscription;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LogClientUtil {

    public static BoundLogList<LogMessage> createObservableListFromLocalCache() {
        final ObservableList<LogMessage> list = FXCollections.observableArrayList(RicoLoggerFactory.getLogCache());
        final Subscription subscription = RicoLoggerFactory.addListener(l -> {
            final List<LogMessage> currentCache = Collections.unmodifiableList(RicoLoggerFactory.getLogCache());
            Platform.runLater(() -> {
                final List<LogMessage> toRemove = list.stream().
                        filter(e -> !currentCache.contains(l)).
                        collect(Collectors.toList());

                list.removeAll(toRemove);

                final List<LogMessage> toAdd = currentCache.stream().
                        filter(e -> !list.contains(e)).
                        collect(Collectors.toList());

                list.addAll(toAdd);
            });
        });
        return new BoundLogList(subscription, list);
    }

}
