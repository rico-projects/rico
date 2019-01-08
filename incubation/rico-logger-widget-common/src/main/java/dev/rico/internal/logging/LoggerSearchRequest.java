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

import org.slf4j.event.Level;

import java.time.ZonedDateTime;
import java.util.Set;

public class LoggerSearchRequest {

    private final ZonedDateTime startDate;

    private final ZonedDateTime endDateTime;

    private final Set<Level> level;

    private final int maxResults;

    public LoggerSearchRequest(final ZonedDateTime startDate, final ZonedDateTime endDateTime, final Set<Level> level, int maxResults) {
        this.startDate = startDate;
        this.endDateTime = endDateTime;
        this.level = level;
        this.maxResults = maxResults;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public ZonedDateTime getEndDateTime() {
        return endDateTime;
    }

    public Set<Level> getLevel() {
        return level;
    }
}
