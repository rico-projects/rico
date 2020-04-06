package dev.rico.internal.remoting.repo;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.PropertyImpl;
import dev.rico.internal.remoting.RemotingUtils;
import dev.rico.internal.remoting.UpdateSource;
import dev.rico.internal.remoting.collections.ObservableArrayList;
import dev.rico.internal.remoting.communication.commands.Command;
import dev.rico.internal.remoting.communication.commands.impl.ListAddCommand;
import dev.rico.internal.remoting.communication.commands.impl.ListRemoveCommand;
import dev.rico.internal.remoting.communication.commands.impl.ListReplaceCommand;
import dev.rico.internal.remoting.communication.commands.impl.ValueChangedCommand;
import dev.rico.internal.remoting.communication.converters.Converters;
import dev.rico.remoting.ListChangeEvent;
import dev.rico.remoting.ObservableList;
import dev.rico.remoting.Property;
import dev.rico.remoting.ValueChangeEvent;
import dev.rico.remoting.converter.BeanRepo;
import dev.rico.remoting.converter.Converter;
import dev.rico.remoting.converter.ValueConverterException;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Repository implements BeanRepo {

    private final Converters converters;

    private final Consumer<Command> commandHandler;

    private final Map<Class<?>, ClassInfo> classToClassInfoMap = new HashMap<>();

    private final Map<String, ClassInfo> classIdToClassInfoMap = new HashMap<>();

    private final Map<Object, String> beanToIdMap = new IdentityHashMap<>();

    private final Map<String, Object> idToBeanMap = new HashMap<>();

    public Repository(final Consumer<Command> commandHandler) {
        this.commandHandler = Assert.requireNonNull(commandHandler, "commandHandler");
        this.converters = new Converters(this);
    }

    protected ClassInfo getClassInfo(final String classId) {
        return classIdToClassInfoMap.get(classId);
    }

    protected ClassInfo getClassInfo(final Class<?> beanClass) {
        return classToClassInfoMap.get(beanClass);
    }

    protected void addClassInfo(final ClassInfo classInfo) {
        Assert.requireNonNull(classInfo, "classInfo");
        classToClassInfoMap.put(classInfo.getBeanClass(), classInfo);
        classIdToClassInfoMap.put(classInfo.getId(), classInfo);
    }

    protected boolean containsClassInfoForClass(final Class<?> beanClass) {
        return getClassInfo(beanClass) != null;
    }

    protected boolean containsClassInfoForClassId(final String classId) {
        return getClassInfo(classId) != null;
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

    public void onListAddCommand(final ListAddCommand command) throws ValueConverterException {
        final String beanId = command.getBeanId();
        final Object bean = getBean(beanId);
        final ClassInfo classInfo = getClassInfo(bean.getClass());
        final String listName = command.getListName();
        final PropertyInfo listInfo = classInfo.getObservableListInfo(listName);
        final ObservableArrayList list = (ObservableArrayList) listInfo.getPrivileged(bean);

        final List<Object> newElements = command.getValues()
                .stream()
                .map(v -> {
                    try {
                        return listInfo.convertFromRemoting(v);
                    } catch (ValueConverterException e) {
                       throw new RuntimeException("TODO", e);
                    }
                })
                .collect(Collectors.toList());

        list.internalAddAll(command.getStart(), newElements);
    }

    public void onListRemoveCommand(final ListRemoveCommand command) throws ValueConverterException {
        final String beanId = command.getBeanId();
        final Object bean = getBean(beanId);
        final ClassInfo classInfo = getClassInfo(bean.getClass());
        final String listName = command.getListName();
        final PropertyInfo listInfo = classInfo.getObservableListInfo(listName);
        final ObservableArrayList list = (ObservableArrayList) listInfo.getPrivileged(bean);
        list.internalRemove(command.getFrom(), command.getTo());
    }

    public void onListReplaceCommand(final ListReplaceCommand command) throws ValueConverterException {
        final String beanId = command.getBeanId();
        final Object bean = getBean(beanId);
        final ClassInfo classInfo = getClassInfo(bean.getClass());
        final String listName = command.getListName();
        final PropertyInfo listInfo = classInfo.getObservableListInfo(listName);
        final ObservableArrayList list = (ObservableArrayList) listInfo.getPrivileged(bean);
        int index = command.getStart();
        for(Object value : command.getValues()) {
            list.internalSet(index, value);
            index++;
        }
    }

    public <T> T createBean(final ClassInfo classInfo, final String id, final UpdateSource source) throws Exception {
        Assert.requireNonNull(classInfo, "classInfo");
        RemotingUtils.assertIsRemotingBean(classInfo.getBeanClass());

        final T bean = (T) classInfo.getBeanClass().newInstance();
        setupProperties(classInfo, bean, id);
        setupObservableLists(classInfo, bean, id);
        idToBeanMap.put(id, bean);
        beanToIdMap.put(bean, id);
        return bean;
    }

    public <T> void deleteBean(T bean) throws Exception {
        RemotingUtils.assertIsRemotingBean(bean);
        String id = beanToIdMap.remove(bean);
        idToBeanMap.remove(id);
    }

    public Object getBean(final String beanId) {
        Assert.requireNonBlank(beanId, "beanId");
        final Object bean = idToBeanMap.get(beanId);
        if (bean == null) {
            throw new IllegalArgumentException("No bean instance found with id " + beanId);
        }
        return bean;
    }

    public String getBeanId(final Object bean) {
        Assert.requireNonNull(bean, "bean");
        RemotingUtils.assertIsRemotingBean(bean);
        final String id = beanToIdMap.get(bean);
        if (id == null) {
            throw new IllegalArgumentException("Only managed remoting beans can be used.");
        }
        return id;
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

    protected PropertyImpl createProperty(final String beanId, final PropertyInfo propertyInfo) {
        Assert.requireNonBlank(beanId, "beanId");
        Assert.requireNonNull(propertyInfo, "propertyInfo");
        PropertyImpl property = new PropertyImpl(e -> processPropertyEvent(propertyInfo, beanId, e));
        return property;
    }

    protected ObservableArrayList createList(final String beanId, final PropertyInfo observableListInfo) {
        final ObservableArrayList list = new ObservableArrayList(e -> processListEvent(observableListInfo, beanId, e));
        return list;
    }

    private void processPropertyEvent(final PropertyInfo propertyInfo, final String beanId, final ValueChangeEvent<?> event) {
        final Object value = event.getNewValue();
        final ValueChangedCommand command = new ValueChangedCommand();
        command.setBeanId(beanId);
        if (value != null) {
            try {
                final Object convertedValue = getConverter(value.getClass()).convertToRemoting(value);
                command.setNewValue(convertedValue);
            } catch (Exception e) {
                throw new RuntimeException("Error in value converter", e);
            }
        }
        command.setPropertyName(propertyInfo.getAttributeName());
        commandHandler.accept(command);
    }

    private void processListEvent(final PropertyInfo observableListInfo, final String beanId, final ListChangeEvent<?> event) {
        for (final ListChangeEvent.Change<?> change : event.getChanges()) {
            if (change.isAdded()) {
                final ListAddCommand command = new ListAddCommand();
                command.setBeanId(beanId);
                command.setListName(observableListInfo.getAttributeName());
                command.setStart(change.getFrom());
                final List<?> newElements = event.getSource().subList(change.getFrom(), change.getTo());
                for (final Object element : newElements) {
                    try {
                        command.getValues().add(observableListInfo.convertToRemoting(element));
                    } catch (Exception e) {
                        throw new RuntimeException("Can not sync list splice", e);
                    }
                }
                commandHandler.accept(command);
            } else if (change.isReplaced()) {
                final ListReplaceCommand command = new ListReplaceCommand();
                command.setBeanId(beanId);
                command.setListName(observableListInfo.getAttributeName());
                command.setStart(change.getFrom());
                final List<?> newElements = event.getSource().subList(change.getFrom(), change.getTo());
                for (final Object element : newElements) {
                    try {
                        command.getValues().add(observableListInfo.convertToRemoting(element));
                    } catch (Exception e) {
                        throw new RuntimeException("Can not sync list splice", e);
                    }
                }
                commandHandler.accept(command);
            } else if (change.isRemoved()) {
                final ListRemoveCommand command = new ListRemoveCommand();
                command.setBeanId(beanId);
                command.setListName(observableListInfo.getAttributeName());
                command.setFrom(change.getFrom());
                command.setTo(change.getRemovedElements().size());
                commandHandler.accept(command);
            }
        }
    }

    protected Consumer<Command> getCommandHandler() {
        return commandHandler;
    }


    public Converter getConverter(final Class<?> clazz) {
        return converters.getConverter(clazz);
    }

    public Converters getConverters() {
        return converters;
    }

    public void clear() {
        classToClassInfoMap.clear();
        classIdToClassInfoMap.clear();
        beanToIdMap.clear();
        idToBeanMap.clear();
    }
}
