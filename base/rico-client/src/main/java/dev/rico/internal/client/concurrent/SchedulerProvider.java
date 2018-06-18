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

import dev.rico.internal.client.AbstractServiceProvider;
import dev.rico.internal.core.Assert;
import dev.rico.client.ClientConfiguration;
import dev.rico.core.concurrent.Scheduler;

import java.util.concurrent.Executor;

public class SchedulerProvider extends AbstractServiceProvider<Scheduler> {

    public SchedulerProvider() {
        super(Scheduler.class);
    }

    @Override
    protected Scheduler createService(final ClientConfiguration configuration) {
        Assert.requireNonNull(configuration, "configuration");
        final Executor backgroundExecutor = configuration.getBackgroundExecutor();
        Assert.requireNonNull(backgroundExecutor, "backgroundExecutor");
        return new SchedulerImpl(backgroundExecutor);
    }
}
