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
package dev.rico.internal.client.session;

import dev.rico.internal.core.Assert;
import dev.rico.internal.core.RicoConstants;
import dev.rico.core.http.HttpURLConnectionHandler;
import dev.rico.client.session.ClientSessionStore;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClientSessionResponseHandler implements HttpURLConnectionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ClientSessionResponseHandler.class);
    private final ClientSessionStore clientSessionStore;

    public ClientSessionResponseHandler(final ClientSessionStore clientSessionStore) {
        this.clientSessionStore = Assert.requireNonNull(clientSessionStore, "clientSessionStore");
    }

    @Override
    public void handle(final HttpURLConnection response) {
        Assert.requireNonNull(response, "response");
        String clientIdInHeader = response.getHeaderField(RicoConstants.CLIENT_ID_HTTP_HEADER_NAME);
        try {
            clientSessionStore.setClientIdentifierForUrl(response.getURL().toURI(), clientIdInHeader);
        } catch (URISyntaxException e) {
            LOG.error("Exception while converting to response URL {} to URI", response.getURL());
            throw new RuntimeException("Exception while converting URL "+response.getURL() +"to URI", e);
        }
    }
}
