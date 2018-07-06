package dev.rico.internal.remoting.communication.commands.impl;

import dev.rico.internal.remoting.communication.commands.AbstractCommand;

public class CreateBeanCommand extends AbstractCommand {

    private String beanId;

    private String classId;

    public String getBeanId() {
        return beanId;
    }

    public String getClassId() {
        return classId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    //TODO

}
