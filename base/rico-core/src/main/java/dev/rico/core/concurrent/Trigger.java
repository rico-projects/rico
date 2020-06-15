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

    Trigger NEVER = t -> Optional.empty();

    Trigger NOW = t -> Optional.of(LocalDateTime.now());

    /**
     * Trigger to activate once after the given duration.
     *
     * @param duration the duration
     * @return the trigger
     */
    static Trigger in(final Duration duration) {
        Assert.requireNonNull(duration, "duration");
        return t -> Optional.of(LocalDateTime.now().plus(duration));
    }

    /**
     * Trigger to active repeatedly after a fixed duration.
     *
     * @param duration the duration
     * @return the trigger
     */
    static Trigger every(final Duration duration) {
        Assert.requireNonNull(duration, "duration");
        return t -> Optional.ofNullable(t)
                .map(ScheduledTaskResult::lastScheduledStartTime)
                .map(last -> last.plus(duration));
    }

    Optional<LocalDateTime> nextExecutionTime(ScheduledTaskResult scheduledTaskResult);
}
