package dev.rico.internal.remoting.communication.handler;

import dev.rico.internal.remoting.collections.ObservableArrayList;
import dev.rico.internal.remoting.communication.commands.ListSpliceCommand;
import dev.rico.internal.remoting.repo.ClassInfo;
import dev.rico.internal.remoting.repo.PropertyInfo;

import java.util.ArrayList;
import java.util.List;

public class OnListSpliceCommand implements CommandHandler<ListSpliceCommand> {

    @Override
    public void onCommand(ListSpliceCommand command) throws Exception {
        final String beanId = command.getBeanId();
        final Object bean = null; //TODO
        final ClassInfo classInfo = null; //TODO
        final PropertyInfo observableListInfo = classInfo.getPropertyInfo(command.getListName());
        final ObservableArrayList list = (ObservableArrayList) observableListInfo.getPrivileged(bean);

        final int from = command.getFrom();
        final int to = command.getTo();
        final int count = command.getCount();

        final List<Object> newElements = new ArrayList<Object>(count);
        for (int i = 0; i < count; i++) {
            final Object remotingValue = command.getValues().get(i);
            final Object value = observableListInfo.convertFromRemoting(remotingValue);
            newElements.add(value);
        }

        list.internalSplice(from, to, newElements);
    }
}
