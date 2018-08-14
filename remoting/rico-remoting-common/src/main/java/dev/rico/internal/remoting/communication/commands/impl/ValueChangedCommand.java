package dev.rico.internal.remoting.communication.commands.impl;

import dev.rico.internal.remoting.communication.commands.AbstractCommand;

public final class ValueChangedCommand extends AbstractCommand {

    private String beanId;

    private String propertyName;

    private Object newValue;

    public ValueChangedCommand() {
    }

    public ValueChangedCommand(final String uniqueIdentifier) {
        super(uniqueIdentifier);
    }

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }
}
