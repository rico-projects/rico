package dev.rico.internal.client.remoting;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.communication.handler.CommandHandler;
import dev.rico.internal.remoting.repo.BeanRepository;
import dev.rico.internal.remoting.communication.commands.BeanRemovedCommand;

public class OnBeanRemoved implements CommandHandler<BeanRemovedCommand> {

    private final BeanRepository beanRepository;

    public OnBeanRemoved(final BeanRepository beanRepository) {
        this.beanRepository = Assert.requireNonNull(beanRepository, "beanRepository");
    }

    @Override
    public void onCommand(final BeanRemovedCommand command) throws Exception {
        final String beanId = Assert.requireNonNull(command, "command").getBeanId();
        final Object bean = beanRepository.getBean(beanId);
        beanRepository.delete(bean);
    }
}
