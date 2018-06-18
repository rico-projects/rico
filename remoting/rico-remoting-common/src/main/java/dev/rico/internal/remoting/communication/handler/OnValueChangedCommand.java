package dev.rico.internal.remoting.communication.handler;

import dev.rico.internal.remoting.PropertyImpl;
import dev.rico.internal.remoting.communication.commands.ValueChangedCommand;
import dev.rico.internal.remoting.repo.BeanRepository;
import dev.rico.internal.remoting.repo.ClassInfo;
import dev.rico.internal.remoting.repo.PropertyInfo;

public class OnValueChangedCommand implements CommandHandler<ValueChangedCommand> {

    private final BeanRepository beanRepository;

    public OnValueChangedCommand(BeanRepository beanRepository) {
        this.beanRepository = beanRepository;
    }

    @Override
    public void onCommand(ValueChangedCommand command) throws Exception {
        final String beanId = command.getBeanId();
        final Object bean = beanRepository.getBean(beanId);
        final ClassInfo classInfo = null; //TODO
        final PropertyInfo propertyInfo = classInfo.getPropertyInfo(command.getPropertyName());
        final PropertyImpl property = (PropertyImpl) propertyInfo.getPrivileged(bean);
        final Object remotingValue = command.getNewValue();
        final Object value = propertyInfo.convertFromRemoting(remotingValue);
        property.internalSet(value);
    }
}
