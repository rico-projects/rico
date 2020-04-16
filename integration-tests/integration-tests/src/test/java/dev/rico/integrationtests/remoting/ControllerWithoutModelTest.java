/*
 * Copyright 2018-2019 Karakun AG.
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

import dev.rico.remoting.client.ClientContext;
import dev.rico.remoting.client.ControllerProxy;
import org.testng.Assert;
import org.testng.annotations.Test;

import static dev.rico.integrationtests.remoting.controllerwithoutmodel.ControllerWithoutModelTestConstants.CONTROLLER_WITHOUT_MODEL_NAME;

public class ControllerWithoutModelTest extends AbstractRemotingIntegrationTest {

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if a controller without model can be created")
    public void testCreateController(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy controller = createController(context, CONTROLLER_WITHOUT_MODEL_NAME);
            Assert.assertNull(controller.getModel());
            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Error in test for " + containerType, e);
        }
    }
}
