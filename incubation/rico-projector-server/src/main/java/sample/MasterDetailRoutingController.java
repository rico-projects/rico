package sample;

import dev.rico.internal.projection.routing.Route;
import dev.rico.internal.server.projection.routing.AbstractRoutingController;
import dev.rico.remoting.BeanManager;
import dev.rico.server.client.ClientSession;
import dev.rico.server.remoting.RemotingController;
import dev.rico.server.remoting.RemotingModel;
import dev.rico.server.remoting.event.RemotingEventBus;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static sample.MasterDetailServerConstants.ANCHOR;

@RemotingController
public class MasterDetailRoutingController extends AbstractRoutingController {

    @RemotingModel
    private Route model;

    @Inject
    public MasterDetailRoutingController(final ClientSession clientSession, final RemotingEventBus eventBus, final BeanManager beanManager) {
        super(clientSession, eventBus, beanManager);
    }

    @PostConstruct
    public void initRouting() {
        init(ANCHOR, "MasterView");
    }

    @Override
    protected Route getRoute() {
        return model;
    }
}
