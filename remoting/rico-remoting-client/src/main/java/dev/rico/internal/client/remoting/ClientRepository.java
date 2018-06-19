package dev.rico.internal.client.remoting;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.UpdateSource;
import dev.rico.internal.remoting.communication.commands.BeanCreatedCommand;
import dev.rico.internal.remoting.communication.commands.BeanRemovedCommand;
import dev.rico.internal.remoting.communication.commands.Command;
import dev.rico.internal.remoting.repo.ClassInfo;
import dev.rico.internal.remoting.repo.Repository;

import java.util.function.Consumer;

public class ClientRepository extends Repository {

    public ClientRepository(Consumer<Command> commandHandler) {
        super(commandHandler);
    }

    public void onBeanCreatedCommand(final BeanCreatedCommand command) throws Exception {
        Assert.requireNonNull(command, "command");
        final Class<?> beanClass = Class.forName(command.getBeanType());
        final String id = command.getBeanId();
        final UpdateSource source = UpdateSource.OTHER;
        final ClassInfo classInfo = getOrCreateClassInfo(beanClass);
        createBean(classInfo, id, source);
    }

    public void onBeanRemovedCommand(final BeanRemovedCommand command) throws Exception {
        final String beanId = Assert.requireNonNull(command, "command").getBeanId();
        final Object bean = getBean(beanId);
        deleteBean(bean);
    }
}
