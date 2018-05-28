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
package dev.rico.internal.client.concurrent;

import dev.rico.internal.core.Assert;
import dev.rico.core.concurrent.Trigger;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public class ScheduledTask {

    private final Runnable task;

    private final Trigger trigger;

    private final LocalDateTime scheduledStartDate;

    private final CompletableFuture<Void> completableFuture;

    public ScheduledTask(final Runnable task, final Trigger trigger, final LocalDateTime scheduledStartDate, final CompletableFuture<Void> completableFuture) {
        this.task = Assert.requireNonNull(task, "task");
        this.trigger = Assert.requireNonNull(trigger, "trigger");
        this.scheduledStartDate = Assert.requireNonNull(scheduledStartDate, "scheduledStartDate");
        this.completableFuture = Assert.requireNonNull(completableFuture, "completableFuture");
    }

    public CompletableFuture<Void> getCompletableFuture() {
        return completableFuture;
    }

    public Runnable getTask() {
        return task;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public LocalDateTime getScheduledStartDate() {
        return scheduledStartDate;
    }
}
