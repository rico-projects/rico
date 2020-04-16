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
package dev.rico.internal.remoting.server.event;

import dev.rico.remoting.server.event.MessageEvent;
import dev.rico.remoting.server.event.MessageEventContext;
import dev.rico.remoting.server.event.Topic;
import org.apiguardian.api.API;

import java.io.Serializable;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class MessageEventImpl<T extends Serializable> implements MessageEvent<T> {

    private final T data;

    private final RemotingEventContext<T> eventContext;

    public MessageEventImpl(final Topic<T> topic, final long timestamp, final T data) {
        this.eventContext = new RemotingEventContext<T>(topic, timestamp);
        this.data = data;
    }

    public void addMetadata(final String key, final Serializable value) {
        eventContext.addMetadata(key, value);
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public MessageEventContext<T> getMessageEventContext() {
        return eventContext;
    }
}
