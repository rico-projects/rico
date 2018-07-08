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

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.MappingException;
import dev.rico.internal.remoting.communication.commands.impl.CallActionCommand;
import dev.rico.internal.remoting.communication.commands.impl.DestroyControllerCommand;
import dev.rico.client.remoting.ControllerInitalizationException;
import dev.rico.client.remoting.ControllerProxy;
import dev.rico.client.remoting.Param;
import dev.rico.remoting.converter.ValueConverterException;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ControllerProxyImpl<T> implements ControllerProxy<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerProxyImpl.class);

    private final String controllerId;

    private final ControllerProxyFactory controllerProxyFactory;

    private T model;

    private volatile boolean destroyed = false;

    private final RemotingCommandHandler commandHandler;

    private final ClientRepository repository;

    public ControllerProxyImpl(final String controllerId, final T model, final ClientRepository repository, final RemotingCommandHandler commandHandler, final ControllerProxyFactory controllerProxyFactory) {
        this.controllerId = Assert.requireNonBlank(controllerId, "controllerId");
        this.controllerProxyFactory = Assert.requireNonNull(controllerProxyFactory, "controllerProxyFactory");
        this.model = model;
        this.commandHandler = Assert.requireNonNull(commandHandler, "commandHandler");
        this.repository = Assert.requireNonNull(repository, "repository");
    }

    @Override
    public T getModel() {
        return model;
    }

    @Override
    public CompletableFuture<Void> invoke(final String actionName, final Map<String, ?> params) {
        final List<Param> paramList = params.entrySet().stream().
                map(e -> new Param(e.getKey(), e.getValue())).
                collect(Collectors.toList());
        return invoke(actionName, paramList.toArray(new Param[paramList.size()]));
    }

    @Override
    public CompletableFuture<Void> invoke(final String actionName, final Param... params) {
        if (destroyed) {
            throw new IllegalStateException("The controller was already destroyed");
        }

        final CallActionCommand callActionCommand = new CallActionCommand();
        callActionCommand.setControllerId(controllerId);
        callActionCommand.setActionName(actionName);
        if(params != null) {
            for (Param param : params) {
                Object value = param.getValue();
                if(value == null) {
                    callActionCommand.addParam(param.getName(), null);
                } else {
                    try {
                        callActionCommand.addParam(param.getName(), repository.getConverter(value.getClass()).convertToRemoting(value));
                    } catch (ValueConverterException e) {
                        throw new MappingException("Error in value conversion of param '" + param.getName() + "' for action '" + actionName + "'", e);
                    }
                }
            }
        }


        return commandHandler.sendAndReact(callActionCommand);
        //TODO: Here we have the special case for error return.....

    }

    @Override
    public CompletableFuture<Void> destroy() {
        if (destroyed) {
            throw new IllegalStateException("The controller was already destroyed");
        }
        destroyed = true;
        final DestroyControllerCommand destroyControllerCommand = new DestroyControllerCommand();
        destroyControllerCommand.setControllerId(controllerId);
        return commandHandler.sendAndReact(destroyControllerCommand);
    }

    @Override
    public <C> CompletableFuture<ControllerProxy<C>> createController(String name) {
        Assert.requireNonBlank(name, "name");

        return controllerProxyFactory.<C>create(name, controllerId).handle((ControllerProxy<C> cControllerProxy, Throwable throwable) -> {
            if (throwable != null) {
                throw new ControllerInitalizationException("Error while creating subcontroller of type " + name, throwable);
            }
            return cControllerProxy;
        });
    }
}
