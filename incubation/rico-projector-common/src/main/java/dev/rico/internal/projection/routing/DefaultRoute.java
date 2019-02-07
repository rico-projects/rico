package dev.rico.internal.projection.routing;

import dev.rico.internal.projection.base.AbstractProjectableBean;
import dev.rico.internal.projection.metadata.KeyValue;
import dev.rico.remoting.ObservableList;
import dev.rico.remoting.Property;
import dev.rico.remoting.RemotingBean;

@RemotingBean
public class DefaultRoute extends AbstractProjectableBean implements Route {

    private Property<String> anchor;

    private Property<String> location;

    private ObservableList<KeyValue> parameters;

    @Override
    public Property<String> anchorProperty() {
        return anchor;
    }

    @Override
    public Property<String> locationProperty() {
        return location;
    }

    @Override
    public ObservableList<KeyValue> getParameters() {
        return parameters;
    }
}
