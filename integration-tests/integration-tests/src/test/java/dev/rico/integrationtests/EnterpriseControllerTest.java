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
package dev.rico.integrationtests;

import dev.rico.client.remoting.ClientContext;
import dev.rico.client.remoting.ControllerProxy;
import dev.rico.integrationtests.enterprise.EnterpriseTestBean;
import dev.rico.remoting.Property;
import org.testng.Assert;
import org.testng.annotations.Test;

import static dev.rico.integrationtests.enterprise.EnterpriseTestConstants.ENTERPRISE_CONTROLLER_NAME;


public class EnterpriseControllerTest extends AbstractIntegrationTest {

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if controller and model can be created")
    public void testCreateController(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<EnterpriseTestBean> controller = createController(context, ENTERPRISE_CONTROLLER_NAME);
            Assert.assertNotNull(controller);
            Assert.assertNotNull(controller.getModel());
            Assert.assertEquals(controller.getModel().getClass(), EnterpriseTestBean.class);
            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not createList controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if @PostConstruct is called in controller")
    public void testPostConstruct(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<EnterpriseTestBean> controller = createController(context, ENTERPRISE_CONTROLLER_NAME);
            Assert.assertTrue(controller.getModel().getPostConstructCalled());
            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not createList controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if @PreDestroy is called in controller")
    public void testPreDestroy(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<EnterpriseTestBean> controller = createController(context, ENTERPRISE_CONTROLLER_NAME);
            Property<Boolean> preDestroyProperty = controller.getModel().preDestroyCalledProperty();
            Assert.assertNull(preDestroyProperty.get());
            destroy(controller, endpoint);
            Assert.assertTrue(preDestroyProperty.get());
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not createList controller for " + containerType, e);
        }
    }
}
