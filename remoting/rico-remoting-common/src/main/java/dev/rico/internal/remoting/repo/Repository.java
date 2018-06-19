package dev.rico.internal.remoting.repo;

import dev.rico.internal.core.Assert;
import dev.rico.internal.core.ReflectionHelper;
import dev.rico.internal.remoting.PropertyImpl;
import dev.rico.internal.remoting.RemotingUtils;
import dev.rico.internal.remoting.UpdateSource;
import dev.rico.internal.remoting.collections.ObservableArrayList;
import dev.rico.internal.remoting.communication.commands.Command;
import dev.rico.internal.remoting.communication.commands.ListSpliceCommand;
import dev.rico.internal.remoting.communication.commands.ValueChangedCommand;
import dev.rico.internal.remoting.communication.converters.Converters;
import dev.rico.remoting.ListChangeEvent;
import dev.rico.remoting.ObservableList;
import dev.rico.remoting.Property;
import dev.rico.remoting.converter.BeanRepo;
import dev.rico.remoting.converter.Converter;
import dev.rico.remoting.converter.ValueConverterException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

public class Repository implements BeanRepo {

    private final Converters converters;

    private final Consumer<Command> commandHandler;

    private final Map<Class<?>, ClassInfo> classToClassInfoMap = new HashMap<>();

    private final Map<String, ClassInfo> modelTypeToClassInfoMap = new HashMap<>();

    private final Map<Object, String> objectPmToRemotingPm = new IdentityHashMap<>();

    private final Map<String, Object> remotingIdToObjectPm = new HashMap<>();

    public Repository(final Consumer<Command> commandHandler) {
        this.commandHandler = Assert.requireNonNull(commandHandler, "commandHandler");
        this.converters = new Converters(this);
    }

    public <T> T createBean(final ClassInfo classInfo, final String id, final UpdateSource source) throws Exception {
        Assert.requireNonNull(classInfo, "classInfo");
        RemotingUtils.assertIsRemotingBean(classInfo.getBeanClass());

        final T bean = (T) classInfo.getBeanClass().newInstance();
        setupProperties(classInfo, bean, id);
        setupObservableLists(classInfo, bean, id);
        remotingIdToObjectPm.put(id, bean);
        objectPmToRemotingPm.put(bean, id);
        return bean;
    }

    private void setupProperties(final ClassInfo classInfo, final Object bean, final String beanId) {
        Assert.requireNonNull(classInfo, "classInfo");
        classInfo.forEachProperty(propertyInfo -> {
            try {
                Assert.requireNonNull(propertyInfo, "propertyInfo");
                final Property property = createProperty(beanId, propertyInfo);
                propertyInfo.setPriviliged(bean, property);
            } catch (Exception e) {
                throw new RuntimeException("Can not createList property " + propertyInfo.getAttributeName(), e);
            }
        });
    }

    private void setupObservableLists(final ClassInfo classInfo, final Object bean, final String beanId) {
        Assert.requireNonNull(classInfo, "classInfo");
        classInfo.forEachObservableList(observableListInfo -> {
            try {
                Assert.requireNonNull(observableListInfo, "observableListInfo");
                final ObservableList observableList = createList(beanId, observableListInfo);
                observableListInfo.setPriviliged(bean, observableList);
            } catch (Exception e) {
                throw new RuntimeException("Can not createList observable list " + observableListInfo.getAttributeName(), e);
            }
        });
    }

    protected ObservableArrayList createList(final String beanId, final PropertyInfo observableListInfo) {
        ObservableArrayList list = new ObservableArrayList();
        //TODO: Define Listener to create & send sync commands
        return list;
    }

    private void processListEvent(final PropertyInfo observableListInfo, final String beanId, final ListChangeEvent<?> event) throws Exception {
        for (final ListChangeEvent.Change<?> change : event.getChanges()) {
            final ListSpliceCommand command = new ListSpliceCommand();
            command.setBeanId(beanId);
            command.setListName(observableListInfo.getAttributeName());
            command.setFrom(change.getFrom());
            command.setTo(change.getFrom() + change.getRemovedElements().size());
            final List<?> newElements = event.getSource().subList(change.getFrom(), change.getTo());
            command.setCount(newElements.size());

            for (final Object element : newElements) {
                command.getValues().add(observableListInfo.convertToRemoting(element));
            }
            commandHandler.accept(command);
        }
    }

    protected PropertyImpl createProperty(final String beanId, final PropertyInfo propertyInfo) {
        Assert.requireNonBlank(beanId, "beanId");
        Assert.requireNonNull(propertyInfo, "propertyInfo");
        PropertyImpl property = new PropertyImpl() {
            @Override
            public void set(Object value) {
                super.set(value);

                final ValueChangedCommand changedCommand = new ValueChangedCommand();
                changedCommand.setBeanId(beanId);
                changedCommand.setNewValue(value);
                changedCommand.setPropertyName(propertyInfo.getAttributeName());
                commandHandler.accept(changedCommand);
            }
        };
        return property;
    }

    public <T> void deleteBean(T bean) {
        RemotingUtils.assertIsRemotingBean(bean);
        String id = objectPmToRemotingPm.remove(bean);
        remotingIdToObjectPm.remove(id);
    }

    public Object getBean(String beanId) {
        if(beanId == null) {
            return null;
        }
        if(!remotingIdToObjectPm.containsKey(beanId)) {
            throw new IllegalArgumentException("No bean instance found with id " + beanId);
        }
        return remotingIdToObjectPm.get(beanId);
    }

    public String getBeanId(Object bean) {
        if (bean == null) {
            return null;
        }
        RemotingUtils.assertIsRemotingBean(bean);
        try {
            return objectPmToRemotingPm.get(bean);
        } catch (NullPointerException ex) {
            throw new IllegalArgumentException("Only managed remoting beans can be used.", ex);
        }
    }

    public ClassInfo getClassInfo(final String modelType) {
        return modelTypeToClassInfoMap.get(modelType);
    }

    public ClassInfo getClassInfo(final Class<?> beanClass) {
        return classToClassInfoMap.get(beanClass);
    }

    public ClassInfo getOrCreateClassInfo(final Class<?> beanClass) {
        final ClassInfo existingClassInfo = classToClassInfoMap.get(beanClass);
        if (existingClassInfo != null) {
            return existingClassInfo;
        }
        final ClassInfo classInfo = createClassInfoForClass(beanClass);
        Assert.requireNonNull(classInfo, "classInfo");
        classToClassInfoMap.put(beanClass, classInfo);

        return classToClassInfoMap.get(beanClass);
    }

    private ClassInfo createClassInfoForClass(final Class<?> beanClass) {
        final List<PropertyInfo> propertyInfos = new ArrayList<>();
        final List<PropertyInfo> observableListInfos = new ArrayList<>();

        for (Field field : ReflectionHelper.getInheritedDeclaredFields(beanClass)) {
            PropertyType type = null;
            if (Property.class.isAssignableFrom(field.getType())) {
                type = PropertyType.PROPERTY;
            } else if (ObservableList.class.isAssignableFrom(field.getType())) {
                type = PropertyType.OBSERVABLE_LIST;
            }
            final Class<?> parameterType = ReflectionHelper.getTypeParameter(field);
            if (type != null && parameterType != null) {
                final Converter converter = converters.getConverter(parameterType);
                final PropertyInfo propertyInfo = new PropertyInfo(converter, field);
                if (type == PropertyType.PROPERTY) {
                    propertyInfos.add(propertyInfo);
                } else {
                    observableListInfos.add(propertyInfo);
                }
            }
        }
        return new ClassInfo(beanClass, propertyInfos, observableListInfos);
    }

    public void onValueChangedCommand(final ValueChangedCommand changedCommand) throws ValueConverterException {
        final String beanId = changedCommand.getBeanId();
        final Object bean = getBean(beanId);
        final ClassInfo classInfo = getClassInfo(bean.getClass());
        final String propertyName = changedCommand.getPropertyName();
        final PropertyInfo propertyInfo = classInfo.getPropertyInfo(propertyName);
        final Object newValueRaw = changedCommand.getNewValue();
        final Object newValue = propertyInfo.convertFromRemoting(newValueRaw);
        final PropertyImpl property = (PropertyImpl) propertyInfo.getPrivileged(bean);
        property.internalSet(newValue);
    }

    public void onListSpliceCommand(final ListSpliceCommand listSpliceCommand) throws ValueConverterException {
        final String beanId = listSpliceCommand.getBeanId();
        final Object bean = getBean(beanId);
        final ClassInfo classInfo = getClassInfo(bean.getClass());
        final String listName = listSpliceCommand.getListName();
        final PropertyInfo listInfo = classInfo.getObservableListInfo(listName);
        final ObservableArrayList list = (ObservableArrayList) listInfo.getPrivileged(bean);

        final int from = listSpliceCommand.getFrom();
        final int to = listSpliceCommand.getTo();
        final int count = listSpliceCommand.getCount();

        final List<Object> newElements = new ArrayList<Object>(count);
        for (int i = 0; i < count; i++) {
            final Object remotingValue = listSpliceCommand.getValues().get(i);
            final Object value = listInfo.convertFromRemoting(remotingValue);
            newElements.add(value);
        }

        list.internalSplice(from, to, newElements);
    }

    protected Consumer<Command> getCommandHandler() {
        return commandHandler;
    }
}
