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
package dev.rico.internal.logging;

import dev.rico.remoting.RemotingBean;
import dev.rico.remoting.ObservableList;
import dev.rico.remoting.Property;
import org.slf4j.event.Level;

import java.time.ZonedDateTime;

@RemotingBean
public class LogSearchFilterBean {

    private Property<ZonedDateTime> startDate;

    private Property<ZonedDateTime> endDateTime;

    private Property<Integer> maxResults;

    private ObservableList<Level> level;

    public ZonedDateTime getStartDate() {
        return startDate.get();
    }

    public Property<ZonedDateTime> startDateProperty() {
        return startDate;
    }

    public void setStartDate(final ZonedDateTime startDate) {
        this.startDate.set(startDate);
    }

    public ZonedDateTime getEndDateTime() {
        return endDateTime.get();
    }

    public Property<ZonedDateTime> endDateTimeProperty() {
        return endDateTime;
    }

    public void setEndDateTime(final ZonedDateTime endDateTime) {
        this.endDateTime.set(endDateTime);
    }

    public ObservableList<Level> getLevel() {
        return level;
    }

    public Integer getMaxResults() {
        return maxResults.get();
    }

    public Property<Integer> maxResultsProperty() {
        return maxResults;
    }

    public void setMaxResults(final Integer maxResults) {
        this.maxResults.set(maxResults);
    }
}
