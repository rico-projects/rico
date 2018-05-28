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

/**
 * Service to add metrics of the server to a http response.
 */
public interface ServerTiming {

    /**
     * Create a new metric with the current time as start time and the given name
     * @param name the name of the metric
     * @return the metric
     */
    default Metric start(String name) {
        return start(name, null);
    }

    /**
     * Create a new metric with the current time as start time, the given name and description.
     * @param name the name of the metric
     * @param description the description of the metric
     * @return the metric
     */
    Metric start(String name, String description);

}
