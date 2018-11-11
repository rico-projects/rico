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

import dev.rico.core.http.RequestChain;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.RicoConstants;
import dev.rico.core.http.HttpURLConnectionInterceptor;
import dev.rico.client.session.ClientSessionStore;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ClientSessionHandler implements HttpURLConnectionInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(ClientSessionHandler.class);

    private final ClientSessionStore clientSessionStore;

    public ClientSessionHandler(final ClientSessionStore clientSessionStore) {
        this.clientSessionStore = Assert.requireNonNull(clientSessionStore, "clientSessionStore");
    }

    @Override
    public void handle(final HttpURLConnection request, final RequestChain requestChain) throws IOException{
        Assert.requireNonNull(request, "request");
        Assert.requireNonNull(requestChain, "requestChain");
        try {
            final String clientId = clientSessionStore.getClientIdentifierForUrl(request.getURL().toURI());
            if (clientId != null) {
                LOG.debug("Adding client id {} to http request at {}", clientId, request.getURL());
                request.setRequestProperty(RicoConstants.CLIENT_ID_HTTP_HEADER_NAME, clientId);
            } else {
                LOG.trace("Request to application at {} without client id. Client id not defined until now.", request.getURL());
            }
            final HttpURLConnection response = requestChain.call();
            final String clientIdInHeader = response.getHeaderField(RicoConstants.CLIENT_ID_HTTP_HEADER_NAME);
            clientSessionStore.setClientIdentifierForUrl(response.getURL().toURI(), clientIdInHeader);
        } catch (final URISyntaxException e) {
            LOG.error("Exception while converting to request URL {} to URI", request.getURL());
            throw new RuntimeException("Exception while converting URL " + request.getURL() + "to URI", e);
        }

    }
}
