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
package dev.rico.integrationtests.server.property;

import dev.rico.integrationtests.property.ObservableListBean;
import dev.rico.integrationtests.server.TestConfiguration;
import dev.rico.client.remoting.Param;
import dev.rico.server.remoting.test.ControllerUnderTest;
import dev.rico.server.remoting.test.SpringTestNGControllerTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.UUID;

import static dev.rico.integrationtests.property.PropertyTestConstants.ADD_ID_ACTION;
import static dev.rico.integrationtests.property.PropertyTestConstants.CHECK_SIZE_ACTION;
import static dev.rico.integrationtests.property.PropertyTestConstants.INDEX_PARAM;
import static dev.rico.integrationtests.property.PropertyTestConstants.LIST_CONTROLLER_NAME;
import static dev.rico.integrationtests.property.PropertyTestConstants.REMOVE_ACTION;
import static dev.rico.integrationtests.property.PropertyTestConstants.SIZE_PARAM;

@SpringBootTest(classes = TestConfiguration.class)
public class ObservableListControllerTest extends SpringTestNGControllerTest {

    private ControllerUnderTest<ObservableListBean> controller;

    @BeforeMethod
    public void init() {
        controller = createController(LIST_CONTROLLER_NAME);
    }

    @AfterMethod
    public void destroy() {
        controller.destroy();
    }

    @Test
    public void testControllerCreation() {
        Assert.assertNotNull(controller);
    }

    @Test
    public void testListCreation() {
        Assert.assertNotNull(controller.getModel().getList());
    }

    @Test
    public void testInitialSize() {
        Assert.assertEquals(controller.getModel().getList().size(), 0);
        checkSizeOnServer(0);
    }

    @Test
    public void testAddOnServer() {
        controller.invoke(ADD_ID_ACTION);
        Assert.assertEquals(controller.getModel().getList().size(), 1);

        controller.invoke(ADD_ID_ACTION);
        controller.invoke(ADD_ID_ACTION);
        Assert.assertEquals(controller.getModel().getList().size(), 3);

        controller.getModel().getList().forEach(v -> Assert.assertNotNull(v));
    }

    @Test
    public void testAddOnClient() {
        checkSizeOnServer(0);
        controller.getModel().getList().add(UUID.randomUUID().toString());
        checkSizeOnServer(1);
        controller.getModel().getList().add(UUID.randomUUID().toString());
        controller.getModel().getList().add(UUID.randomUUID().toString());
        checkSizeOnServer(3);
    }

    @Test
    public void testRemoveOnServer() {
        final String s1 = UUID.randomUUID().toString();
        final String s2 = UUID.randomUUID().toString();
        final String s3 = UUID.randomUUID().toString();

        controller.getModel().getList().add(s1);
        controller.getModel().getList().add(s2);
        controller.getModel().getList().add(s3);

        controller.invoke(REMOVE_ACTION, new Param(INDEX_PARAM, 0));
        Assert.assertEquals(controller.getModel().getList().size(), 2);
        Assert.assertTrue(controller.getModel().getList().contains(s2));
        Assert.assertTrue(controller.getModel().getList().contains(s3));

        controller.invoke(REMOVE_ACTION, new Param(INDEX_PARAM, 1));
        Assert.assertEquals(controller.getModel().getList().size(), 1);
        Assert.assertTrue(controller.getModel().getList().contains(s2));
    }

    @Test
    public void testRemoveOnClient() {
        controller.getModel().getList().add(UUID.randomUUID().toString());
        controller.getModel().getList().add(UUID.randomUUID().toString());
        controller.getModel().getList().add(UUID.randomUUID().toString());
        checkSizeOnServer(3);

        controller.getModel().getList().remove(0);
        checkSizeOnServer(2);

        controller.getModel().getList().remove(1);
        checkSizeOnServer(1);
    }

    @Test
    public void testRemoveRangeOnClient() {
        controller.getModel().getList().add(UUID.randomUUID().toString());
        controller.getModel().getList().add(UUID.randomUUID().toString());
        controller.getModel().getList().add(UUID.randomUUID().toString());
        controller.getModel().getList().add(UUID.randomUUID().toString());
        controller.getModel().getList().add(UUID.randomUUID().toString());

        controller.getModel().getList().remove(0, 3);
        checkSizeOnServer(2);
    }

    private void checkSizeOnServer(final int size) {
        controller.getModel().setCheckResult(false);
        controller.invoke(CHECK_SIZE_ACTION, new Param(SIZE_PARAM, size));
        Assert.assertTrue(controller.getModel().getCheckResult());
    }
}
