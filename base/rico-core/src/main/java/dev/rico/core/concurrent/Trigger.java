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
package dev.rico.core.concurrent;

import dev.rico.internal.core.Assert;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public interface Trigger {

    /**
     * Trigger to activate once after the given duration.
     *
     * @param delay the delay
     * @return the trigger
     */
    static Trigger in(final Duration delay) {
        Assert.requireNonNull(delay, "delay");
        final Optional<LocalDateTime> first = Optional.of(LocalDateTime.now().plus(delay));

        return t -> {
            if (t == null) {
                return first;
            }
            return Optional.empty();
        };
    }

    /**
     * Trigger to active repeatedly after a fixed duration.
     *
     * @param duration the duration
     * @return the trigger
     */
    static Trigger every(final Duration duration) {
        return afterAndEvery(duration, duration);
    }

    /**
     * Trigger to active now and repeatedly after a fixed duration.
     *
     * @param duration the duration
     * @return the trigger
     */
    static Trigger nowAndEvery(final Duration duration) {
        return afterAndEvery(Duration.ZERO, duration);
    }

    /**
     * Trigger to active after an initial delay and repeatedly after a fixed duration.
     *
     * @param delay the delay
     * @param duration the duration
     * @return the trigger
     */
    static Trigger afterAndEvery(final Duration delay, final Duration duration) {
        Assert.requireNonNull(delay, "delay");
        Assert.requireNonNull(duration, "duration");
        final Optional<LocalDateTime> first = Optional.of(LocalDateTime.now().plus(delay));
        return t -> Optional.ofNullable(t)
                    .map(ScheduledTaskResult::lastScheduledStartTime)
                    .map(last -> last.plus(duration))
                    .or(() -> first);
    }

    /**
     * Determines when to execute the task the next time.
     *
     * @param scheduledTaskResult the times of the last execution. If the task has not yet been executed {@code null} is passed.
     * @return
     */
    Optional<LocalDateTime> nextExecutionTime(ScheduledTaskResult scheduledTaskResult);
}
