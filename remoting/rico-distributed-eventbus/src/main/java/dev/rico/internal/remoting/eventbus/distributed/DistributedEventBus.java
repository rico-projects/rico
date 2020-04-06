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
package dev.rico.internal.remoting.eventbus.distributed;

import dev.rico.internal.server.remoting.event.AbstractEventBus;
import dev.rico.internal.server.remoting.event.MessageEventImpl;
import dev.rico.internal.core.Assert;
import dev.rico.core.functional.Subscription;
import dev.rico.server.remoting.event.MessageEventContext;
import dev.rico.server.remoting.event.MessageListener;
import dev.rico.server.remoting.event.Topic;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import org.apiguardian.api.API;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class DistributedEventBus extends AbstractEventBus {

    private final HazelcastInstance hazelcastClient;

    private final Map<String, String> iTopicRegistrations = new ConcurrentHashMap<>();

    private final Map<String, Integer> iTopicCount = new ConcurrentHashMap<>();

    private final Lock hazelcastEventPipeLock = new ReentrantLock();

    public DistributedEventBus(final HazelcastInstance hazelcastClient) {
        this.hazelcastClient = Assert.requireNonNull(hazelcastClient, "hazelcastClient");
    }

    protected <T extends Serializable> void publishForOtherSessions(final MessageEventImpl<T> event) {
        Assert.requireNonNull(event, "event");
        final ITopic<MessageEventImpl<T>> topic = toHazelcastTopic(event.getMessageEventContext().getTopic());
        topic.publish(event);
    }

    @Override
    public <T extends Serializable> Subscription subscribe(final Topic<T> topic, final MessageListener<? super T> handler, final Predicate<MessageEventContext<T>> filter) {
        final Subscription basicSubscription = super.subscribe(topic, handler, filter);
        final Subscription hazelcastSubscription = createHazelcastSubscription(topic);
        return new Subscription() {
            @Override
            public void unsubscribe() {
                hazelcastSubscription.unsubscribe();
                basicSubscription.unsubscribe();
            }
        };
    }

    private <T extends Serializable> Subscription createHazelcastSubscription(final Topic<T> topic) {
        hazelcastEventPipeLock.lock();
        try {
            final ITopic<MessageEventImpl<T>> hazelcastTopic = toHazelcastTopic(topic);
            Assert.requireNonNull(hazelcastTopic, "hazelcastTopic");

            final Integer currentCount = iTopicCount.get(topic.getName());
            if (currentCount == null || currentCount == 0) {
                registerHazelcastEventPipe(hazelcastTopic);
            } else {
                iTopicCount.put(topic.getName(), currentCount + 1);
            }

            return new Subscription() {
                @Override
                public void unsubscribe() {
                    final Integer currentCount = iTopicCount.get(topic.getName());
                    if (currentCount > 1) {
                        iTopicCount.put(topic.getName(), currentCount - 1);
                    } else {
                        unregisterHazelcastEventPipe(hazelcastTopic);
                    }
                }
            };
        } finally {
            hazelcastEventPipeLock.unlock();
        }
    }

    private <T extends Serializable> void registerHazelcastEventPipe(final ITopic<MessageEventImpl<T>> topic) {
        hazelcastEventPipeLock.lock();
        try {
            Assert.requireNonNull(topic, "hazelcastTopic");

            final String registrationId = topic.addMessageListener(new com.hazelcast.core.MessageListener<MessageEventImpl<T>>() {
                @Override
                public void onMessage(com.hazelcast.core.Message<MessageEventImpl<T>> message) {
                    final MessageEventImpl<T> event = message.getMessageObject();
                    triggerEventHandling(event);
                }
            });
            Assert.requireNonBlank(registrationId, "registrationId");

            iTopicRegistrations.put(topic.getName(), registrationId);
            iTopicCount.put(topic.getName(), 1);
        } finally {
            hazelcastEventPipeLock.unlock();
        }
    }

    private <T extends Serializable> void unregisterHazelcastEventPipe(final ITopic<MessageEventImpl<T>> topic) {
        hazelcastEventPipeLock.lock();
        try {
            Assert.requireNonNull(topic, "hazelcastTopic");

            final Integer count = iTopicCount.get(topic.getName());
            if (count == null || count != 1) {
                throw new IllegalStateException("Count for topic " + topic.getName() + " is wrong: " + count);
            }

            final String registrationId = iTopicRegistrations.get(topic.getName());
            Assert.requireNonBlank(registrationId, "registrationId");

            topic.removeMessageListener(registrationId);

            iTopicRegistrations.remove(topic.getName());
            iTopicCount.remove(topic.getName());
        } finally {
            hazelcastEventPipeLock.unlock();
        }
    }

    private <T extends Serializable> ITopic<MessageEventImpl<T>> toHazelcastTopic(final Topic<T> topic) {
        return hazelcastClient.getTopic(topic.getName());
    }

}
