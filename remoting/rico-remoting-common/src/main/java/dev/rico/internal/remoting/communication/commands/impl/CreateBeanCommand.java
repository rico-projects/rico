package dev.rico.internal.remoting.communication.commands.impl;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.communication.commands.AbstractCommand;

public class CreateBeanCommand extends AbstractCommand {

    private String beanId;

    private String classId;

    public CreateBeanCommand() {
    }

    public CreateBeanCommand(final String uniqueIdentifier) {
        super(uniqueIdentifier);
    }

    public String getBeanId() {
        return beanId;
    }

    public String getClassId() {
        return classId;
    }

    public void setBeanId(String beanId) {
        this.beanId = Assert.requireNonBlank(beanId, "beanId");
    }

    public void setClassId(String classId) {
        this.classId = Assert.requireNonBlank(classId, "classId");
    }
}
