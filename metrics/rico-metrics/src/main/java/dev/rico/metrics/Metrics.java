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
package dev.rico.metrics;

import dev.rico.core.context.Context;
import dev.rico.metrics.types.Counter;
import dev.rico.metrics.types.Gauge;
import dev.rico.metrics.types.Timer;

/**
 * General service to create and access metrics.
 */
public interface Metrics {

    /**
     * Returns the {@link Counter} metric for the given name and {@link Context} definition or create a new one.
     * @param name the name of the metric
     * @param context the context of the metric
     * @return the counter metric
     */
    Counter getOrCreateCounter(String name, Context... context);

    /**
     * Returns the {@link Timer} metric for the given name and {@link Context} definition or create a new one.
     * @param name the name of the metric
     * @param context the context of the metric
     * @return the timer metric
     */
    Timer getOrCreateTimer(String name, Context... context);

    /**
     * Returns the {@link Gauge} metric for the given name and {@link Context} definition or create a new one.
     * @param name the name of the metric
     * @param context the context of the metric
     * @return the gauge metric
     */
    Gauge getOrCreateGauge(String name, Context... context);

}
