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
package dev.rico.internal.metrics.server.servlet;

import dev.rico.internal.metrics.MetricsImpl;
import dev.rico.internal.core.context.ContextImpl;
import dev.rico.core.context.Context;
import dev.rico.server.ServerListener;
import dev.rico.server.client.ClientSession;
import dev.rico.server.client.ClientSessionListener;

import java.util.concurrent.atomic.AtomicLong;

@ServerListener
public class MetricsClientSessionListener implements ClientSessionListener {

    private final AtomicLong counter = new AtomicLong();


    @Override
    public void sessionCreated(final ClientSession clientSession) {
        final Context idTag = new ContextImpl("sessionId", clientSession.getId());
        MetricsImpl.getInstance().getOrCreateGauge("clientSessions", idTag)
                .setValue(counter.incrementAndGet());
    }

    @Override
    public void sessionDestroyed(final ClientSession clientSession) {
        final Context idTag = new ContextImpl("sessionId", clientSession.getId());
        MetricsImpl.getInstance().getOrCreateGauge("clientSessions", idTag)
                .setValue(counter.incrementAndGet());
    }
}
