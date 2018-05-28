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
package dev.rico.internal.server.logging;

import dev.rico.internal.core.Assert;
import dev.rico.core.functional.Subscription;
import dev.rico.internal.logging.LogSearchFilterBean;
import dev.rico.internal.logging.LoggerSearchRequest;
import dev.rico.server.remoting.RemotingAction;
import dev.rico.server.remoting.RemotingController;
import dev.rico.server.remoting.RemotingModel;
import org.slf4j.event.Level;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@RemotingController
public class LogFilterController {

    @RemotingModel
    private LogSearchFilterBean model;

    private final List<Consumer<LoggerSearchRequest>> searchListener = new ArrayList<>();

    @RemotingAction
    public void search() {
        final LoggerSearchRequest request = getCurrentRequest();
        searchListener.forEach(l -> l.accept(request));
    }

    @RemotingAction
    public void clearStartDate() {
        final LocalDateTime minTime = LocalDateTime.MIN;
        model.setStartDate(ZonedDateTime.of(minTime, ZoneId.systemDefault()));
    }

    @RemotingAction
    public void setStartDateToNow() {
        model.setStartDate(ZonedDateTime.now());
    }

    @RemotingAction
    public void setEndDateToNow() {
        model.setEndDateTime(ZonedDateTime.now());
    }

    @RemotingAction
    public void removeLevelFiltering() {
        model.getLevel().clear();
        model.getLevel().addAll(Level.values());
    }

    public LoggerSearchRequest getCurrentRequest() {
        final ZonedDateTime start = model.getStartDate();
        final ZonedDateTime end = model.getEndDateTime();
        final Set<Level> levels = Collections.unmodifiableSet(new HashSet<>(model.getLevel()));
        final int maxResults = Optional.ofNullable(model.maxResultsProperty().get()).orElse(-1);
        return new LoggerSearchRequest(start, end, levels, maxResults);
    }

    public Subscription addSearchListener(final Consumer<LoggerSearchRequest> listener) {
        searchListener.add(Assert.requireNonNull(listener, "listener"));
        return () -> searchListener.remove(listener);
    }
}
