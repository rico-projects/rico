package dev.rico.sample;

import dev.rico.remoting.ObservableList;
import dev.rico.remoting.Property;
import dev.rico.remoting.RemotingBean;

@RemotingBean
public class TestModel {

    private ObservableList<Item> items;

    private Property<Double> valueA;

    private Property<Double> valueB;

    public ObservableList<Item> getItems() {
        return items;
    }

    public Property<Double> getValueA() {
        return valueA;
    }

    public Property<Double> getValueB() {
        return valueB;
    }
}
