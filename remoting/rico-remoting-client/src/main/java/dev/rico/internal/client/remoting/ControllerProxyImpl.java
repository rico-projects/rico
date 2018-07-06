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
import dev.rico.internal.remoting.communication.converters.Converters;
import dev.rico.internal.remoting.MappingException;
import dev.rico.internal.remoting.communication.commands.impl.CallActionCommand;
import dev.rico.internal.remoting.communication.commands.impl.DestroyControllerCommand;
import dev.rico.client.remoting.ControllerActionException;
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

    private final AbstractClientConnector clientConnector;

    private final ClientPlatformBeanRepository platformBeanRepository;

    private final ControllerProxyFactory controllerProxyFactory;

    private final Converters converters;

    private T model;

    private volatile boolean destroyed = false;

    public ControllerProxyImpl(final String controllerId, final T model, final AbstractClientConnector clientConnector, final ClientPlatformBeanRepository platformBeanRepository, final ControllerProxyFactory controllerProxyFactory, final Converters converters) {
        this.clientConnector = Assert.requireNonNull(clientConnector, "clientConnector");
        this.controllerId = Assert.requireNonBlank(controllerId, "controllerId");
        this.controllerProxyFactory = Assert.requireNonNull(controllerProxyFactory, "controllerProxyFactory");
        this.model = model;
        this.platformBeanRepository = Assert.requireNonNull(platformBeanRepository, "platformBeanRepository");
        this.converters = Assert.requireNonNull(converters, "converters");
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

        final ClientControllerActionCallBean bean = platformBeanRepository.createControllerActionCallBean(controllerId, actionName, params);

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
                        callActionCommand.addParam(param.getName(), converters.getConverter(value.getClass()).convertToRemoting(value));
                    } catch (ValueConverterException e) {
                        throw new MappingException("Error in value conversion of param '" + param.getName() + "' for action '" + actionName + "'", e);
                    }
                }
            }
        }

        final CompletableFuture<Void> result = new CompletableFuture<>();
        clientConnector.send(callActionCommand, () -> {

                if (bean.isError()) {
                    result.completeExceptionally(new ControllerActionException("Error on calling action on the server. Please check the server log."));
                } else {
                    result.complete(null);
                }
                bean.unregister();
        });
        return result;
    }

    @Override
    public CompletableFuture<Void> destroy() {
        if (destroyed) {
            throw new IllegalStateException("The controller was already destroyed");
        }
        destroyed = true;

        final InternalAttributesBean bean = platformBeanRepository.getInternalAttributesBean();

        final CompletableFuture<Void> ret = new CompletableFuture<>();

        final DestroyControllerCommand destroyControllerCommand = new DestroyControllerCommand();
        destroyControllerCommand.setControllerId(controllerId);

        clientConnector.send(destroyControllerCommand, () -> {
            model = null;
            ret.complete(null);
        });
        return ret;
    }

    @Override
    public <C> CompletableFuture<ControllerProxy<C>> createController(String name) {
        Assert.requireNonBlank(name, "name");

        return controllerProxyFactory.<C>create(name, controllerId).handle((ControllerProxy<C> cControllerProxy, Throwable throwable) -> {
            if (throwable != null) {
                throw new ControllerInitalizationException("Error while creating controller of type " + name, throwable);
            }
            return cControllerProxy;
        });
    }
}
