package dev.rico.internal.projection.routing;

import dev.rico.internal.projection.base.Projectable;
import dev.rico.remoting.Property;
import dev.rico.remoting.RemotingBean;

@RemotingBean
public interface Route extends Projectable {

    Property<String> anchorProperty();

    Property<String> locationProperty();

    default String getAnchor() {
        return anchorProperty().get();
    }

    default void setAnchor(String anchor) {
        anchorProperty().set(anchor);
    }

    default String getLocation() {
        return locationProperty().get();
    }

    default void setLocation(String location) {
        locationProperty().set(location);
    }

}
