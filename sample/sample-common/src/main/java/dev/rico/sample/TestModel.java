package dev.rico.sample;

import dev.rico.remoting.ObservableList;
import dev.rico.remoting.RemotingBean;

@RemotingBean
public class TestModel {

    private ObservableList<Item> items;

    public ObservableList<Item> getItems() {
        return items;
    }
}
