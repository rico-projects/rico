package dev.rico.internal.remoting.collections;

import dev.rico.internal.remoting.communication.commands.ListSpliceCommand;
import dev.rico.internal.remoting.repo.PropertyInfo;
import dev.rico.remoting.ListChangeEvent;

import java.util.List;

public class ListSpliceCommandProvider {

    public void processEvent(final PropertyInfo observableListInfo, final String beanId, final ListChangeEvent<?> event) throws Exception {
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
        }
    }
}
