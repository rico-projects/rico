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

import dev.rico.client.remoting.ControllerProxy;
import dev.rico.internal.remoting.communication.commands.impl.CreateControllerCommand;
import dev.rico.internal.core.Assert;
import org.apiguardian.api.API;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ControllerProxyFactory {

    private final RemotingCommandHandler commandHandler;

    private final ClientRepository repository;

    private final AtomicLong controllerIdCounter;

    private final AtomicLong modelIdCounter;

    public ControllerProxyFactory(final RemotingCommandHandler commandHandler, ClientRepository repository) {
        this.commandHandler = Assert.requireNonNull(commandHandler, "commandHandler");
        this.repository = Assert.requireNonNull(repository, "repository");
        this.controllerIdCounter = new AtomicLong();
        this.modelIdCounter = new AtomicLong();
    }

    public <T> CompletableFuture<ControllerProxy<T>> create(String name) {
       return create(name, null);
    }

    public <T> CompletableFuture<ControllerProxy<T>> create(String name, String parentControllerId) {
        Assert.requireNonBlank(name, "name");
        final String controllerId = controllerIdCounter.incrementAndGet() + "";
        final String modelId = modelIdCounter.incrementAndGet() + "";
        final CreateControllerCommand createControllerCommand = new CreateControllerCommand();
        createControllerCommand.setControllerId(controllerId);
        createControllerCommand.setModelId(modelId);
        createControllerCommand.setControllerName(name);
        if(parentControllerId != null) {
            createControllerCommand.setParentControllerId(parentControllerId);
        }

        return commandHandler.sendAndReact(createControllerCommand).thenApply(new Function<Void, ControllerProxy<T>>() {
            @Override
            public ControllerProxy<T> apply(Void aVoid) {
                final T model = (T) repository.getBean(modelId);
                return new ControllerProxyImpl<T>(controllerId, (T) model, repository, commandHandler,ControllerProxyFactory.this);
            }
        });
    }
}
