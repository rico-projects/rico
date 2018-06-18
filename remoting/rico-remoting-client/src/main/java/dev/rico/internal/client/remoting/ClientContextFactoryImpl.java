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
package dev.rico.internal.client.remoting;

import dev.rico.internal.client.remoting.legacy.ClientModelStore;
import dev.rico.client.Client;
import dev.rico.internal.client.session.StrictClientSessionResponseHandler;
import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.communication.codec.Codec;
import dev.rico.client.ClientConfiguration;
import dev.rico.client.session.ClientSessionStore;
import dev.rico.core.http.HttpClient;
import dev.rico.core.http.HttpURLConnectionHandler;
import dev.rico.client.remoting.ClientContext;
import dev.rico.client.remoting.ClientContextFactory;
import dev.rico.client.remoting.ClientInitializationException;
import org.apiguardian.api.API;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Factory to createList a {@link ClientContext}. Normally you will createList a {@link ClientContext} at the bootstrap of your
 * client by using the {@link #create(ClientConfiguration, URI)} method and use this context as a singleton in your client.
 * The {@link ClientContext} defines the connection between the client and the remoting server endpoint.
 */
@API(since = "0.x", status = INTERNAL)
public class ClientContextFactoryImpl implements ClientContextFactory {

    public ClientContextFactoryImpl() {
    }

    /**
     * Create a {@link ClientContext} based on the given configuration. This method doesn't block and returns a
     * {@link CompletableFuture} to receive its result. If the {@link ClientContext} can't be created the
     * {@link CompletableFuture#get()} will throw a {@link ClientInitializationException}.
     *
     * @param clientConfiguration the configuration
     * @return the future
     */
    public ClientContext create(final ClientConfiguration clientConfiguration, final URI endpoint) {
        Assert.requireNonNull(clientConfiguration, "clientConfiguration");
        final HttpClient httpClient = Client.getService(HttpClient.class);
        final HttpURLConnectionHandler clientSessionCheckResponseHandler = new StrictClientSessionResponseHandler(endpoint);
        httpClient.addResponseHandler(clientSessionCheckResponseHandler);
        final Function<ClientModelStore, AbstractClientConnector> connectionProvider = s -> {
            return new HttpClientConnector(endpoint, clientConfiguration, s, Codec.getInstance(), e -> {}, httpClient);
        };


        return new ClientContextImpl(clientConfiguration, endpoint, connectionProvider, Client.getService(ClientSessionStore.class));
    }

}
