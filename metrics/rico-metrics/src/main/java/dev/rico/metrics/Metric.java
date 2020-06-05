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

import dev.rico.core.lang.StringPair;

import java.util.List;

/**
 * Basic interface for all metric types (see {@link dev.rico.metrics.types.Gauge},
 * {@link dev.rico.metrics.types.Counter} and {@link dev.rico.metrics.types.Timer}).
 */
public interface Metric extends AutoCloseable {

    /**
     * @return the name of the metric
     */
    String getName();

    /**
     * @return the context for this metric.
     */
    List<StringPair> getContext();

    @Override
    void close();
}
