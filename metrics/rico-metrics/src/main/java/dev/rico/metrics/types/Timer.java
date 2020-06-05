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
package dev.rico.metrics.types;

import dev.rico.core.functional.CheckedRunnable;
import dev.rico.core.functional.CheckedSupplier;
import dev.rico.internal.core.Assert;
import dev.rico.metrics.Metric;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * A timer metric
 */
public interface Timer extends Metric {

    /**
     * Adds a duration to the timer
     *
     * @param amount the amount of the duration
     * @param unit   the time unit of the duration
     */
    void record(long amount, TimeUnit unit);

    /**
     * Adds a duration to the timer
     *
     * @param duration the duration
     */
    default void record(final Duration duration) {
        Assert.requireNonNull(duration, "duration");
        record(duration.toNanos(), TimeUnit.NANOSECONDS);
    }

    /**
     * Executes the given task and adds the duration of execution to the timer metric
     *
     * @param task the task
     * @throws Exception forwarded exception of the task
     */
    default void record(final CheckedRunnable task) throws Exception {
        Assert.requireNonNull(task, "task");
        final CheckedSupplier<Void> supplier = () -> {
            task.run();
            return null;
        };
        record(supplier);
    }

    /**
     * Executes the given task and adds the duration of execution to the timer metric
     *
     * @param task the task
     * @param <T>  return type of the task
     * @return the result of the task
     * @throws Exception forwarded exception of the task
     */
    default <T> T record(final CheckedSupplier<T> task) throws Exception {
        Assert.requireNonNull(task, "task");
        final long nanos = System.nanoTime();
        try {
            return task.get();
        } finally {
            record(System.nanoTime() - nanos, TimeUnit.NANOSECONDS);
        }
    }
}
