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
package dev.rico.internal.client.remoting.legacy.communication;

import dev.rico.internal.client.remoting.legacy.ClientModelStore;
import dev.rico.internal.remoting.legacy.commands.InterruptLongPollCommand;
import dev.rico.internal.remoting.legacy.commands.StartLongPollCommand;
import dev.rico.internal.remoting.legacy.communication.Command;
import dev.rico.remoting.RemotingException;
import dev.rico.client.remoting.RemotingExceptionHandler;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apiguardian.api.API.Status.DEPRECATED;

@API(since = "0.x", status = DEPRECATED)
public abstract class AbstractClientConnector {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractClientConnector.class);

    private final Executor uiExecutor;

    private final Executor backgroundExecutor;

    private final RemotingExceptionHandler remotingExceptionHandler;

    private final ClientResponseHandler responseHandler;

    private final ICommandBatcher commandBatcher;

    /**
     * whether we currently wait for push events (internal state) and may need to release
     */
    protected final AtomicBoolean releaseNeeded = new AtomicBoolean(false);

    protected final AtomicBoolean connectedFlag = new AtomicBoolean(false);

    protected final AtomicBoolean useLongPolling = new AtomicBoolean(false);

    protected boolean connectionFlagForUiExecutor = false;

    private final StartLongPollCommand pushListener;

    private final InterruptLongPollCommand releaseCommand;

    protected AbstractClientConnector(final ClientModelStore clientModelStore, final Executor uiExecutor, final ICommandBatcher commandBatcher, final RemotingExceptionHandler remotingExceptionHandler, final Executor backgroundExecutor) {
        this.uiExecutor = Objects.requireNonNull(uiExecutor);
        this.commandBatcher = Objects.requireNonNull(commandBatcher);
        this.remotingExceptionHandler = Objects.requireNonNull(remotingExceptionHandler);
        this.backgroundExecutor = Objects.requireNonNull(backgroundExecutor);
        this.responseHandler = new ClientResponseHandler(clientModelStore);

        this.pushListener = new StartLongPollCommand();
        this.releaseCommand = new InterruptLongPollCommand();
    }

    private void handleError(final Exception exception) {
        Objects.requireNonNull(exception);

        disconnect();

        uiExecutor.execute(new Runnable() {
            @Override
            public void run() {
                connectionFlagForUiExecutor = false;
                if (exception instanceof RemotingException) {
                    remotingExceptionHandler.handle((RemotingException) exception);
                } else {
                    remotingExceptionHandler.handle(new RemotingException("internal remoting error", exception));
                }
            }
        });
    }

    protected void commandProcessing() {
        boolean longPollingActivated = false;
        while (connectedFlag.get()) {
            try {
                final List<CommandAndHandler> toProcess = commandBatcher.getWaitingBatches().getVal();
                final List<Command> commands = new ArrayList<>();
                for (CommandAndHandler c : toProcess) {
                    commands.add(c.getCommand());
                }

                if (LOG.isDebugEnabled()) {
                    final StringBuffer buffer = new StringBuffer();
                    for (Command command : commands) {
                        buffer.append(command.getClass().getSimpleName());
                        buffer.append(", ");
                    }
                    LOG.trace("Sending {} commands to server: {}", commands.size(), buffer.substring(0, buffer.length() - 2));
                } else {
                    LOG.trace("Sending {} commands to server", commands.size());
                }

                final List<? extends Command> answers = transmit(commands);

                uiExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        processResults(answers, toProcess);
                    }
                });
            } catch (Exception e) {
                if (connectedFlag.get()) {
                    handleError(e);
                } else {
                    LOG.warn("Remoting error based on broken connection in parallel request", e);
                }
            }
            if(!longPollingActivated && useLongPolling.get()) {
                uiExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        listen();
                    }
                });
                longPollingActivated = true;
            }
        }
    }

    protected abstract List<Command> transmit(final List<Command> commands) throws RemotingException;

    public void send(final Command command, final OnFinishedHandler callback, final HandlerType handlerType) {
        LOG.trace("Command of type {} should be withContent to server", command.getClass().getSimpleName());
        if (!connectedFlag.get()) {
            //TODO: Change to RemotingException
            throw new IllegalStateException("Connection is broken");
        }
        // we have some change so regardless of the batching we may have to release a push
        if (!command.equals(pushListener)) {
            release();
        }
        // we are inside the UI thread and events calls come in strict order as received by the UI toolkit
        final CommandAndHandler handler = new CommandAndHandler(command, callback, handlerType);
        commandBatcher.batch(handler);
    }

    public void send(final Command command, final OnFinishedHandler callback) {
        send(command, callback, HandlerType.UI);
    }

    public void send(final Command command) {
        send(command, null);
    }

    protected void processResults(final List<? extends Command> response, final List<CommandAndHandler> commandsAndHandlers) {

        if (LOG.isDebugEnabled() && response.size() > 0) {
            final StringBuffer buffer = new StringBuffer();
            for (final Command command : response) {
                buffer.append(command.getClass().getSimpleName());
                buffer.append(", ");
            }
            LOG.trace("Processing {} commands from server: {}", response.size(), buffer.substring(0, buffer.length() - 2));
        } else {
            LOG.trace("Processing {} commands from server", response.size());
        }

        for (final Command serverCommand : response) {
            dispatchHandle(serverCommand);
        }

        final OnFinishedHandler callback = commandsAndHandlers.get(0).getHandler();
        if (callback != null) {
            LOG.trace("Handling registered callback");
            try {
                callback.onFinished();
            } catch (Exception e) {
                LOG.error("Error in handling callback", e);
                throw e;
            }
        }
    }

    public void dispatchHandle(final Command command) {
        responseHandler.dispatchHandle(command);
    }

    /**
     * listens for the pushListener to return. The pushListener must be set and pushEnabled must be true.
     */
    protected void listen() {
        if (!connectedFlag.get() || releaseNeeded.get()) {
            return;
        }

        releaseNeeded.set(true);
        try {
            send(pushListener, new OnFinishedHandler() {
                @Override
                public void onFinished() {
                    releaseNeeded.set(false);
                    listen();
                }
            });
        } catch (Exception e) {
            LOG.error("Error in sending long poll", e);
        }
    }

    /**
     * Release the current push listener, which blocks the sending queue.
     * Does nothing in case that the push listener is not active.
     */
    protected void release() {
        if (!releaseNeeded.get()) {
            return; // there is no point in releasing if we do not wait. Avoid excessive releasing.
        }

        releaseNeeded.set(false);// release is under way
        backgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Command> releaseCommandList = new ArrayList<Command>(Collections.singletonList(releaseCommand));
                    transmit(releaseCommandList);
                } catch (RemotingException e) {
                    handleError(e);
                }
            }
        });
    }

    public void connect(final boolean longPoll) {
        if (connectedFlag.get()) {
            throw new IllegalStateException("Can not call connect on a connected connection");
        }

        connectedFlag.set(true);
        uiExecutor.execute(new Runnable() {
            @Override
            public void run() {
                connectionFlagForUiExecutor = true;
            }
        });

        backgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                commandProcessing();
            }
        });
        useLongPolling.set(longPoll);
    }

    public void connect() {
        connect(true);
    }

    public void disconnect() {
        if (!connectedFlag.get()) {
            throw new IllegalStateException("Can not call disconnect on a disconnected connection");
        }
        connectedFlag.set(false);
        uiExecutor.execute(new Runnable() {
            @Override
            public void run() {
                connectionFlagForUiExecutor = false;
            }
        });
    }

    protected Command getReleaseCommand() {
        return releaseCommand;
    }

}
