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
package dev.rico.internal.server.remoting.context;

import dev.rico.internal.core.Assert;
import dev.rico.internal.server.remoting.model.BeanManagerImpl;
import dev.rico.internal.remoting.communication.commands.*;
import dev.rico.internal.remoting.communication.converters.Converters;
import dev.rico.internal.server.client.ClientSessionProvider;
import dev.rico.internal.server.remoting.config.RemotingConfiguration;
import dev.rico.internal.server.remoting.controller.ControllerHandler;
import dev.rico.internal.server.remoting.controller.ControllerRepository;
import dev.rico.internal.server.remoting.gc.GarbageCollectionCallback;
import dev.rico.internal.server.remoting.gc.GarbageCollector;
import dev.rico.internal.server.remoting.gc.Instance;
import dev.rico.internal.server.remoting.model.ServerRepository;
import dev.rico.internal.server.remoting.servlet.ServerTimingFilter;
import dev.rico.server.remoting.BeanManager;
import dev.rico.server.client.ClientSession;
import dev.rico.server.spi.components.ManagedBeanFactory;
import dev.rico.server.timing.Metric;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * This class defines the central entry point for a remoting session on the server.
 * Each client context on the client side is connected with one {@link ServerRemotingContext}.
 */
@API(since = "0.x", status = INTERNAL)
public class ServerRemotingContext {

    private static final Logger LOG = LoggerFactory.getLogger(ServerRemotingContext.class);

    private final RemotingConfiguration configuration;

    private final BeanManager beanManager;

    private final ControllerHandler controllerHandler;

    private final Consumer<ServerRemotingContext> onDestroyCallback;

    private final GarbageCollector garbageCollector;

    private final RemotingContextTaskQueue taskQueue;

    private final ClientSession clientSession;

    private boolean hasResponseCommands = false;

    private boolean active = false;

    private final ServerRepository beanRepository;

    public ServerRemotingContext(final RemotingConfiguration configuration, ClientSession clientSession, ClientSessionProvider clientSessionProvider, ManagedBeanFactory beanFactory, ControllerRepository controllerRepository, Consumer<ServerRemotingContext> onDestroyCallback) {
        this.configuration = Assert.requireNonNull(configuration, "configuration");
        Assert.requireNonNull(beanFactory, "beanFactory");
        Assert.requireNonNull(controllerRepository, "controllerRepository");
        this.onDestroyCallback = Assert.requireNonNull(onDestroyCallback, "onDestroyCallback");
        this.clientSession = Assert.requireNonNull(clientSession, "clientSession");

        beanRepository = new ServerRepository(null);

        //Init Garbage Collection
        garbageCollector = new GarbageCollector(configuration, new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    beanRepository.deleteBean(instance.getBean());
                }
            }
        });

        CommunicationManager manager = new CommunicationManager() {
            @Override
            public boolean hasResponseCommands() {
                return hasResponseCommands;
      //          return hasResponseCommands || serverModelStore.hasResponseCommands();
            }
        };
        taskQueue = new RemotingContextTaskQueue(clientSession.getId(), clientSessionProvider, manager, configuration.getMaxPollTime(), TimeUnit.MILLISECONDS);


        beanManager = new BeanManagerImpl(beanRepository);


        //Init ControllerHandler
        controllerHandler = new ControllerHandler(beanFactory, beanRepository, controllerRepository);

        //Register commands
        registerDefaultCommands();
    }

    protected <T extends Command> void registerCommand(final Class<T> commandClass, final Consumer<T> handler) {
        Assert.requireNonNull(commandClass, "commandClass");
        Assert.requireNonNull(handler, "handler");
//        registry.register(commandClass, new CommandHandler() {
//            @Override
//            public void handleCommand(final Command command, final List response) {
//                LOG.trace("Handling {} for ServerRemotingContext {}", commandClass.getSimpleName(), getId());
//                handler.accept((T) command);
//            }
//        });
    }

    private void registerDefaultCommands() {
        registerCommand(CreateContextCommand.class, (c) -> onInitContext());
        registerCommand(DestroyContextCommand.class, (c) -> onDestroyContext());
        registerCommand(CreateControllerCommand.class, (createControllerCommand) -> {
            Assert.requireNonNull(createControllerCommand, "createControllerCommand");
            onCreateController(createControllerCommand.getControllerName(), createControllerCommand.getParentControllerId());
        });
        registerCommand(DestroyControllerCommand.class, (destroyControllerCommand) -> {
            Assert.requireNonNull(destroyControllerCommand, "destroyControllerCommand");
            onDestroyController(destroyControllerCommand.getControllerId());
        });
        registerCommand(CallActionCommand.class, (callActionCommand) -> {
            Assert.requireNonNull(callActionCommand, "callActionCommand");
            onCallControllerAction(callActionCommand.getControllerId(), callActionCommand.getActionName(), callActionCommand.getParams());
        });
        //registerCommand(registry, StartLongPollCommand.class, (c) -> onLongPoll());
        //registerCommand(registry, InterruptLongPollCommand.class, (c) -> interrupt());
    }

    private void onInitContext() {
    }

    private void onDestroyContext() {
        destroy();
    }

    public void destroy() {
        controllerHandler.destroyAllControllers();

        onDestroyCallback.accept(this);
    }

    private void onCreateController(final String controllerName, final String parentControllerId) {
        Assert.requireNonBlank(controllerName, "controllerName");

       // final InternalAttributesBean bean = platformBeanRepository.getInternalAttributesBean();
        final String controllerId = controllerHandler.createController(controllerName, parentControllerId);

        //bean.setControllerId(controllerId);
        Object model = controllerHandler.getControllerModel(controllerId);
        if (model != null) {
          //  bean.setModel(model);
        }
    }

    private void onDestroyController(final String controllerId) {
        Assert.requireNonBlank(controllerId, "controllerId");
        controllerHandler.destroyController(controllerId);
    }

    private void onCallControllerAction(final String controllerId, final String actionName, final Map<String, Object> params) {
        Assert.requireNonBlank(controllerId, "controllerId");
        Assert.requireNonBlank(actionName, "actionName");
        Assert.requireNonNull(params, "params");

        final Metric metric = ServerTimingFilter.getCurrentTiming().start("RemotingActionCall:" + actionName, "Remote action call");
        try {
            controllerHandler.invokeAction(controllerId, actionName, params);
        } catch (final Exception e) {
            LOG.error("Unexpected exception while invoking action {} on controller {}",
                    actionName, controllerId, e);
            //bean.setError(true);
        } finally {
            metric.stop();
        }
    }

    public void interrupt() {
        taskQueue.interrupt();
    }

    protected void onLongPoll() {
        if (configuration.isUseGc()) {
            LOG.trace("Handling GarbageCollection for ServerRemotingContext {}", getId());
            onGarbageCollection();
        }
        final Metric metric = ServerTimingFilter.getCurrentTiming().start("TaskExecution", "Execution of Tasks in Long Poll");
        try {
            taskQueue.executeTasks();
        } finally {
            metric.stop();
        }
    }

    private void onGarbageCollection() {
        final Metric metric = ServerTimingFilter.getCurrentTiming().start("RemotingGc", "Garbage collection for the remoting model");
        try {
            garbageCollector.gc();
        } finally {
            metric.stop();
        }
    }

    public BeanManager getBeanManager() {
        return beanManager;
    }

    public String getId() {
        return clientSession.getId();
    }

    public List<Command> handle(final List<Command> commands) {
        active = true;
        try {
            final List<Command> results = new LinkedList<>();
            for (final Command command : commands) {
               // results.addAll(serverConnector.receive(command));
                hasResponseCommands = !results.isEmpty();
            }
            return results;
        } finally {
            active = false;
        }
    }

    public ClientSession getClientSession() {
        return clientSession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerRemotingContext that = (ServerRemotingContext) o;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public Future<Void> runLater(final Runnable runnable) {
        Assert.requireNonNull(runnable, "runnable");
        return callLater(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                runnable.run();
                return null;
            }
        });
    }

    public <T> Future<T> callLater(final Callable<T> callable) {
        return taskQueue.addTask(callable);
    }

    public boolean isActive() {
        return active;
    }
}
