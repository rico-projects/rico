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
package dev.rico.metrics;

import dev.rico.core.context.Context;
import dev.rico.metrics.types.Counter;
import dev.rico.metrics.types.Gauge;
import dev.rico.metrics.types.Timer;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Facade interface for the metrics API. This interface provides several factory methods to create metrics.
 */
@API(since = "1.1.0", status = EXPERIMENTAL)
public interface Metrics {

    /**
     * Returns a new counter if no counter instance exists for the given name and context.
     * Otherwise the existing counter instance will be returned.
     *
     * @param name    the name of the counter
     * @param context the context
     * @return the counter
     */
    Counter getOrCreateCounter(String name, Context... context);

    /**
     * Returns a new timer if no timer instance exists for the given name and context.
     * Otherwise the existing timer instance will be returned.
     *
     * @param name    the name of the timer
     * @param context the context
     * @return the timer
     */
    Timer getOrCreateTimer(String name, Context... context);

    /**
     * Returns a new gauge if no gauge instance exists for the given name and context.
     * Otherwise the existing gauge instance will be returned.
     *
     * @param name    the name of the gauge
     * @param context the context
     * @return the gauge
     */
    Gauge getOrCreateGauge(String name, Context... context);

}
