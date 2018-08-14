package dev.rico.internal.remoting.communication.commands.impl;

import dev.rico.internal.remoting.communication.commands.AbstractCommand;

public class ListRemoveCommand extends AbstractCommand {

    private String beanId;

    private String listName;

    private int from;

    private int to;

    public ListRemoveCommand() {
    }

    public ListRemoveCommand(String uniqueIdentifier) {
        super(uniqueIdentifier);
    }

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }
}
