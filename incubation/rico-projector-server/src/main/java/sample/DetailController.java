package sample;

import dev.rico.internal.core.Assert;
import dev.rico.internal.server.projection.routing.RouteAnchor;
import dev.rico.internal.server.projection.routing.Routing;
import dev.rico.server.remoting.RemotingAction;
import dev.rico.server.remoting.RemotingController;
import dev.rico.server.remoting.RemotingModel;
import dev.rico.server.remoting.RemotingValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Optional;

@RemotingController
public class DetailController {

    @RemotingValue
    private String id;

    @RouteAnchor
    private String anchor;

    @RemotingModel
    private DetailModel model;

    private final Routing routing;

    private final ItemService itemService;

    @Inject
    public DetailController(final Routing routing, final ItemService itemService) {
        this.routing = Assert.requireNonNull(routing, "routing");
        this.itemService = Assert.requireNonNull(itemService, "itemService");
    }

    @PostConstruct
    public void init() {
        Optional.ofNullable(id)
                .ifPresent(i -> showId(i));
    }

    @RemotingAction
    public void save() {
        if(model.idProperty().get() == null) {
            final Item item = new Item();
            item.setName(model.nameProperty().get());
            item.setDescription(model.descriptionProperty().get());
            final Item savedItem = itemService.save(item);
            updateFromItem(savedItem);
        } else {
            final Item item = itemService.find(id)
                    .orElseThrow(() -> new IllegalArgumentException("Can not find item for " + id));
            item.setName(model.nameProperty().get());
            item.setDescription(model.descriptionProperty().get());
            final Item savedItem = itemService.save(item);
            updateFromItem(savedItem);
        }
    }

    @RemotingAction
    public void refresh() {
        showId(model.idProperty().get());
    }

    @RemotingAction
    public void goToMasterView() {
        routing.route(anchor, "MasterView");
    }

    private void updateFromItem(final Item item) {
        Assert.requireNonNull(item, "item");
        model.idProperty().set(item.getId());
        model.nameProperty().set(item.getName());
        model.descriptionProperty().set(item.getDescription());
    }

    private void showId(final String id) {
        Assert.requireNonBlank(id, "id");
        final Item item = itemService.find(id)
                .orElseThrow(() -> new IllegalArgumentException("Can not find item for " + id));
        updateFromItem(item);
    }
}
