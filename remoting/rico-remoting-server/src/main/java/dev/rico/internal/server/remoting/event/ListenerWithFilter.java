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
package dev.rico.internal.server.remoting.event;

import dev.rico.event.MessageEventContext;
import dev.rico.event.MessageListener;
import dev.rico.internal.core.Assert;
import org.apiguardian.api.API;

import java.io.Serializable;
import java.util.function.Predicate;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ListenerWithFilter<T extends Serializable> {

    private final MessageListener<T> listener;

    private final Predicate<MessageEventContext<T>> filter;

    public ListenerWithFilter(final MessageListener<T> listener, final Predicate<MessageEventContext<T>> filter) {
        this.listener = Assert.requireNonNull(listener, "listener");
        this.filter = filter;
    }

    public ListenerWithFilter(MessageListener<T> listener) {
        this(listener, null);
    }

    public MessageListener<T> getListener() {
        return listener;
    }

    public Predicate<MessageEventContext<T>> getFilter() {
        return filter;
    }
}
