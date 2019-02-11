package sample;

import dev.rico.internal.core.Assert;
import dev.rico.internal.server.projection.routing.RouteAnchor;
import dev.rico.internal.server.projection.routing.Routing;
import dev.rico.server.remoting.RemotingAction;
import dev.rico.server.remoting.RemotingController;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@RemotingController
public class MasterController {

    @RouteAnchor
    private String anchor;

    private final Routing routing;

    private final ItemService itemService;

    public MasterController(final Routing routing, final ItemService itemService) {
        this.routing = Assert.requireNonNull(routing, "routing");
        this.itemService = Assert.requireNonNull(itemService, "itemService");
    }

    @RemotingAction
    public void showDetails(final String id) {
        final Map<String, Serializable> params = new HashMap<>();
        params.put("id", id);
        routing.route(anchor, "DetailsView", params);
    }

}
