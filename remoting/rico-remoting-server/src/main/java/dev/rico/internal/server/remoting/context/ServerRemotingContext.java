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
import dev.rico.internal.remoting.BeanManagerImpl;
import dev.rico.internal.remoting.ClassRepository;
import dev.rico.internal.remoting.ClassRepositoryImpl;
import dev.rico.internal.remoting.Converters;
import dev.rico.internal.remoting.EventDispatcher;
import dev.rico.internal.remoting.InternalAttributesBean;
import dev.rico.internal.remoting.ListMapper;
import dev.rico.internal.remoting.PresentationModelBuilderFactory;
import dev.rico.internal.remoting.codec.OptimizedJsonCodec;
import dev.rico.internal.remoting.collections.ListMapperImpl;
import dev.rico.internal.remoting.commands.CallActionCommand;
import dev.rico.internal.remoting.commands.CreateContextCommand;
import dev.rico.internal.remoting.commands.CreateControllerCommand;
import dev.rico.internal.remoting.commands.DestroyContextCommand;
import dev.rico.internal.remoting.commands.DestroyControllerCommand;
import dev.rico.internal.remoting.legacy.commands.InterruptLongPollCommand;
import dev.rico.internal.remoting.legacy.commands.StartLongPollCommand;
import dev.rico.internal.remoting.legacy.communication.Command;
import dev.rico.internal.server.client.ClientSessionProvider;
import dev.rico.internal.server.remoting.config.RemotingConfiguration;
import dev.rico.internal.server.remoting.controller.ControllerHandler;
import dev.rico.internal.server.remoting.controller.ControllerRepository;
import dev.rico.internal.server.remoting.gc.GarbageCollectionCallback;
import dev.rico.internal.server.remoting.gc.GarbageCollector;
import dev.rico.internal.server.remoting.gc.Instance;
import dev.rico.internal.server.remoting.legacy.ServerConnector;
import dev.rico.internal.server.remoting.legacy.ServerModelStore;
import dev.rico.internal.server.remoting.legacy.action.AbstractServerAction;
import dev.rico.internal.server.remoting.legacy.communication.ActionRegistry;
import dev.rico.internal.server.remoting.legacy.communication.CommandHandler;
import dev.rico.internal.server.remoting.model.ServerBeanBuilder;
import dev.rico.internal.server.remoting.model.ServerBeanBuilderImpl;
import dev.rico.internal.server.remoting.model.ServerBeanRepository;
import dev.rico.internal.server.remoting.model.ServerBeanRepositoryImpl;
import dev.rico.internal.server.remoting.model.ServerControllerActionCallBean;
import dev.rico.internal.server.remoting.model.ServerEventDispatcher;
import dev.rico.internal.server.remoting.model.ServerPlatformBeanRepository;
import dev.rico.internal.server.remoting.model.ServerPresentationModelBuilderFactory;
import dev.rico.internal.server.servlet.ServerTimingFilter;
import dev.rico.remoting.BeanManager;
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

    private final ServerModelStore serverModelStore;

    private final ServerConnector serverConnector;

    private final ServerBeanRepository beanRepository;

    private final Converters converters;

    private final BeanManager beanManager;

    private final ControllerHandler controllerHandler;

    private final EventDispatcher dispatcher;

    private ServerPlatformBeanRepository platformBeanRepository;

    private final Consumer<ServerRemotingContext> onDestroyCallback;

    private final GarbageCollector garbageCollector;

    private final RemotingContextTaskQueue taskQueue;

    private final ClientSession clientSession;

    private boolean hasResponseCommands = false;

    private boolean active = false;

    public ServerRemotingContext(final RemotingConfiguration configuration, ClientSession clientSession, ClientSessionProvider clientSessionProvider, ManagedBeanFactory beanFactory, ControllerRepository controllerRepository, Consumer<ServerRemotingContext> onDestroyCallback) {
        this.configuration = Assert.requireNonNull(configuration, "configuration");
        Assert.requireNonNull(beanFactory, "beanFactory");
        Assert.requireNonNull(controllerRepository, "controllerRepository");
        this.onDestroyCallback = Assert.requireNonNull(onDestroyCallback, "onDestroyCallback");
        this.clientSession = Assert.requireNonNull(clientSession, "clientSession");

        serverModelStore = new ServerModelStore();

        //Init Server Connector
        serverConnector = new ServerConnector();
        serverConnector.setCodec(OptimizedJsonCodec.getInstance());
        serverConnector.setServerModelStore(serverModelStore);
        serverConnector.registerDefaultActions();

        //Init Garbage Collection
        garbageCollector = new GarbageCollector(configuration, new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    beanRepository.onGarbageCollectionRejection(instance.getBean());
                }
            }
        });

        CommunicationManager manager = new CommunicationManager() {
            @Override
            public boolean hasResponseCommands() {
                return hasResponseCommands || serverModelStore.hasResponseCommands();
            }
        };
        taskQueue = new RemotingContextTaskQueue(clientSession.getId(), clientSessionProvider, manager, configuration.getMaxPollTime(), TimeUnit.MILLISECONDS);

        //Init BeanRepository
        dispatcher = new ServerEventDispatcher(serverModelStore);
        beanRepository = new ServerBeanRepositoryImpl(serverModelStore, dispatcher, garbageCollector);
        converters = new Converters(beanRepository);

        //Init BeanManager
        final PresentationModelBuilderFactory builderFactory = new ServerPresentationModelBuilderFactory(serverModelStore);
        final ClassRepository classRepository = new ClassRepositoryImpl(serverModelStore, converters, builderFactory);
        final ListMapper listMapper = new ListMapperImpl(serverModelStore, classRepository, beanRepository, builderFactory, dispatcher);
        final ServerBeanBuilder beanBuilder = new ServerBeanBuilderImpl(classRepository, beanRepository, listMapper, builderFactory, dispatcher, garbageCollector);
        beanManager = new BeanManagerImpl(beanRepository, beanBuilder);


        //Init ControllerHandler
        controllerHandler = new ControllerHandler(beanFactory, beanBuilder, beanRepository, controllerRepository, converters);

        //Register commands
        registerDefaultCommands();
    }

    protected <T extends Command> void registerCommand(final ActionRegistry registry, final Class<T> commandClass, final Consumer<T> handler) {
        Assert.requireNonNull(registry, "registry");
        Assert.requireNonNull(commandClass, "commandClass");
        Assert.requireNonNull(handler, "handler");
        registry.register(commandClass, new CommandHandler() {
            @Override
            public void handleCommand(final Command command, final List response) {
                LOG.trace("Handling {} for ServerRemotingContext {}", commandClass.getSimpleName(), getId());
                handler.accept((T) command);
            }
        });
    }

    private void registerDefaultCommands() {
        serverConnector.register(new AbstractServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {
                registerCommand(registry, CreateContextCommand.class, (c) -> onInitContext());
                registerCommand(registry, DestroyContextCommand.class, (c) -> onDestroyContext());
                registerCommand(registry, CreateControllerCommand.class, (createControllerCommand) -> {
                    Assert.requireNonNull(createControllerCommand, "createControllerCommand");
                    onCreateController(createControllerCommand.getControllerName(), createControllerCommand.getParentControllerId());
                });
                registerCommand(registry, DestroyControllerCommand.class, (destroyControllerCommand) -> {
                    Assert.requireNonNull(destroyControllerCommand, "destroyControllerCommand");
                    onDestroyController(destroyControllerCommand.getControllerId());
                });
                registerCommand(registry, CallActionCommand.class, (callActionCommand) -> {
                    Assert.requireNonNull(callActionCommand, "callActionCommand");
                    onCallControllerAction(callActionCommand.getControllerId(), callActionCommand.getActionName(), callActionCommand.getParams());
                });
                registerCommand(registry, StartLongPollCommand.class, (c) -> onLongPoll());
                registerCommand(registry, InterruptLongPollCommand.class, (c) -> interrupt());
            }
        });
    }

    private void onInitContext() {
        platformBeanRepository = new ServerPlatformBeanRepository(serverModelStore, beanRepository, dispatcher, converters);
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

        if (platformBeanRepository == null) {
            throw new IllegalStateException("An action was called before the init-command was sent.");
        }
        final InternalAttributesBean bean = platformBeanRepository.getInternalAttributesBean();
        final String controllerId = controllerHandler.createController(controllerName, parentControllerId);

        bean.setControllerId(controllerId);
        Object model = controllerHandler.getControllerModel(controllerId);
        if (model != null) {
            bean.setModel(model);
        }
    }

    private void onDestroyController(final String controllerId) {
        Assert.requireNonBlank(controllerId, "controllerId");
        if (platformBeanRepository == null) {
            throw new IllegalStateException("An action was called before the init-command was sent.");
        }
        controllerHandler.destroyController(controllerId);
    }

    private void onCallControllerAction(final String controllerId, final String actionName, final Map<String, Object> params) {
        Assert.requireNonBlank(controllerId, "controllerId");
        Assert.requireNonBlank(actionName, "actionName");
        Assert.requireNonNull(params, "params");

        //TODO: Remove this. Should bve handled by commands.
        final ServerControllerActionCallBean bean = platformBeanRepository.getControllerActionCallBean();
        Assert.requireNonNull(bean, "bean");

        if (platformBeanRepository == null) {
            throw new IllegalStateException("An action was called before the init-command was sent.");
        }
        final Metric metric = ServerTimingFilter.getCurrentTiming().start("RemotingActionCall:"+actionName, "Remote action call");
        try {
            controllerHandler.invokeAction(controllerId, actionName, params);
        } catch (final Exception e) {
            LOG.error("Unexpected exception while invoking action {} on controller {}",
                    actionName, controllerId, e);
            bean.setError(true);
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

    public ServerModelStore getServerModelStore() {
        return serverModelStore;
    }

    public ServerConnector getServerConnector() {
        return serverConnector;
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
                results.addAll(serverConnector.receive(command));
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
