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
package dev.rico.server.timing;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * A metric that defines a single task on the server
 */
@API(since = "2.0", status = STABLE)
public interface ServerTimer extends AutoCloseable {

    /**
     * Returns the name of the metric
     *
     * @return the name
     */
    String getName();

    /**
     * Returns the description of the metric
     *
     * @return the description
     */
    String getDescription();
    
    /**
     * Stops the metric and calculates the duration. This should only be called ones.
     */
    void stop();

    @Override
    default void close() {
        stop();
    }
}
