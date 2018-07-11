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
package dev.rico.internal.client.remoting.communication;

import dev.rico.core.http.HttpResponse;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.http.HttpHeaderConstants;
import dev.rico.internal.core.http.HttpStatus;
import dev.rico.internal.remoting.communication.codec.Codec;
import dev.rico.internal.remoting.communication.commands.Command;
import dev.rico.client.ClientConfiguration;
import dev.rico.core.http.HttpClient;
import dev.rico.core.http.RequestMethod;
import dev.rico.remoting.RemotingException;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import static dev.rico.core.http.RequestMethod.GET;
import static dev.rico.internal.core.http.HttpHeaderConstants.JSON_MIME_TYPE;
import static dev.rico.internal.core.http.HttpStatus.HTTP_OK;
import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * This class is used to sync the unique client scope id of the current remoting context
 */
@API(since = "0.x", status = INTERNAL)
public class HttpClientConnector implements RemotingCommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientConnector.class);

    private final URI servletUrl;

    private final Codec codec;

    private final HttpClient client;

    private final AtomicBoolean connected = new AtomicBoolean(false);

    private final Executor backgroundExecutor;

    private final Executor uiExecutor;

    private final Consumer<Command> clientCommandHandler;

    private final Consumer<Exception> errorHandler;

    private final CommandQueue commandQueue;

    private AtomicBoolean interruptsActive = new AtomicBoolean(false);

    private final Lock interruptLock;

    private final Condition interruptCondition;

    public HttpClientConnector(final URI servletUrl, final ClientConfiguration configuration, final Codec codec, final HttpClient client, final Consumer<Command> clientCommandHandler, final Consumer<Exception> errorHandler) {
        this.servletUrl = Assert.requireNonNull(servletUrl, "servletUrl");
        this.codec = Assert.requireNonNull(codec, "codec");
        this.client = Assert.requireNonNull(client, "client");
        this.backgroundExecutor = Assert.requireNonNull(configuration, "configuration").getBackgroundExecutor();
        this.uiExecutor = configuration.getUiExecutor();
        this.clientCommandHandler = Assert.requireNonNull(clientCommandHandler, "clientCommandHandler");
        this.errorHandler = Assert.requireNonNull(errorHandler, "errorHandler");
        this.commandQueue = new CommandQueue();

        this.interruptLock = new ReentrantLock();
        this.interruptCondition = interruptLock.newCondition();
    }

    public void connect() {
        backgroundExecutor.execute(() -> {
            connected.set(true);
            try {
                while (isConnected()) {
                    final List<Command> toSend = new ArrayList<>();
                    final List<Runnable> onFinishHandler = new ArrayList<>();
                    commandQueue.getAll().forEach(c -> {
                        final Command command = c.getCommand();
                        final Runnable handler = c.getHandler();
                        toSend.add(command);
                        if (handler != null) {
                            onFinishHandler.add(handler);
                        }
                    });

                    final List<Command> receivedCommands = transmitCommands(toSend);
                    callInUiAndWait(() -> {
                        try {
                            receivedCommands.forEach(c -> clientCommandHandler.accept(c));
                            onFinishHandler.forEach(h -> h.run());
                        } catch (Exception e) {
                            errorHandler.accept(e);
                        }
                    });
                }
            } catch (Exception e) {
                handleError(e);
            }
        });

        backgroundExecutor.execute(() -> {
            try {
                while (isConnected()) {
                    interruptLock.lock();
                    try {
                        interruptCondition.await();
                        transmitInterrupt();
                    } finally {
                        interruptLock.unlock();
                    }
                }
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    private void handleError(final Exception e) {
        try {
            callInUiAndWait(() -> errorHandler.accept(e));
        } catch (InterruptedException e1) {
            LOG.error("Internal error!", e1);
        } catch (ExecutionException e1) {
            LOG.error("Internal error!", e1);
        }
    }

    @Override
    public void send(final Command command) {
        this.sendImpl(command, null);
    }

    @Override
    public CompletableFuture<Void> sendAndReact(final Command command) {
        Assert.requireNonNull(command, "command");
        final CompletableFuture<Void> result = new CompletableFuture<>();
        sendImpl(command, () -> result.complete(null));
        return result;
    }

    private void sendImpl(final Command command, Runnable onFinishHandler) {
        final CommandAndHandler cmh = new CommandAndHandler(command, onFinishHandler);
        backgroundExecutor.execute(() -> {
            commandQueue.add(cmh);
            if (interruptsActive.get()) {
                interruptLock.lock();
                try {
                    interruptCondition.signal();
                } finally {
                    interruptLock.unlock();
                }
            }
        });
    }

    private List<Command> transmitCommands(final List<Command> commands) throws RemotingException {
        Assert.requireNonNull(commands, "commands");
        if (!connected.get()) {
            throw new RemotingException("Not connected!");
        }
        try {
            final String data = codec.encode(commands);
            LOG.info("Sending request with data size {}", data.length());
            final HttpResponse<String> response = client.post(servletUrl)
                    .withContent(data, JSON_MIME_TYPE)
                    .readString()
                    .execute()
                    .get();
            if (response.getStatusCode() != HTTP_OK) {
                throw new RemotingException("Bad http response code " + response.getStatusCode());
            }
            final String receivedContent = response.getContent();
            LOG.info("Received response with data size {}", receivedContent.length());
            return codec.decode(receivedContent);
        } catch (final Exception e) {
            throw new RemotingException("Error in request", e);
        }
    }

    private void transmitInterrupt() throws RemotingException {
        try {
            final HttpResponse<Void> response = client.get(servletUrl)
                    .withoutContent()
                    .withoutResult()
                    .execute()
                    .get();
            if (response.getStatusCode() != HTTP_OK) {
                throw new RemotingException("Bad http response code " + response.getStatusCode());
            }
        } catch (final Exception e) {
            throw new RemotingException("Error in interrupt request", e);
        }
    }

    public void disconnect() {
        interruptsActive.set(false);
        connected.set(false);
    }

    public boolean isConnected() {
        return connected.get();
    }

    @Deprecated
    private void callInUiAndWait(final Runnable runnable) throws ExecutionException, InterruptedException {
        Assert.requireNonNull(runnable, "runnable");
        final CompletableFuture<Void> blocker = new CompletableFuture<>();
        uiExecutor.execute(() -> {
            try {
                runnable.run();
            } finally {
                blocker.complete(null);
            }
        });
        blocker.get();
    }

    public void activeInterrupts() {
        interruptsActive.set(true);
    }
}



