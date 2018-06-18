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
package dev.rico.internal.server.client;

import dev.rico.internal.core.Assert;
import dev.rico.internal.server.servlet.Mutex;
import dev.rico.server.client.ClientSession;
import dev.rico.server.client.ClientSessionListener;
import org.apiguardian.api.API;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClientSessionMutextHolder implements ClientSessionListener {

    private final Map<String, WeakReference<Mutex>> sessionMutexMap = new HashMap<>();

    private final static String SESSION_MUTEX_ATTRIBUTE = "Session-Mutex";

    @Override
    public void sessionCreated(final ClientSession clientSession) {
        Assert.requireNonNull(clientSession, "clientSession");
        final Mutex mutex = new Mutex();
        clientSession.setAttribute(SESSION_MUTEX_ATTRIBUTE, mutex);
        sessionMutexMap.put(clientSession.getId(), new WeakReference<>(mutex));
    }

    @Override
    public void sessionDestroyed(final ClientSession clientSession) {
        Assert.requireNonNull(clientSession, "clientSession");
        sessionMutexMap.remove(clientSession.getId());
    }

    public Mutex getMutexForClientSession(final String sessionId) {
        Assert.requireNonBlank(sessionId, "sessionId");
        final WeakReference<Mutex> mutexReference = sessionMutexMap.get(sessionId);
        Assert.requireNonNull(mutexReference, "mutexReference");
        return mutexReference.get();
    }
}
