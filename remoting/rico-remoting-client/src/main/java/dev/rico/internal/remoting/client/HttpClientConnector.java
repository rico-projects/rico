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
package dev.rico.internal.remoting.client;

import dev.rico.client.concurrent.BackgroundExecutor;
import dev.rico.client.concurrent.UiExecutor;
import dev.rico.internal.remoting.commands.CreateContextCommand;
import dev.rico.remoting.client.RemotingExceptionHandler;
import dev.rico.core.http.HttpClient;
import dev.rico.core.http.RequestMethod;
import dev.rico.internal.remoting.client.legacy.ClientModelStore;
import dev.rico.internal.remoting.client.legacy.communication.AbstractClientConnector;
import dev.rico.internal.remoting.client.legacy.communication.BlindCommandBatcher;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.http.HttpHeaderConstants;
import dev.rico.internal.remoting.commands.DestroyContextCommand;
import dev.rico.internal.remoting.legacy.communication.Codec;
import dev.rico.internal.remoting.legacy.communication.Command;
import dev.rico.remoting.RemotingException;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
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

    private final AtomicBoolean hasContext = new AtomicBoolean(false);

    public HttpClientConnector(final URI servletUrl, final UiExecutor uiExecutor, final BackgroundExecutor backgroundExecutor, final ClientModelStore clientModelStore, final Codec codec, final RemotingExceptionHandler onException, final HttpClient client) {
        super(clientModelStore, uiExecutor, new BlindCommandBatcher(), onException, backgroundExecutor);
        this.servletUrl = Assert.requireNonNull(servletUrl, "servletUrl");
        this.codec = Assert.requireNonNull(codec, "codec");
        this.client = Assert.requireNonNull(client, "client");
    }

    public List<Command> transmit(final List<Command> commands) throws RemotingException {
        Assert.requireNonNull(commands, "commands");

        final List<Command> commandsToSend = new ArrayList<>();

        for (Command command : commands) {
            if (command instanceof CreateContextCommand) {
                hasContext.set(true);
            }
            if (hasContext.get()) {
                commandsToSend.add(command);
            } else {
                LOG.debug("No context - ignoring command: {}", command);
            }
            if (command instanceof DestroyContextCommand) {
                hasContext.set(false);
            }
        }

        try {
            final String data = codec.encode(commandsToSend);

            connectedFlagLock.lock();
            try {
                if (!connectedFlag.get()) {
                    LOG.warn("No connection, aborting request.");
                    return Collections.emptyList();
                }

                final String receivedContent = client.request(servletUrl, RequestMethod.POST).withContent(data, HttpHeaderConstants.JSON_MIME_TYPE).readString().execute().get().getContent();
                return codec.decode(receivedContent);
            }
            finally {
                connectedFlagLock.unlock();
            }
        } catch (final Exception e) {
            throw new RemotingException("Error in remoting layer", e);
        }
    }

    @Override
    public void disconnect() {
        if (hasContext.get()) {
            try {
                transmit(Collections.singletonList(new DestroyContextCommand()));
            } catch (RemotingException ignored) {
                // best effort. If it fails it fails
                LOG.error("Ignoring exception during destroy command", ignored);
            }
        }
        super.disconnect();
    }
}



