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
package dev.rico.internal.client.concurrent;

import dev.rico.internal.core.Assert;

import java.util.function.Function;

public class ProcessDescription<V, T> {

    private final Function<V, T> function;

    private final ThreadType threadType;

    public ProcessDescription(final Function<V, T> function, final ThreadType threadType) {
        this.function = Assert.requireNonNull(function, "function");
        this.threadType = Assert.requireNonNull(threadType, "threadType");
    }

    public Function<V, T> getFunction() {
        return function;
    }

    public ThreadType getThreadType() {
        return threadType;
    }
}
