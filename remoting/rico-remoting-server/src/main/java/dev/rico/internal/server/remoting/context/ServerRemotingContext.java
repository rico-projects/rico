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
import dev.rico.internal.remoting.communication.commands.impl.*;
import dev.rico.internal.remoting.communication.merge.CommandMergeUtils;
import dev.rico.internal.server.remoting.model.BeanManagerImpl;
import dev.rico.internal.remoting.communication.commands.*;
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

import java.util.*;
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

    private final ServerRepository beanRepository;

    private final Map<Class, CommandHandler> commandHandlers;

    private final Queue<Command> toSendQueue;

    public ServerRemotingContext(final RemotingConfiguration configuration, ClientSession clientSession, ClientSessionProvider clientSessionProvider, ManagedBeanFactory beanFactory, ControllerRepository controllerRepository, Consumer<ServerRemotingContext> onDestroyCallback) {
        this.configuration = Assert.requireNonNull(configuration, "configuration");
        Assert.requireNonNull(beanFactory, "beanFactory");
        Assert.requireNonNull(controllerRepository, "controllerRepository");
        this.onDestroyCallback = Assert.requireNonNull(onDestroyCallback, "onDestroyCallback");
        this.clientSession = Assert.requireNonNull(clientSession, "clientSession");


        this.commandHandlers = new HashMap<>();

        beanRepository = new ServerRepository(() -> getGarbageCollector(), command -> sendCommand(command));

        //Init Garbage Collection
        garbageCollector = new GarbageCollector(configuration, new GarbageCollectionCallback() {
            @Override
            public void onReject(Set<Instance> instances) {
                for (Instance instance : instances) {
                    try {
                        beanRepository.deleteBean(instance.getBean());
                    } catch (Exception e) {
                        LOG.error("Error in bean garbage collection!", e);
                        throw new RuntimeException("Error in bean garbage collection!", e);
                    }
                }
            }
        });

        CommunicationManager manager = new CommunicationManager() {
            @Override
            public boolean hasResponseCommands() {
                return !toSendQueue.isEmpty();
            }
        };
        taskQueue = new RemotingContextTaskQueue(clientSession.getId(), clientSessionProvider, manager, configuration.getMaxPollTime(), TimeUnit.MILLISECONDS);


        beanManager = new BeanManagerImpl(beanRepository);


        //Init ControllerHandler
        controllerHandler = new ControllerHandler(beanFactory, beanRepository, controllerRepository);

        toSendQueue = new LinkedList<>();

        registerCommand(CreateContextCommand.class, command -> onInitContext());

        registerCommand(DestroyContextCommand.class, command -> onDestroyContext());

        registerCommand(CreateControllerCommand.class, command -> {
            Assert.requireNonNull(command, "command");
            onCreateController(command.getControllerName(), command.getParentControllerId());
        });

        registerCommand(DestroyControllerCommand.class, command -> {
            Assert.requireNonNull(command, "command");
            onDestroyController(command.getControllerId());
        });

        registerCommand(CallActionCommand.class, command -> {
            Assert.requireNonNull(command, "command");
            onCallControllerAction(command.getControllerId(), command.getActionName(), command.getParams());
        });

        registerCommand(ValueChangedCommand.class, command -> beanRepository.onValueChangedCommand(command));

        registerCommand(ListAddCommand.class, command -> beanRepository.onListAddCommand(command));

        registerCommand(ListReplaceCommand.class, command -> beanRepository.onListReplaceCommand(command));

        registerCommand(ListRemoveCommand.class, command -> beanRepository.onListRemoveCommand(command));
    }

    private void sendCommand(final Command c) {
        toSendQueue.offer(c);
    }

    protected  <T extends Command> void registerCommand(final Class<T> commandClass, final CommandHandler<T> handler) {
        Assert.requireNonNull(commandClass, "commandClass");
        Assert.requireNonNull(handler, "handler");
        commandHandlers.put(commandClass, handler);
    }

    public List<Command> handle(final List<Command> commands) {
        for (final Command command : commands) {
            final Class<?> commandClass = command.getClass();
            final CommandHandler handler = commandHandlers.get(commandClass);
            Assert.requireNonNull(handler, "handler");
            try {
                handler.handle(command);
            } catch (final Exception e) {
                //TODO:
                throw new RuntimeException("Error in command handling", e);
            }
        }

        onLongPoll();

        final List<Command> results = new LinkedList<>();
        while (!toSendQueue.isEmpty()) {
            final Command nextCommand = toSendQueue.poll();
            doPossibleMerge(results, nextCommand);
            results.add(nextCommand);
        }
        return results;
    }

    private void doPossibleMerge(final List<Command> current, final Command nextCommand) {
        if(nextCommand instanceof ValueChangedCommand) {
            ListIterator<Command> iterator = current.listIterator();
            while (iterator.hasNext()) {
                final Command command = iterator.next();
                if(command instanceof ValueChangedCommand) {
                    CommandMergeUtils.checkValueChangedCommand((ValueChangedCommand)command, () -> iterator.remove(), (ValueChangedCommand)nextCommand);
                }
            }
        } else if(nextCommand instanceof ListAddCommand) {
            ListIterator<Command> iterator = current.listIterator();
            while (iterator.hasNext()) {
                final Command command = iterator.next();
                if(command instanceof ListAddCommand) {
                    CommandMergeUtils.checkListAddCommand((ListAddCommand)command, () -> iterator.remove(), (ListAddCommand)nextCommand);
                }
            }
        } else if(nextCommand instanceof ListReplaceCommand) {
            ListIterator<Command> iterator = current.listIterator();
            while (iterator.hasNext()) {
                final Command command = iterator.next();
                if(command instanceof ListReplaceCommand) {
                    CommandMergeUtils.checkListReplaceCommand((ListReplaceCommand)command, () -> iterator.remove(), (ListReplaceCommand)nextCommand);
                }
            }
        } else if(nextCommand instanceof ListRemoveCommand) {
            ListIterator<Command> iterator = current.listIterator();
            while (iterator.hasNext()) {
                final Command command = iterator.next();
                if(command instanceof ListRemoveCommand) {
                    CommandMergeUtils.checkListRemoveCommand((ListRemoveCommand)command, () -> iterator.remove(), (ListRemoveCommand)nextCommand);
                }
            }
        }
    }


    private void onInitContext() {
    }

    private void onDestroyContext() {
        destroy();
    }

    public void destroy() {
        try {
            controllerHandler.destroyAllControllers();
        } catch (Exception e) {
            LOG.error("Error in destroying remoting context!", e);
        } finally {
            onDestroyCallback.accept(this);
        }
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

    private void onDestroyController(final String controllerId) throws Exception {
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
            LOG.error("Unexpected exception while invoking action '{}' on controller {}",
                    actionName, controllerId, e);
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

    protected void onGarbageCollection() {
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

    private GarbageCollector getGarbageCollector() {
        return garbageCollector;
    }
}
