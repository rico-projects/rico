package dev.rico.internal.server.remoting.model;

import dev.rico.internal.remoting.PropertyImpl;
import dev.rico.internal.remoting.UpdateSource;
import dev.rico.internal.remoting.collections.ObservableArrayList;
import dev.rico.internal.remoting.communication.commands.Command;
import dev.rico.internal.remoting.communication.commands.impl.CreateBeanCommand;
import dev.rico.internal.remoting.communication.commands.impl.CreateBeanTypeCommand;
import dev.rico.internal.remoting.communication.commands.impl.DeleteBeanCommand;
import dev.rico.internal.remoting.repo.ClassInfo;
import dev.rico.internal.remoting.repo.PropertyInfo;
import dev.rico.internal.remoting.repo.Repository;
import dev.rico.internal.server.remoting.gc.GarbageCollector;

import java.util.UUID;
import java.util.function.Consumer;

public class ServerRepository extends Repository {

    private GarbageCollector garbageCollector;

    public ServerRepository(Consumer<Command> commandHandler) {
        super(commandHandler);
    }

    public <T> T createRootModel(Class<T> beanClass) throws Exception {
        return create(beanClass, true);
    }

    public <T> T createSubModel(Class<T> beanClass) throws Exception {
        return create(beanClass, false);
    }

    private <T> T create(Class<T> beanClass, boolean rootBean) throws Exception {
        if(!containsClassInfoForClass(beanClass)) {
            final ClassInfo newClassInfo = ClassInfo.create(beanClass, getConverters());
            addClassInfo(newClassInfo);
            CreateBeanTypeCommand command = new CreateBeanTypeCommand();
            //TODO: fill command based on class info...
            getCommandHandler().accept(command);
        }
        final String instanceId = UUID.randomUUID().toString();
        final ClassInfo classInfo = getClassInfo(beanClass);
        final T bean = createBean(classInfo, instanceId, UpdateSource.SELF);
        garbageCollector.onBeanCreated(bean, rootBean);
        CreateBeanCommand command = new CreateBeanCommand();
        command.setBeanId(instanceId);
        command.setClassId(bean.getClass().toString());
        getCommandHandler().accept(command);
        return bean;
    }

    @Override
    protected ObservableArrayList createList(String beanId, PropertyInfo observableListInfo) {
        final ObservableArrayList<?> list = super.createList(beanId, observableListInfo);
        list.onChanged(e -> {
            e.getChanges().forEach(c -> {
                if(c.isAdded()) {
                    list.subList(c.getFrom(), c.getTo()).forEach(i -> garbageCollector.onAddedToList(list, i));
                }
                if(c.isRemoved()) {
                    c.getRemovedElements().forEach(i -> garbageCollector.onRemovedFromList(list, i));
                }
                if(c.isReplaced()) {
                    //??? TODO
                }
            });
        });
        return list;
    }

    @Override
    protected PropertyImpl createProperty(String beanId, PropertyInfo propertyInfo) {
        final PropertyImpl property = super.createProperty(beanId, propertyInfo);
        property.onChanged(e -> garbageCollector.onPropertyValueChanged(e.getSource(), e.getOldValue(), e.getNewValue()));
        return property;
    }

    @Override
    public <T> void deleteBean(T bean) throws Exception {
        final String beanId = getBeanId(bean);
        super.deleteBean(bean);

        DeleteBeanCommand command = new DeleteBeanCommand();
        command.setBeanId(beanId);
        getCommandHandler().accept(command);
    }
}