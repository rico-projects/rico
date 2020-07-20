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
package dev.rico.internal.client.session;

import dev.rico.client.session.ClientSessionStore;
import dev.rico.core.logging.Logger;
import dev.rico.core.logging.LoggerFactory;
import dev.rico.internal.core.Assert;
import org.apiguardian.api.API;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClientSessionStoreImpl implements ClientSessionStore {

    private static final Logger LOG = LoggerFactory.getLogger(ClientSessionStoreImpl.class);

    private final Lock mapLock = new ReentrantLock();

    private final Map<String, String> domainToId = new HashMap<>();

    private Function<URI, String> domainToAppConverter = new SimpleUrlToAppDomainConverter();

    @Override
    public String getClientIdentifierForUrl(final URI url) {
        Assert.requireNonNull(url, "url");
        final String applicationDomain = domainToAppConverter.apply(url);
        if (applicationDomain == null) {
            throw new IllegalStateException("Can not define application domain for url " + url);
        }
        LOG.debug("searching for client id application domain: {}", applicationDomain);

        mapLock.lock();
        try {
            final String clientId = domainToId.get(applicationDomain);
            LOG.debug("found client id '{}' for application domain {}", clientId, applicationDomain);
            return clientId;
        } finally {
            mapLock.unlock();
        }
    }

    public void setClientIdentifierForUrl(final URI url, final String clientId) {
        Assert.requireNonNull(url, "url");
        final String applicationDomain = domainToAppConverter.apply(url);
        if (applicationDomain == null) {
            throw new IllegalStateException("Can not define application domain for url " + url);
        }
        LOG.debug("updating client id for application domain: {}", applicationDomain);

        mapLock.lock();
        try {
            if (domainToId.containsKey(applicationDomain)) {
                final String storedId = domainToId.get(applicationDomain);
                if (clientId != null && !storedId.equals(clientId)) {
                    throw new IllegalStateException("Client Id for application domain " + applicationDomain + " already specified.");
                }
            } else {
                LOG.debug("Defining client id '{}' for application domain {}", clientId, applicationDomain);
                if (clientId == null) {
                    LOG.debug("Since client id for application domain {} is defined as 'null' it will be removed", applicationDomain);
                    domainToId.remove(applicationDomain);
                } else {
                    domainToId.put(applicationDomain, clientId);
                }
            }
        } finally {
            mapLock.unlock();
        }
    }

    @Override
    public void resetSession(final URI url) {
        LOG.debug("Resetting client id for url {}", url);
        setClientIdentifierForUrl(url, null);
    }

    public Function<URI, String> getDomainToAppConverter() {
        return domainToAppConverter;
    }

    public void setDomainToAppConverter(final Function<URI, String> domainToAppConverter) {
        this.domainToAppConverter = Assert.requireNonNull(domainToAppConverter, "domainToAppConverter");
    }
}
