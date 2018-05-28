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
package dev.rico.internal.server.projection.update;

import dev.rico.internal.core.Assert;
import dev.rico.core.functional.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UpdateHandler<T> {

    private final List<Consumer<T>> consumers;

    public UpdateHandler() {
        this.consumers = new ArrayList<>();
    }

    public void update(final T instance) {
        consumers.forEach(c -> c.accept(instance));
    }

    public Subscription addHandler(final Consumer<T> handler) {
        Assert.requireNonNull(handler, "handler");
        consumers.add(handler);
        return () -> consumers.remove(handler);
    }
}
