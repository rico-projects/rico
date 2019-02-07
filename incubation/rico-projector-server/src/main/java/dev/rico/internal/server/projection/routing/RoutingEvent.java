package dev.rico.internal.server.projection.routing;

import java.io.Serializable;

public class RoutingEvent implements Serializable {

    private String anchor;

    private String location;

    public RoutingEvent(final String anchor, final String location) {
        this.anchor = anchor;
        this.location = location;
    }

    public String getAnchor() {
        return anchor;
    }

    public String getLocation() {
        return location;
    }
}
