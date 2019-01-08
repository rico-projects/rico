/*
 * Copyright 2018 Karakun AG.
 * Copyright 2015-2018 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.rico.integrationtests.remoting;

import dev.rico.integrationtests.AbstractIntegrationTest;
import dev.rico.integrationtests.remoting.parentchild.ChildTestBean;
import dev.rico.integrationtests.remoting.parentchild.ParentTestBean;
import dev.rico.remoting.Property;
import dev.rico.client.remoting.ClientContext;
import dev.rico.client.remoting.ControllerProxy;
import org.testng.Assert;
import org.testng.annotations.Test;

import static dev.rico.integrationtests.remoting.parentchild.ParentChildTestConstants.CHILD_CONTROLLER_NAME;
import static dev.rico.integrationtests.remoting.parentchild.ParentChildTestConstants.DUMMY_CHILD_CONTROLLER_NAME;
import static dev.rico.integrationtests.remoting.parentchild.ParentChildTestConstants.PARENT_CONTROLLER_NAME;


public class ParentChildControllerTest extends AbstractRemotingIntegrationTest {

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if controller and model can be created")
    public void testCreateController(final String containerType, final String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<ParentTestBean> controller = createController(context, PARENT_CONTROLLER_NAME);
            ControllerProxy<?> childController = controller.createController(CHILD_CONTROLLER_NAME).get();
            Assert.assertNotNull(controller);
            Assert.assertNotNull(controller.getModel());
            Assert.assertEquals(controller.getModel().getClass(), ParentTestBean.class);
            Assert.assertEquals(childController.getModel().getClass(), ChildTestBean.class);
            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if @PostChildCreated is called in controller")
    public void testPostChildCreatedCalledWhenChildControllerCreated(final String containerType, final String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<ParentTestBean> controller = createController(context, PARENT_CONTROLLER_NAME);
            Assert.assertTrue(controller.getModel().postCreatedCalledProperty().get());
            Property<Boolean> postChildCreatedProperty = controller.getModel().postChildCreatedCalledProperty();
            Assert.assertNull(postChildCreatedProperty.get());
            controller.createController(CHILD_CONTROLLER_NAME);
            Thread.sleep(2000);
            Assert.assertTrue(postChildCreatedProperty.get());
            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if @PostChildCreated is not called in controller")
    public void testPostChildCreatedCalledWhenNonChildControllerCreated(final String containerType, final String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<ParentTestBean> controller = createController(context, PARENT_CONTROLLER_NAME);
            Assert.assertTrue(controller.getModel().postCreatedCalledProperty().get());
            Property<Boolean> postChildCreatedProperty = controller.getModel().postChildCreatedCalledProperty();
            Assert.assertNull(postChildCreatedProperty.get());
            controller.createController(DUMMY_CHILD_CONTROLLER_NAME);
            Thread.sleep(1000);
            Assert.assertNull(postChildCreatedProperty.get());
            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if @PreChildDestroyed is called in controller")
    public void testPreChildDestroyedCalledWhenChildControllerDestroyed(final String containerType, final String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<ParentTestBean> controller = createController(context, PARENT_CONTROLLER_NAME);
            controller.createController(CHILD_CONTROLLER_NAME);
            Property<Boolean> preChildDestroyProperty = controller.getModel().preChildDestroyedCalledProperty();
            Assert.assertNull(preChildDestroyProperty.get());
            destroy(controller, endpoint);
            Assert.assertTrue(preChildDestroyProperty.get());
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if @PreChildDestroyed is not called in controller")
    public void testPreChildDestroyedNotCalledWhenNonChildControllerDestroyed(final String containerType, final String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<ParentTestBean> controller = createController(context, PARENT_CONTROLLER_NAME);
            ControllerProxy<?> childController = controller.createController(DUMMY_CHILD_CONTROLLER_NAME).get();
            Property<Boolean> preDestroyProperty = controller.getModel().preChildDestroyedCalledProperty();
            Assert.assertNull(preDestroyProperty.get());
            childController.destroy();
            Assert.assertNull(preDestroyProperty.get());
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if @PreDestroyed in parent is called first")
    public void testPreDestroyInParentIsCalledFirstWhenParentIsDestroyed(final String containerType, final String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<ParentTestBean> controller = createController(context, PARENT_CONTROLLER_NAME);
            ControllerProxy<?> childController = controller.createController(CHILD_CONTROLLER_NAME).get();
            Property<Boolean> childPreDestroyCalledProperty = ((ChildTestBean)childController.getModel()).preDestroyedCalledProperty();
            Property<Boolean> parentPreDestroyCalledProperty = controller.getModel().preDestroyedCalledProperty();
            Assert.assertNull(parentPreDestroyCalledProperty.get());
            Assert.assertNull(childPreDestroyCalledProperty.get());
            destroy(controller, endpoint);
            Assert.assertTrue(parentPreDestroyCalledProperty.get());
            Assert.assertTrue(childPreDestroyCalledProperty.get());
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }
}
