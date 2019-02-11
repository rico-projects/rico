package dev.rico.internal.server.projection.routing;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public class RoutingEvent implements Serializable {

    private String anchor;

    private String location;

    private Map<String, Serializable> parameters;

    public RoutingEvent(final String anchor, final String location) {
        this(anchor, location, Collections.emptyMap());
    }

    public RoutingEvent(final String anchor, final String location, final Map<String, Serializable> parameters) {
        this.anchor = anchor;
        this.location = location;
        this.parameters = Collections.unmodifiableMap(parameters);
    }

    public String getAnchor() {
        return anchor;
    }

    public String getLocation() {
        return location;
    }

    public Map<String, Serializable> getParameters() {
        return parameters;
    }
}
