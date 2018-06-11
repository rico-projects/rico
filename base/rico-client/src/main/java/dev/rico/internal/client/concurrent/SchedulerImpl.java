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
import dev.rico.core.concurrent.Scheduler;
import dev.rico.core.concurrent.Trigger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SchedulerImpl implements Scheduler {

    private final Executor executor;

    private final List<ScheduledTask> tasks;

    private final Lock taskLock;

    private final Condition taskCondition;

    private final ScheduledTaskComparator comparator;

    public SchedulerImpl(final Executor executor) {
        this.executor = Assert.requireNonNull(executor, "executor");
        this.tasks = new CopyOnWriteArrayList<>();
        this.comparator = new ScheduledTaskComparator();
        this.taskLock = new ReentrantLock();
        this.taskCondition = taskLock.newCondition();

        executor.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                taskLock.lock();
                try {
                    if(!tasks.isEmpty()) {
                        tasks.sort(comparator);
                        final ScheduledTask nextTask = tasks.get(0);
                        final LocalDateTime now = LocalDateTime.now();
                        if(nextTask.getScheduledStartDate().isBefore(now) || nextTask.getScheduledStartDate().isEqual(now)) {
                            tasks.remove(nextTask);
                            schedule(nextTask);
                        } else {
                            try {
                                taskCondition.awaitNanos(Duration.between(now, nextTask.getScheduledStartDate()).toNanos());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } finally {
                    taskLock.unlock();
                }
            }
        });
    }

    private CompletableFuture<Void> schedule( final ScheduledTask task) {
        Assert.requireNonNull(task, "task");
        return schedule(task.getTask(), task.getTrigger(), task.getScheduledStartDate(), task.getCompletableFuture());
    }

    private CompletableFuture<Void> schedule(final Runnable task, final Trigger trigger, final LocalDateTime scheduledStartTime, final CompletableFuture<Void> completableFuture) {
        Assert.requireNonNull(task, "task");
        Assert.requireNonNull(trigger, "trigger");
        Assert.requireNonNull(trigger, "completableFuture");
        executor.execute(() -> {
            final LocalDateTime startTime = LocalDateTime.now();
            task.run();
            final LocalDateTime endTime = LocalDateTime.now();
            final LocalDateTime nextTime = trigger.nextExecutionTime(new ScheduledTaskResultImpl(scheduledStartTime, startTime, endTime))
                    .orElse(null);
            if(nextTime == null) {
                completableFuture.complete(null);
            } else {
                final ScheduledTask scheduledTask = new ScheduledTask(task, trigger, nextTime, completableFuture);
                taskLock.lock();
                try {
                    tasks.add(scheduledTask);
                    taskCondition.signal();
                } finally {
                    taskLock.unlock();
                }
            }
        });
        return completableFuture;
    }

    @Override
    public CompletableFuture<Void> schedule(final Runnable task, final Trigger trigger) {
        return schedule(task, trigger, LocalDateTime.now(), new CompletableFuture<>());
    }

    @Override
    public void execute(final Runnable command) {
        executor.execute(command);
    }
}
