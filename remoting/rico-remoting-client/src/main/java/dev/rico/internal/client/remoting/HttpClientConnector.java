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
import dev.rico.internal.client.remoting.legacy.communication.AbstractClientConnector;
import dev.rico.internal.client.remoting.legacy.communication.BlindCommandBatcher;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.http.HttpHeaderConstants;
import dev.rico.internal.remoting.commands.DestroyContextCommand;
import dev.rico.internal.remoting.legacy.communication.Codec;
import dev.rico.internal.remoting.legacy.communication.Command;
import dev.rico.client.ClientConfiguration;
import dev.rico.core.http.HttpClient;
import dev.rico.core.http.RequestMethod;
import dev.rico.remoting.RemotingException;
import dev.rico.client.remoting.RemotingExceptionHandler;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * This class is used to sync the unique client scope id of the current remoting context
 */
@API(since = "0.x", status = INTERNAL)
public class HttpClientConnector extends AbstractClientConnector {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientConnector.class);

    private final URI servletUrl;

    private final Codec codec;

    private final HttpClient client;

    private final AtomicBoolean disconnecting = new AtomicBoolean(false);

    public HttpClientConnector(final URI servletUrl, final ClientConfiguration configuration, final ClientModelStore clientModelStore, final Codec codec, final RemotingExceptionHandler onException, final HttpClient client) {
        super(clientModelStore, Assert.requireNonNull(configuration, "configuration").getUiExecutor(), new BlindCommandBatcher(), onException, configuration.getBackgroundExecutor());
        this.servletUrl = Assert.requireNonNull(servletUrl, "servletUrl");
        this.codec = Assert.requireNonNull(codec, "codec");
        this.client = Assert.requireNonNull(client, "client");
    }

    public List<Command> transmit(final List<Command> commands) throws RemotingException {
        Assert.requireNonNull(commands, "commands");

        if (disconnecting.get()) {
            LOG.warn("Canceled communication based on disconnect");
            return Collections.emptyList();
        }

        //block if diconnect is called in other thread (poll / release)
        for (Command command : commands) {
            if (command instanceof DestroyContextCommand) {
                disconnecting.set(true);
            }
        }

        try {
            final String data = codec.encode(commands);
            final String receivedContent = client.request(servletUrl, RequestMethod.POST).withContent(data, HttpHeaderConstants.JSON_MIME_TYPE).readString().execute().get().getContent();
            return codec.decode(receivedContent);
        } catch (final Exception e) {
            throw new RemotingException("Error in remoting layer", e);
        }
    }

    @Override
    public void connect() {
        disconnecting.set(false);
        super.connect();
    }

    @Override
    public void disconnect() {
        super.disconnect();
        disconnecting.set(false);
    }
}



