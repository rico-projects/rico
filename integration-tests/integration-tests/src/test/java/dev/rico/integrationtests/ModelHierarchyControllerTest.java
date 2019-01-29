package dev.rico.integrationtests;

import dev.rico.client.remoting.ClientContext;
import dev.rico.client.remoting.ControllerProxy;
import dev.rico.integrationtests.modelhierarchy.RootModel;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static dev.rico.integrationtests.modelhierarchy.ModelHierarchyConstants.ADD_COUNTER_LISTENER_ACTION;
import static dev.rico.integrationtests.modelhierarchy.ModelHierarchyConstants.A_TO_RANDOM_ACTION;
import static dev.rico.integrationtests.modelhierarchy.ModelHierarchyConstants.B_TO_RANDOM_ACTION;
import static dev.rico.integrationtests.modelhierarchy.ModelHierarchyConstants.CONTROLLER_NAME;
import static dev.rico.integrationtests.modelhierarchy.ModelHierarchyConstants.SWITCH_CHILDREN_ACTION;

public class ModelHierarchyControllerTest extends AbstractIntegrationTest {

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER)
    public void testSetChildModels(final String containerType, final String endpoint) {
        try {
            final ClientContext context = connect(endpoint);
            final ControllerProxy<RootModel> controller = createController(context, CONTROLLER_NAME);
            controller.invoke(A_TO_RANDOM_ACTION).get();
            controller.invoke(B_TO_RANDOM_ACTION).get();

            Assert.assertNotNull(controller.getModel().childAProperty().get());
            Assert.assertNotNull(controller.getModel().childAProperty().get().nameProperty().get());
            Assert.assertNotNull(controller.getModel().childBProperty().get());
            Assert.assertNotNull(controller.getModel().childBProperty().get().nameProperty().get());

            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER)
    public void testSetChildModelsWithCounter(final String containerType, final String endpoint) {
        try {
            final ClientContext context = connect(endpoint);
            final ControllerProxy<RootModel> controller = createController(context, CONTROLLER_NAME);
            controller.invoke(ADD_COUNTER_LISTENER_ACTION).get();
            controller.invoke(A_TO_RANDOM_ACTION).get();
            controller.invoke(B_TO_RANDOM_ACTION).get();

            Assert.assertNotNull(controller.getModel().childAProperty().get());
            Assert.assertNotNull(controller.getModel().childAProperty().get().nameProperty().get());
            Assert.assertNotNull(controller.getModel().childBProperty().get());
            Assert.assertNotNull(controller.getModel().childBProperty().get().nameProperty().get());
            Assert.assertEquals(controller.getModel().childAChangedProperty().get().intValue(), 1);
            Assert.assertEquals(controller.getModel().childBChangedProperty().get().intValue(), 1);

            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER)
    public void testSwitchChildModels(final String containerType, final String endpoint) {
        try {
            final ClientContext context = connect(endpoint);
            ControllerProxy<RootModel> controller = createController(context, CONTROLLER_NAME);
            controller.invoke(A_TO_RANDOM_ACTION).get();
            controller.invoke(B_TO_RANDOM_ACTION).get();

            final String nameA = controller.getModel().childAProperty().get().nameProperty().get();
            final String nameB = controller.getModel().childBProperty().get().nameProperty().get();

            controller.invoke(SWITCH_CHILDREN_ACTION).get();

            Assert.assertEquals(controller.getModel().childAProperty().get().nameProperty().get(), nameB);
            Assert.assertEquals(controller.getModel().childBProperty().get().nameProperty().get(), nameA);

            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER)
    public void testSwitchChildModelsWithCounter(final String containerType, final String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<RootModel> controller = createController(context, CONTROLLER_NAME);
            controller.invoke(ADD_COUNTER_LISTENER_ACTION).get();

            controller.invoke(A_TO_RANDOM_ACTION).get();
            controller.invoke(B_TO_RANDOM_ACTION).get();

            final String nameA = controller.getModel().childAProperty().get().nameProperty().get();
            final String nameB = controller.getModel().childBProperty().get().nameProperty().get();

            controller.invoke(SWITCH_CHILDREN_ACTION).get();

            Assert.assertEquals(controller.getModel().childAProperty().get().nameProperty().get(), nameB);
            Assert.assertEquals(controller.getModel().childBProperty().get().nameProperty().get(), nameA);
            Assert.assertEquals(controller.getModel().childAChangedProperty().get().intValue(), 2);
            Assert.assertEquals(controller.getModel().childBChangedProperty().get().intValue(), 2);

            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER)
    public void testSetChildMultipleTimes(final String containerType, final String endpoint) {
        try {
            final ClientContext context = connect(endpoint);
            final ControllerProxy<RootModel> controller = createController(context, CONTROLLER_NAME);

            final AtomicInteger counter = new AtomicInteger(0);
            controller.getModel().childAProperty().onChanged(e -> counter.addAndGet(1));

            controller.invoke(A_TO_RANDOM_ACTION).get();
            final String nameA = controller.getModel().childAProperty().get().nameProperty().get();

            controller.invoke(A_TO_RANDOM_ACTION).get();
            Assert.assertNotEquals(controller.getModel().childAProperty().get().nameProperty().get(), nameA);

            Assert.assertEquals(counter.get(), 2);

            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER)
    public void testSetChildMultipleTimesWithCounter(final String containerType, final String endpoint) {
        try {
            final ClientContext context = connect(endpoint);
            final ControllerProxy<RootModel> controller = createController(context, CONTROLLER_NAME);
            controller.invoke(ADD_COUNTER_LISTENER_ACTION).get();


            final AtomicInteger counter = new AtomicInteger(0);
            controller.getModel().childAProperty().onChanged(e -> counter.addAndGet(1));

            controller.invoke(A_TO_RANDOM_ACTION).get();
            final String nameA = controller.getModel().childAProperty().get().nameProperty().get();

            controller.invoke(A_TO_RANDOM_ACTION).get();
            Assert.assertNotEquals(controller.getModel().childAProperty().get().nameProperty().get(), nameA);

            Assert.assertEquals(counter.get(), 2);

            Assert.assertEquals(controller.getModel().childAChangedProperty().get().intValue(), 2);
            Assert.assertEquals(controller.getModel().childBChangedProperty().get().intValue(), 0);

            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }
}
