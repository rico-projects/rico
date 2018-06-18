package dev.rico.internal.client.remoting;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.BeanBuilder;
import dev.rico.internal.remoting.UpdateSource;
import dev.rico.internal.remoting.communication.commands.BeanCreatedCommand;
import dev.rico.internal.remoting.communication.handler.CommandHandler;

public class OnBeanCreated implements CommandHandler<BeanCreatedCommand> {

    private final BeanBuilder beanBuilder;

    public OnBeanCreated(final BeanBuilder beanBuilder) {
        this.beanBuilder = Assert.requireNonNull(beanBuilder, "beanBuilder");
    }

    public void onCommand(final BeanCreatedCommand command) throws Exception {
        Assert.requireNonNull(command, "command");
        final Class<?> beanClass = Class.forName(command.getBeanType());
        final String id = command.getBeanId();
        final UpdateSource source = UpdateSource.OTHER;
        beanBuilder.createInstanceForClass(beanClass, id, source);
    }
}
