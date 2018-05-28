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
package dev.rico.server.timing;

import java.time.Duration;

/**
 * A metric that defines a single task on the server
 */
public interface Metric {

    /**
     * Returns the name of the metric
     * @return the name
     */
    String getName();

    /**
     * Returns the description of the metric
     * @return the description
     */
    String getDescription();

    /**
     * Returns the duration of the metric after {@link #stop()} was called. If {@link #stop()}
     * was not called {@code null} will be returned
     * @return the duration of the metric or null
     */
    Duration getDuration();

    /**
     *  Stops the metric and calculates the duration. This can only be called ones.
     * @throws IllegalStateException if {@link #stop()} was already called.
     */
    void stop() throws IllegalStateException;

}
