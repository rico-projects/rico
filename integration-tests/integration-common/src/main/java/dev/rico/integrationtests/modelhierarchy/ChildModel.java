package dev.rico.integrationtests.modelhierarchy;

import dev.rico.remoting.Property;
import dev.rico.remoting.RemotingBean;

@RemotingBean
public class ChildModel {

    private Property<String> name;

    public Property<String> nameProperty() {
        return name;
    }
}
