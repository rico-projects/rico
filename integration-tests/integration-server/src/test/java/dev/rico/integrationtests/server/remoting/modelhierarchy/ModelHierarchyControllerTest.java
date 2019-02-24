package dev.rico.integrationtests.server.remoting.modelhierarchy;

import dev.rico.integrationtests.remoting.modelhierarchy.RootModel;
import dev.rico.integrationtests.server.TestConfiguration;
import dev.rico.server.remoting.test.ControllerUnderTest;
import dev.rico.server.remoting.test.SpringTestNGControllerTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static dev.rico.integrationtests.remoting.modelhierarchy.ModelHierarchyConstants.ADD_COUNTER_LISTENER_ACTION;
import static dev.rico.integrationtests.remoting.modelhierarchy.ModelHierarchyConstants.A_TO_RANDOM_ACTION;
import static dev.rico.integrationtests.remoting.modelhierarchy.ModelHierarchyConstants.B_TO_RANDOM_ACTION;
import static dev.rico.integrationtests.remoting.modelhierarchy.ModelHierarchyConstants.CONTROLLER_NAME;
import static dev.rico.integrationtests.remoting.modelhierarchy.ModelHierarchyConstants.SWITCH_CHILDREN_ACTION;

@SpringBootTest(classes = TestConfiguration.class)
public class ModelHierarchyControllerTest extends SpringTestNGControllerTest {

    private ControllerUnderTest<RootModel> controller;

    @BeforeMethod
    public void init() {
        controller = createController(CONTROLLER_NAME);
    }

    @AfterMethod
    public void destroy() {
        controller.destroy();
    }

    @Test
    public void testSetChildModels() {
        controller.invoke(A_TO_RANDOM_ACTION);
        controller.invoke(B_TO_RANDOM_ACTION);

        Assert.assertNotNull(controller.getModel().childAProperty().get());
        Assert.assertNotNull(controller.getModel().childAProperty().get().nameProperty().get());
        Assert.assertNotNull(controller.getModel().childBProperty().get());
        Assert.assertNotNull(controller.getModel().childBProperty().get().nameProperty().get());
    }

    @Test
    public void testSetChildModelsWithCounter() {
        controller.invoke(ADD_COUNTER_LISTENER_ACTION);

        controller.invoke(A_TO_RANDOM_ACTION);
        controller.invoke(B_TO_RANDOM_ACTION);

        Assert.assertNotNull(controller.getModel().childAProperty().get());
        Assert.assertNotNull(controller.getModel().childAProperty().get().nameProperty().get());
        Assert.assertNotNull(controller.getModel().childBProperty().get());
        Assert.assertNotNull(controller.getModel().childBProperty().get().nameProperty().get());

        Assert.assertEquals(controller.getModel().childAChangedProperty().get().intValue(), 1);
        Assert.assertEquals(controller.getModel().childBChangedProperty().get().intValue(), 1);
    }

    @Test
    public void testSwitchChildModels() {
        controller.invoke(A_TO_RANDOM_ACTION);
        controller.invoke(B_TO_RANDOM_ACTION);

        final String nameA = controller.getModel().childAProperty().get().nameProperty().get();
        final String nameB = controller.getModel().childBProperty().get().nameProperty().get();

        controller.invoke(SWITCH_CHILDREN_ACTION);

        Assert.assertEquals(controller.getModel().childAProperty().get().nameProperty().get(), nameB);
        Assert.assertEquals(controller.getModel().childBProperty().get().nameProperty().get(), nameA);

    }

    @Test
    public void testSwitchChildModelsWithCounter() {
        controller.invoke(ADD_COUNTER_LISTENER_ACTION);

        controller.invoke(A_TO_RANDOM_ACTION);
        controller.invoke(B_TO_RANDOM_ACTION);

        final String nameA = controller.getModel().childAProperty().get().nameProperty().get();
        final String nameB = controller.getModel().childBProperty().get().nameProperty().get();

        controller.invoke(SWITCH_CHILDREN_ACTION);

        Assert.assertEquals(controller.getModel().childAProperty().get().nameProperty().get(), nameB);
        Assert.assertEquals(controller.getModel().childBProperty().get().nameProperty().get(), nameA);

        Assert.assertEquals(controller.getModel().childAChangedProperty().get().intValue(), 2);
        Assert.assertEquals(controller.getModel().childBChangedProperty().get().intValue(), 2);
    }

    @Test
    public void testSetChildMultipleTimes() {
        controller.invoke(A_TO_RANDOM_ACTION);
        final String nameA = controller.getModel().childAProperty().get().nameProperty().get();
        controller.invoke(A_TO_RANDOM_ACTION);
        Assert.assertNotEquals(controller.getModel().childAProperty().get().nameProperty().get(), nameA);
    }

    @Test
    public void testSetChildMultipleTimesWithCounter() {
        controller.invoke(ADD_COUNTER_LISTENER_ACTION);

        controller.invoke(A_TO_RANDOM_ACTION);
        final String nameA = controller.getModel().childAProperty().get().nameProperty().get();
        controller.invoke(A_TO_RANDOM_ACTION);
        Assert.assertNotEquals(controller.getModel().childAProperty().get().nameProperty().get(), nameA);

        Assert.assertEquals(controller.getModel().childAChangedProperty().get().intValue(), 2);
        Assert.assertEquals(controller.getModel().childBChangedProperty().get().intValue(), 0);
    }

}
