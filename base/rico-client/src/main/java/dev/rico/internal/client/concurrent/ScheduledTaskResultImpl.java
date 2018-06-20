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

import dev.rico.core.concurrent.ScheduledTaskResult;

import java.time.LocalDateTime;

public class ScheduledTaskResultImpl implements ScheduledTaskResult {

    private final LocalDateTime lastScheduledStartTime;

    private final LocalDateTime lastStartTime;

    private final LocalDateTime lastCompletionTime;

    public ScheduledTaskResultImpl(final LocalDateTime lastScheduledStartTime, final LocalDateTime lastStartTime, final LocalDateTime lastCompletionTime) {
        this.lastScheduledStartTime = lastScheduledStartTime;
        this.lastStartTime = lastStartTime;
        this.lastCompletionTime = lastCompletionTime;
    }

    @Override
    public LocalDateTime lastScheduledStartTime() {
        return lastScheduledStartTime;
    }

    @Override
    public LocalDateTime lastStartTime() {
        return lastStartTime;
    }

    @Override
    public LocalDateTime lastCompletionTime() {
        return lastCompletionTime;
    }
}
