package dev.rico.integrationtests.modelhierarchy;

import dev.rico.remoting.ObservableList;
import dev.rico.remoting.Property;
import dev.rico.remoting.RemotingBean;

@RemotingBean
public class RootModel {

    private Property<ChildModel> childA;

    private Property<ChildModel> childB;

    private Property<Integer> childAChanged;

    private Property<Integer> childBChanged;

    private ObservableList<ChildModel> list;

    public Property<ChildModel> childAProperty() {
        return childA;
    }

    public Property<ChildModel> childBProperty() {
        return childB;
    }

    public ObservableList<ChildModel> getList() {
        return list;
    }

    public Property<Integer> childAChangedProperty() {
        return childAChanged;
    }

    public Property<Integer> childBChangedProperty() {
        return childBChanged;
    }
}
