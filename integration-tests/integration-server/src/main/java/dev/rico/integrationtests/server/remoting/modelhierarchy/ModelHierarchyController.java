package dev.rico.integrationtests.server.remoting.modelhierarchy;

import dev.rico.integrationtests.remoting.modelhierarchy.ChildModel;
import dev.rico.integrationtests.remoting.modelhierarchy.RootModel;
import dev.rico.remoting.BeanManager;
import dev.rico.server.remoting.RemotingAction;
import dev.rico.server.remoting.RemotingController;
import dev.rico.server.remoting.RemotingModel;

import javax.inject.Inject;
import java.util.UUID;

import static dev.rico.integrationtests.remoting.modelhierarchy.ModelHierarchyConstants.ADD_COUNTER_LISTENER_ACTION;
import static dev.rico.integrationtests.remoting.modelhierarchy.ModelHierarchyConstants.A_TO_RANDOM_ACTION;
import static dev.rico.integrationtests.remoting.modelhierarchy.ModelHierarchyConstants.B_TO_RANDOM_ACTION;
import static dev.rico.integrationtests.remoting.modelhierarchy.ModelHierarchyConstants.CONTROLLER_NAME;
import static dev.rico.integrationtests.remoting.modelhierarchy.ModelHierarchyConstants.SWITCH_CHILDREN_ACTION;

@RemotingController(CONTROLLER_NAME)
public class ModelHierarchyController {

    @RemotingModel
    private RootModel model;

    @Inject
    private BeanManager beanManager;

    @RemotingAction(ADD_COUNTER_LISTENER_ACTION)
    public void addCounterListener() {
        model.childAChangedProperty().set(0);
        model.childBChangedProperty().set(0);
        model.childAProperty().onChanged(e -> model.childAChangedProperty().set(model.childAChangedProperty().get() + 1));
        model.childBProperty().onChanged(e -> model.childBChangedProperty().set(model.childBChangedProperty().get() + 1));
    }

    @RemotingAction(SWITCH_CHILDREN_ACTION)
    public void switchChildren() {
        final ChildModel modelA = model.childAProperty().get();
        final ChildModel modelB = model.childBProperty().get();

        model.childAProperty().set(modelB);
        model.childBProperty().set(modelA);
    }

    @RemotingAction(A_TO_RANDOM_ACTION)
    public void setAToRandom() {
        final ChildModel modelA = beanManager.create(ChildModel.class);
        modelA.nameProperty().set(UUID.randomUUID().toString());
        model.childAProperty().set(modelA);
    }

    @RemotingAction(B_TO_RANDOM_ACTION)
    public void setBToRandom() {
        final ChildModel modelB = beanManager.create(ChildModel.class);
        modelB.nameProperty().set(UUID.randomUUID().toString());
        model.childBProperty().set(modelB);
    }

}
