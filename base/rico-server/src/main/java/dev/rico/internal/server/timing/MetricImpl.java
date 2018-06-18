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
package dev.rico.internal.server.timing;

import dev.rico.server.timing.Metric;

import java.time.Duration;
import java.time.ZonedDateTime;

public class MetricImpl implements Metric {

    private final String name;

    private final String description;

    private final ZonedDateTime startTime;

    private Duration duration;

    public MetricImpl(final String name, final String description) {
        this.name = name;
        this.description = description;
        this.startTime = ZonedDateTime.now();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    @Override
    public synchronized void stop() {
        if(duration != null) {
            throw new IllegalStateException("Metric '" + name + "' was already stopped!");
        }
        duration = Duration.between(startTime, ZonedDateTime.now());
    }
}
