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
package dev.rico.internal.core.concurrent;

import dev.rico.internal.core.Assert;
import dev.rico.core.concurrent.ScheduledTaskResult;
import dev.rico.core.concurrent.Trigger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

public class SimpleTrigger implements Trigger {

    private final Supplier<Boolean> supplier;

    private final Trigger innerTrigger;

    public SimpleTrigger(final Supplier<Boolean> supplier, final Duration duration) {
        this(supplier, Optional.of(duration).map(d -> (Trigger) r -> Optional.of(LocalDateTime.now().plus(d))).get());
    }

    public SimpleTrigger(final Supplier<Boolean> supplier, final Trigger innerTrigger) {
        this.supplier = Assert.requireNonNull(supplier, "supplier");
        this.innerTrigger = Assert.requireNonNull(innerTrigger, "innerTrigger");
    }

    @Override
    public Optional<LocalDateTime> nextExecutionTime(final ScheduledTaskResult scheduledTaskResult) {
        if (supplier.get()) {
            return innerTrigger.nextExecutionTime(scheduledTaskResult);
        } else {
            return Optional.empty();
        }
    }
}
