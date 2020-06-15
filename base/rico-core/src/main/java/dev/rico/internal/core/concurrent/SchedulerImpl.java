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

import dev.rico.core.concurrent.Scheduler;
import dev.rico.core.concurrent.Trigger;
import dev.rico.internal.core.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SchedulerImpl implements Scheduler {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerImpl.class);

    private final Executor executor;

    private final List<ScheduledTask> tasks;

    private final Lock taskLock;

    private final Condition taskCondition;

    private final Comparator<ScheduledTask> comparator;

    public SchedulerImpl(final Executor executor) {
        this.executor = Assert.requireNonNull(executor, "executor");
        this.tasks = new CopyOnWriteArrayList<>();
        this.comparator = Comparator.comparing(ScheduledTask::getScheduledStartDate);
        this.taskLock = new ReentrantLock();
        this.taskCondition = taskLock.newCondition();

        executor.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                taskLock.lock();
                try {
                    if (!tasks.isEmpty()) {
                        tasks.sort(comparator);
                        final ScheduledTask nextTask = tasks.get(0);
                        final LocalDateTime now = LocalDateTime.now();
                        if (nextTask.getScheduledStartDate().isBefore(now) || nextTask.getScheduledStartDate().isEqual(now)) {
                            tasks.remove(nextTask);
                            executeScheduledTask(nextTask);
                        } else {
                            taskCondition.awaitNanos(Duration.between(now, nextTask.getScheduledStartDate()).toNanos());
                        }
                    } else {
                        taskCondition.await();
                    }
                } catch (InterruptedException e) {
                    LOG.warn("Scheduler has been interrupted", e);
                    throw new RuntimeException(e);
                } finally {
                    taskLock.unlock();
                }
            }
        });
    }

    private void executeScheduledTask(final ScheduledTask task) {
        Assert.requireNonNull(task, "task");
        executor.execute(() -> {
            final LocalDateTime startTime = LocalDateTime.now();
            task.getTask().run();
            final LocalDateTime endTime = LocalDateTime.now();
            final ScheduledTaskResultImpl lastTime = new ScheduledTaskResultImpl(task.getScheduledStartDate(), startTime, endTime);

            schedule(task.getTask(), task.getTrigger(), lastTime, task.getCompletableFuture());
        });
    }

    public void schedule(final Runnable task, final Trigger trigger, final ScheduledTaskResultImpl lastTime, final CompletableFuture<Void> completableFuture) {
        final LocalDateTime nextTime = trigger.nextExecutionTime(lastTime).orElse(null);
        if (nextTime == null) {
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
    }

    @Override
    public CompletableFuture<Void> schedule(final Runnable task, final Trigger trigger) {
        final CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        final LocalDateTime now = LocalDateTime.now();
        final ScheduledTaskResultImpl lastTime = new ScheduledTaskResultImpl(now, now, now);

        schedule(task, trigger, lastTime, completableFuture);

        return completableFuture;
    }

    @Override
    public void execute(final Runnable command) {
        executor.execute(command);
    }
}
