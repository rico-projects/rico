package dev.rico.internal.remoting.communication.commands;

public final class BeanRemovedCommand implements Command {

    private String beanId;

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }
}
