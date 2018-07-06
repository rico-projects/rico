package dev.rico.internal.remoting.communication.commands.impl;

import dev.rico.internal.remoting.communication.commands.AbstractCommand;

import java.util.ArrayList;
import java.util.List;

public final class ListSpliceCommand extends AbstractCommand {

    private String beanId;

    private String listName;

    private int from;

    private int to;

    private int count;

    private final List<Object> values;

    public ListSpliceCommand() {
        values = new ArrayList<>();
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Object> getValues() {
        return values;
    }
}
