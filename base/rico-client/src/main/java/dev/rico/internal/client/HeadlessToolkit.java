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
package dev.rico.internal.client;

import dev.rico.client.Toolkit;
import dev.rico.client.concurrent.UiExecutor;
import dev.rico.internal.core.Assert;
import org.apiguardian.api.API;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(since = "0.19.0", status = EXPERIMENTAL)
public class HeadlessToolkit implements Toolkit {

    private final static Executor EXECUTOR = Executors.newSingleThreadExecutor();

    @Override
    public UiExecutor getUiExecutor() {
        return task -> EXECUTOR.execute(Assert.requireNonNull(task, "task"));
    }

    @Override
    public String getName() {
        return ClientConstants.HEADLESS_TOOLKIT;
    }
}
