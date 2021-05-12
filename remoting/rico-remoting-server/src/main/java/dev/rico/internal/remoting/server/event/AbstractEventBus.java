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

import dev.rico.core.functional.Subscription;
import dev.rico.core.logging.Logger;
import dev.rico.core.logging.LoggerFactory;
import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.server.context.ServerRemotingContext;
import dev.rico.internal.remoting.server.context.ServerRemotingContextProvider;
import dev.rico.internal.server.client.ClientSessionLifecycleHandler;
import dev.rico.remoting.server.event.MessageEventContext;
import dev.rico.remoting.server.event.MessageListener;
import dev.rico.remoting.server.event.RemotingEventBus;
import dev.rico.remoting.server.event.Topic;
import dev.rico.server.client.ClientSession;
import org.apiguardian.api.API;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public abstract class AbstractEventBus implements RemotingEventBus {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractEventBus.class);

    private ServerRemotingContextProvider contextProvider;

    private final Map<Topic<?>, List<ListenerWithFilter<?>>> topicToListenerMap = new ConcurrentHashMap<>();

    private final Map<MessageListener<?>, String> listenerToSessionMap = new ConcurrentHashMap<>();

    private final Map<String, List<Subscription>> sessionStore = new ConcurrentHashMap<>();

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public void init(final ServerRemotingContextProvider contextProvider, final ClientSessionLifecycleHandler lifecycleHandler) {
        this.contextProvider = Assert.requireNonNull(contextProvider, "contextProvider");
        Assert.requireNonNull(lifecycleHandler, "lifecycleHandler").addSessionDestroyedListener((s) -> onSessionEnds(s.getId()));
        initialized.set(true);
    }

    @Override
    public <T extends Serializable> void publish(final Topic<T> topic, final T data) {
        checkInitialization();

        final MessageEventImpl event = new MessageEventImpl(topic, System.currentTimeMillis(), data);

        event.addMetadata(EventConstants.TYPE_PARAM, EventConstants.TYPE_PLATFORM);

        final ServerRemotingContext currentContext = getCurrentContext();
        if (currentContext != null) {
            final ClientSession clientSession = currentContext.getClientSession();
            if (clientSession != null) {
                event.addMetadata(EventConstants.CLIENT_SESSION_PARAM, clientSession.getId());
                final HttpSession httpSession = clientSession.getHttpSession();
                if (httpSession != null) {
                    event.addMetadata(EventConstants.HTTP_SESSION_PARAM, httpSession.getId());
                }
            }
        }
        //Handle listener in same session
        if (currentContext != null) {
            final List<ListenerWithFilter<T>> listenersInCurrentSession = getListenersForSessionAndTopic(currentContext.getId(), topic);
            for (ListenerWithFilter<T> listenerAndFilter : listenersInCurrentSession) {
                final Predicate<MessageEventContext<T>> filter = listenerAndFilter.getFilter();
                final MessageListener<T> listener = listenerAndFilter.getListener();
                if (filter == null || filter.test(event.getMessageEventContext())) {
                    listener.onMessage(event);
                }
            }
        }
        publishForOtherSessions(event);
    }

    @Override
    public <T extends Serializable> Subscription subscribe(final Topic<T> topic, final MessageListener<? super T> listener, final Predicate<MessageEventContext<T>> filter) {
        checkInitialization();
        Assert.requireNonNull(topic, "topic");
        Assert.requireNonNull(listener, "listener");

        final ServerRemotingContext subscriptionContext = getCurrentContext();
        if (subscriptionContext == null) {
            throw new IllegalStateException("Subscription can only be done from remoting context!");
        }
        final String subscriptionSessionId = subscriptionContext.getId();
        LOG.trace("Adding subscription for topic {} in remoting context {}", topic.getName(), subscriptionSessionId);
        List<ListenerWithFilter<?>> listeners = topicToListenerMap.get(topic);
        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<>();
            topicToListenerMap.put(topic, listeners);
        }
        final ListenerWithFilter listenerWithFilter = new ListenerWithFilter(listener, filter);
        listeners.add(listenerWithFilter);
        listenerToSessionMap.put(listener, subscriptionSessionId);
        final Subscription subscription = new Subscription() {
            @Override
            public void unsubscribe() {
                LOG.trace("Removing subscription for topic {} in remoting context {}", topic.getName(), subscriptionSessionId);
                final List<ListenerWithFilter<?>> listeners = topicToListenerMap.get(topic);
                if (listeners != null) {
                    listeners.remove(listenerWithFilter);
                }
                listenerToSessionMap.remove(listener);
                removeSubscriptionForSession(this, subscriptionSessionId);
            }
        };
        addSubscriptionForSession(subscription, subscriptionSessionId);
        return subscription;
    }

    public <T extends Serializable> Subscription subscribe(final Topic<T> topic, final MessageListener<? super T> listener) {
        return subscribe(topic, listener, null);
    }

    protected <T extends Serializable> void triggerEventHandling(final MessageEventImpl<T> event) {
        Assert.requireNonNull(event, "event");

        final Topic<T> topic = event.getMessageEventContext().getTopic();
        LOG.trace("Handling data for topic {}", topic.getName());
        final List<ListenerWithFilter<?>> listeners = topicToListenerMap.get(topic);
        if (listeners != null) {
            for (final ListenerWithFilter<?> listenerAndFilter : listeners) {
                final String sessionId = listenerToSessionMap.get(listenerAndFilter.getListener());
                if (sessionId == null) {
                    throw new RuntimeException("Internal Error! No session id defined for event bus listener!");
                }
                if (sendInSameClientSession(event, (MessageListener<T>) listenerAndFilter.getListener())) {
                    // This listener was already called at the publish call
                    // since the event was called from the same session
                    LOG.trace("Event listener for topic {} was already called in remoting context {}", topic.getName(), sessionId);
                } else {
                    LOG.trace("Event listener for topic {} must be called later in remoting context {}", topic.getName(), sessionId);
                    contextProvider.getContextById(sessionId).runLater(new Runnable() {

                        @Override
                        public void run() {
                            LOG.trace("Calling event listener for topic {} in remoting context {}", topic.getName(), sessionId);
                            final Predicate<MessageEventContext<T>> sessionFilter = ((ListenerWithFilter<T>) listenerAndFilter).getFilter();
                            final MessageListener<T> listener = (MessageListener<T>) listenerAndFilter.getListener();
                            try {
                                if (sessionFilter == null || sessionFilter.test(event.getMessageEventContext())) {
                                    listener.onMessage(event);
                                }
                            } catch (final Exception e) {
                                LOG.error("Error in calling event listener for topic '" + topic.getName() + "' in remoting context " + sessionId, e);
                            }
                        }
                    });
                }
            }
        }
    }

    private <T extends Serializable> boolean sendInSameClientSession(final MessageEventImpl<T> event, final MessageListener<T> listener) {
        Assert.requireNonNull(event, "event");
        final String listenerSessionId = listenerToSessionMap.get(listener);
        final MessageEventContext<T> eventContext = event.getMessageEventContext();
        if (eventContext != null) {
            return new ClientSessionEventFilter(listenerSessionId).test(eventContext);
        }
        return false;
    }

    protected abstract <T extends Serializable> void publishForOtherSessions(final MessageEventImpl<T> event);

    private void checkInitialization() {
        if (!initialized.get()) {
            throw new RuntimeException("EventBus not initialized");
        }
    }

    private <T extends Serializable> List<ListenerWithFilter<T>> getListenersForSessionAndTopic(final String sessionId, final Topic<T> topic) {
        Assert.requireNonBlank(sessionId, "sessionId");
        Assert.requireNonNull(topic, "topic");

        final List<ListenerWithFilter<?>> handlers = topicToListenerMap.get(topic);
        if (handlers == null) {
            return Collections.emptyList();
        }

        final List<ListenerWithFilter<T>> ret = new ArrayList<>();
        for (ListenerWithFilter<?> listener : handlers) {
            if (sessionId.equals(listenerToSessionMap.get(listener.getListener()))) {
                ret.add((ListenerWithFilter<T>) listener);
            }
        }
        return ret;
    }

    private void addSubscriptionForSession(final Subscription subscription, final String clientSessionId) {
        List<Subscription> subscriptionsForSession = sessionStore.get(clientSessionId);
        if (subscriptionsForSession == null) {
            subscriptionsForSession = new CopyOnWriteArrayList<>();
            sessionStore.put(clientSessionId, subscriptionsForSession);
        }
        subscriptionsForSession.add(subscription);
    }

    private void removeSubscriptionForSession(final Subscription subscription, final String clientSessionId) {
        final List<Subscription> subscriptionsForSession = sessionStore.get(clientSessionId);
        if (subscriptionsForSession != null) {
            subscriptionsForSession.remove(subscription);
        }
    }

    private void onSessionEnds(final String clientSessionId) {
        Assert.requireNonBlank(clientSessionId, "clientSessionId");
        final List<Subscription> subscriptions = sessionStore.get(clientSessionId);
        if (subscriptions != null) {
            for (Subscription subscription : subscriptions) {
                subscription.unsubscribe();
            }
        }
    }

    private ServerRemotingContext getCurrentContext() {
        return contextProvider.getCurrentContext();
    }
}
