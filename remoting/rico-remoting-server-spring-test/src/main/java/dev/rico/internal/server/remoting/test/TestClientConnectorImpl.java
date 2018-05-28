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
package dev.rico.internal.server.remoting.test;

import dev.rico.internal.client.remoting.legacy.ClientModelStore;
import dev.rico.internal.client.remoting.legacy.communication.AbstractClientConnector;
import dev.rico.internal.client.remoting.legacy.communication.CommandAndHandler;
import dev.rico.internal.client.remoting.legacy.communication.CommandBatcher;
import dev.rico.internal.client.remoting.legacy.communication.OnFinishedHandler;
import dev.rico.internal.client.remoting.legacy.communication.SimpleExceptionHandler;
import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.legacy.commands.StartLongPollCommand;
import dev.rico.internal.remoting.legacy.communication.Command;
import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class TestClientConnectorImpl extends AbstractClientConnector {

    private final Function<List<Command>, List<Command>> communicationFunction;

    public TestClientConnectorImpl(final ClientModelStore clientModelStore, final Executor uiExecutor, final Function<List<Command>, List<Command>> communicationFunction) {
        super(clientModelStore, uiExecutor, new CommandBatcher(), new SimpleExceptionHandler(), Executors.newCachedThreadPool());
        this.communicationFunction = Assert.requireNonNull(communicationFunction, "communicationFunction");
    }

    @Override
    protected void commandProcessing() {
        /* do nothing! */
    }

    @Override
    public void connect() {
        /* do nothing! */
    }

    @Override
    public void disconnect() {
         /* do nothing! */
    }

    @Override
    public void send(Command command, OnFinishedHandler callback) {
        List<Command> answer = transmit(new ArrayList<>(Arrays.asList(command)));
        CommandAndHandler handler = new CommandAndHandler(command, callback);
        processResults(answer, new ArrayList<>(Arrays.asList(handler)));
    }

    @Override
    public void send(Command command) {
        send(command, null);
    }

    @Override
    protected void listen() {
        //TODO: no implementation since EventBus is used in a different way for this tests. Should be refactored in parent class.
    }

    @Override
    protected List<Command> transmit(List<Command> commands) {
        ArrayList<Command> realCommands = new ArrayList<>(commands);
        realCommands.add(new StartLongPollCommand());
        return communicationFunction.apply(commands);
    }

}
