package dev.rico.internal.remoting.communication.commands.impl;

import dev.rico.internal.remoting.communication.commands.AbstractCommand;

public class CreateBeanTypeCommand extends AbstractCommand{
    private String className;
    private String beanType;

    public String getClassName() {
        return className;
    }

    public String getClassId() {
        return beanType;
    }
}
