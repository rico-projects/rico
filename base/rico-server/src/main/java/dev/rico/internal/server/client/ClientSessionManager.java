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
package dev.rico.internal.server.client;

import dev.rico.core.Configuration;
import dev.rico.core.logging.Logger;
import dev.rico.core.logging.LoggerFactory;
import dev.rico.internal.core.Assert;
import dev.rico.server.client.ClientSession;
import org.apiguardian.api.API;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.MAX_CLIENTS_PER_SESSION;
import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.MAX_CLIENTS_PER_SESSION_DEFAULT_VALUE;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClientSessionManager {

    private static final Logger LOG = LoggerFactory.getLogger(ClientSessionManager.class);

    private static final String CLIENT_CONTEXT_MAP = "CLIENT_CONTEXT_MAP";

    private static final String CLIENT_SESSION_LOCK = "CLIENT_SESSION_LOCK";

    private final Configuration configuration;

    private final ClientSessionLifecycleHandlerImpl lifecycleHandler;

    public ClientSessionManager(final Configuration configuration, final ClientSessionLifecycleHandlerImpl lifecycleHandler) {
        this.configuration = Assert.requireNonNull(configuration, "configuration");
        this.lifecycleHandler = Assert.requireNonNull(lifecycleHandler, "lifecycleHandler");
    }

    public String createClientSession(final HttpSession httpSession) throws MaxSessionCountReachedException {
        Assert.requireNonNull(httpSession, "httpSession");
        if (!canAddInSession(httpSession)) {
            throw new MaxSessionCountReachedException();
        }

        final ClientSession clientSession = new HttpClientSessionImpl(httpSession);
        add(httpSession, clientSession);

        lifecycleHandler.onSessionCreated(clientSession);
        LOG.trace("Created new client session {} in http session {}", clientSession.getId(), httpSession.getId());
        return clientSession.getId();
    }

    public boolean checkValidClientSession(final HttpSession httpSession, final String clientSessionId) {
        final Lock lock = getOrCreateClientSessionLockForHttpSession(httpSession);
        Assert.requireNonNull(lock, "lock");
        lock.lock();
        try {
            return getOrCreateClientSessionMapInHttpSession(httpSession).containsKey(clientSessionId);
        } finally {
            lock.unlock();
        }
    }

    public void removeAllClientSessionsInHttpSession(final HttpSession httpSession) {
        final Lock lock = getOrCreateClientSessionLockForHttpSession(httpSession);
        Assert.requireNonNull(lock, "lock");
        lock.lock();
        try {
            final Map<String, ClientSession> map = getOrCreateClientSessionMapInHttpSession(httpSession);
            for (ClientSession session : map.values()) {
                setClientSessionForThread(httpSession, session.getId());
                try {
                    lifecycleHandler.onSessionDestroyed(session);
                } finally {
                    resetClientSessionForThread();
                }
            }
            map.clear();
        } finally {
            lock.unlock();
        }
    }

    public void setClientSessionForThread(final HttpSession httpSession, final String clientSessionId) {
        final Lock lock = getOrCreateClientSessionLockForHttpSession(httpSession);
        Assert.requireNonNull(lock, "lock");
        lock.lock();
        try {
            lifecycleHandler.setCurrentSession(getOrCreateClientSessionMapInHttpSession(httpSession).get(clientSessionId));
        } finally {
            lock.unlock();
        }
    }

    public void resetClientSessionForThread() {
        lifecycleHandler.setCurrentSession(null);
    }

    private void add(final HttpSession httpSession, final ClientSession clientSession) {
        Assert.requireNonNull(clientSession, "clientSession");
        final Lock lock = getOrCreateClientSessionLockForHttpSession(httpSession);
        Assert.requireNonNull(lock, "lock");
        lock.lock();
        try {
            getOrCreateClientSessionMapInHttpSession(httpSession).put(clientSession.getId(), clientSession);
        } finally {
            lock.unlock();
        }
    }

    private boolean canAddInSession(final HttpSession httpSession) {
        final Lock lock = getOrCreateClientSessionLockForHttpSession(httpSession);
        Assert.requireNonNull(lock, "lock");
        lock.lock();
        try {
            return getOrCreateClientSessionMapInHttpSession(httpSession).size() < configuration.getIntProperty(MAX_CLIENTS_PER_SESSION, MAX_CLIENTS_PER_SESSION_DEFAULT_VALUE);
        } finally {
            lock.unlock();
        }
    }

    private Map<String, ClientSession> getOrCreateClientSessionMapInHttpSession(final HttpSession session) {
        Assert.requireNonNull(session, "session");
        if (session.getAttribute(CLIENT_CONTEXT_MAP) == null) {
            session.setAttribute(CLIENT_CONTEXT_MAP, new HashMap<>());
        }
        return (Map<String, ClientSession>) session.getAttribute(CLIENT_CONTEXT_MAP);
    }

    private synchronized Lock getOrCreateClientSessionLockForHttpSession(final HttpSession session) {
        Assert.requireNonNull(session, "session");
        if (session.getAttribute(CLIENT_SESSION_LOCK) == null) {
            session.setAttribute(CLIENT_SESSION_LOCK, new ReentrantLock());
        }
        return (Lock) session.getAttribute(CLIENT_SESSION_LOCK);
    }

}
