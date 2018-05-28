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
package dev.rico.internal.server.remoting.event;

import dev.rico.internal.core.Assert;
import dev.rico.server.remoting.event.MessageEventContext;
import dev.rico.server.remoting.event.Topic;
import org.apiguardian.api.API;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class RemotingEventContext<T extends Serializable> implements MessageEventContext<T> {

    private final Topic<T> topic;

    private final long timestamp;

    private Map<String, Serializable> metadata = new HashMap<>();

    public RemotingEventContext(final Topic<T> topic, final long timestamp) {
        this.topic = Assert.requireNonNull(topic, "topic");
        this.timestamp = timestamp;
    }

    public void addMetadata(final String key, final Serializable value) {
        metadata.put(key, value);
    }

    @Override
    public Topic<T> getTopic() {
        return topic;
    }

    @Override
    public Map<String, Serializable> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }
}
