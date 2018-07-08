package dev.rico.internal.remoting.communication.commands.impl;

import dev.rico.internal.remoting.communication.commands.AbstractCommand;

public class CreateBeanTypeCommand extends AbstractCommand {

    private String className;

    private String beanType;

    public CreateBeanTypeCommand() {
    }

    public CreateBeanTypeCommand(final String uniqueIdentifier) {
        super(uniqueIdentifier);
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getBeanType() {
        return beanType;
    }

    public void setBeanType(String beanType) {
        this.beanType = beanType;
    }

    public String getClassName() {
        return className;
    }

    public String getClassId() {
        return beanType;
    }
}
