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

import dev.rico.server.client.ClientSession;
import dev.rico.internal.core.Assert;
import org.apiguardian.api.API;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Default implementation of {@link ClientSession} that uses a map internally to store all attributes
 */
@API(since = "0.x", status = INTERNAL)
public class HttpClientSessionImpl implements ClientSession {

    private final Map<String, Object> store;

    private final HttpSession httpSession;

    private final String clientSessionId;

    public HttpClientSessionImpl(final HttpSession httpSession) {
        this.httpSession = Assert.requireNonNull(httpSession, "httpSession");
        this.clientSessionId = UUID.randomUUID().toString();
        this.store = new ConcurrentHashMap<>();
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        store.put(name, value);
    }

    @Override
    public Object getAttribute(final String name) {
        return store.get(name);
    }

    @Override
    public void removeAttribute(final String name) {
        store.remove(name);
    }

    @Override
    public Set<String> getAttributeNames() {
        return Collections.unmodifiableSet(store.keySet());
    }

    @Override
    public void invalidate() {
        store.clear();
    }

    @Override
    public String getId() {
        return clientSessionId;
    }

    @Override
    public HttpSession getHttpSession() {
        return httpSession;
    }

}
