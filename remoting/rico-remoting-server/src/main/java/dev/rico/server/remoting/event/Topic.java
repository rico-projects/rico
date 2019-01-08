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
package dev.rico.server.remoting.event;

import org.apiguardian.api.API;

import java.io.Serializable;
import java.util.UUID;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * This class defines a topic for the {@link RemotingEventBus}. By using the event bus data messages can be withContent to
 * a specific topic. A topic is defined by it's name that means that each topic needs a unique name.
 *
 * @param <T> the type of data that can be withContent to this topic
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = MAINTAINED)
public class Topic<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = -875992657685501660L;

    private final String name;

    /**
     * Default constructur that uses a {@link UUID} based string for the name of this topic.
     */
    public Topic() {
        this(UUID.randomUUID().toString());
    }

    /**
     * Constructor that creates a topic based on its name. This name must be unique.
     *
     * @param name the name
     */
    public Topic(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the topic
     *
     * @return name of the topic
     */
    public String getName() {
        return name;
    }

    /**
     * Convenience methods that creates a new topic based on the given unique name
     *
     * @param uniqueName the name
     * @param <T>        the type of data that can be send to the created topic
     * @return the topic
     */
    public static <T extends Serializable> Topic<T> create(String uniqueName) {
        return new Topic<>(uniqueName);
    }

    /**
     * Convenience methods that creates a new topic based on a generated unique name
     * @param <T> the type of data that can be send to the created topic
     * @return the topic
     */
    public static <T extends Serializable> Topic<T> create() {
        return new Topic<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Topic)) return false;

        Topic<?> topic = (Topic<?>) o;

        return name != null ? name.equals(topic.name) : topic.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
