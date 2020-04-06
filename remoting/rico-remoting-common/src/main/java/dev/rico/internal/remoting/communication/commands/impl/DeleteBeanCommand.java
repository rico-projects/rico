package dev.rico.internal.remoting.communication.commands.impl;

import dev.rico.internal.core.Assert;
import dev.rico.internal.remoting.communication.commands.AbstractCommand;

public class DeleteBeanCommand extends AbstractCommand {

    private String beanId;

    public DeleteBeanCommand() {
    }

    public DeleteBeanCommand(final String uniqueIdentifier) {
        super(uniqueIdentifier);
    }

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(final String beanId) {
        this.beanId = Assert.requireNonBlank(beanId, "beanId");
    }
}

