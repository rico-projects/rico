package dev.rico.internal.remoting.communication.commands.impl;

import dev.rico.internal.remoting.communication.commands.AbstractCommand;

import java.util.ArrayList;
import java.util.List;

public class ListReplaceCommand extends AbstractCommand {

    private String beanId;

    private String listName;

    private int start;

    private final List<Object> values = new ArrayList<>();

    public ListReplaceCommand() {
    }

    public ListReplaceCommand(String uniqueIdentifier) {
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

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public List<Object> getValues() {
        return values;
    }
}
