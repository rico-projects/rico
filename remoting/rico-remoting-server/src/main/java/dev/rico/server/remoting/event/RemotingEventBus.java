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
package dev.rico.server.remoting.event;

import dev.rico.core.functional.Subscription;
import dev.rico.server.remoting.RemotingController;
import org.apiguardian.api.API;

import java.io.Serializable;
import java.util.function.Predicate;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * The remoting event bus that can be used to withContent messages to client sessions.
 * The {@link RemotingEventBus} can be injected in any
 * managed bean and will automatically publish the given data in the client session.
 * This means that you ca subscribe your remoting controller (see {@link RemotingController})
 * to the event bus and publish messages from any other bean like an REST endpoint.
 * <br>
 * <center><img src="doc-files/event-bus.png" alt="Notification MVC Widget example"></center>
 *
 * @author Hendrik Ebbers
 */
@API(since = "1.0.0.RC1", status = EXPERIMENTAL)
public interface RemotingEventBus {

    /**
     * Publish a message to the given topic
     *
     * @param topic the topic
     * @param data  the data of the message
     * @param <T>   type of the data
     */
    <T extends Serializable> void publish(Topic<T> topic, T data);

    /**
     * Register as a listener for a given topic. All messages that will be published for the given address
     * by any client session will trigger the given handler in the correct client session.
     *
     * @param topic    the topic
     * @param listener the listener
     */
    <T extends Serializable> Subscription subscribe(Topic<T> topic, MessageListener<? super T> listener);

    /**
     * Register as a listener for a given topic. All messages that will be published for the given address
     * by any cllient session will trigger the given handler in the correct client session.
     *
     * @param topic    the topic
     * @param listener the listener
     * @param filter   the filter
     */
    <T extends Serializable> Subscription subscribe(Topic<T> topic, MessageListener<? super T> listener, Predicate<MessageEventContext<T>> filter);
}
