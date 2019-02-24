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
package dev.rico.integrationtests.server.remoting.runLater;

import dev.rico.integrationtests.remoting.runlater.RunLaterTestBean;
import dev.rico.integrationtests.server.TestConfiguration;
import dev.rico.server.remoting.test.CommunicationMonitor;
import dev.rico.server.remoting.test.ControllerUnderTest;
import dev.rico.server.remoting.test.SpringTestNGControllerTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static dev.rico.integrationtests.remoting.runlater.RunLaterTestConstants.RUN_LATER_ACTION_NAME;
import static dev.rico.integrationtests.remoting.runlater.RunLaterTestConstants.RUN_LATER_ASYNC_ACTION_NAME;
import static dev.rico.integrationtests.remoting.runlater.RunLaterTestConstants.RUN_LATER_CONTROLLER_NAME;

@SpringBootTest(classes = TestConfiguration.class)
public class RunLaterControllerTest extends SpringTestNGControllerTest {

    @Test
    public void testRunLaterInPostConstruct() {
        //given:
        final ControllerUnderTest<RunLaterTestBean> controller = createController(RUN_LATER_CONTROLLER_NAME);
        final RunLaterTestBean model = controller.getModel();

        //then
        Assert.assertNotNull(model.getPostConstructPreRunLaterCallIndex());
        Assert.assertNotNull(model.getPostConstructRunLaterCallIndex());
        Assert.assertNotNull(model.getPostConstructPostRunLaterCallIndex());

        Assert.assertTrue(model.getPostConstructPreRunLaterCallIndex() > 0);
        Assert.assertTrue(model.getPostConstructRunLaterCallIndex() > 0);
        Assert.assertTrue(model.getPostConstructPostRunLaterCallIndex() > 0);

        Assert.assertTrue(model.getPostConstructPreRunLaterCallIndex() < model.getPostConstructPostRunLaterCallIndex());
        Assert.assertTrue(model.getPostConstructPostRunLaterCallIndex() < model.getPostConstructRunLaterCallIndex());

    }

    @Test
    public void testRunLaterInAction() {
        //given:
        final ControllerUnderTest<RunLaterTestBean> controller = createController(RUN_LATER_CONTROLLER_NAME);
        final RunLaterTestBean model = controller.getModel();

        //when:
        controller.invoke(RUN_LATER_ACTION_NAME);

        //then:
        Assert.assertNotNull(model.getActionPreRunLaterCallIndex());
        Assert.assertNotNull(model.getActionRunLaterCallIndex());
        Assert.assertNotNull(model.getActionPostRunLaterCallIndex());

        Assert.assertTrue(model.getActionPreRunLaterCallIndex() > 0);
        Assert.assertTrue(model.getActionRunLaterCallIndex() > 0);
        Assert.assertTrue(model.getActionPostRunLaterCallIndex() > 0);

        Assert.assertTrue(model.getActionPreRunLaterCallIndex() < model.getActionPostRunLaterCallIndex());
        Assert.assertTrue(model.getActionPostRunLaterCallIndex() < model.getActionRunLaterCallIndex());
    }

    @Test
    public void testRunLaterAsyncInAction() throws Exception {
        //given:
        final ControllerUnderTest<RunLaterTestBean> controller = createController(RUN_LATER_CONTROLLER_NAME);
        final RunLaterTestBean model = controller.getModel();
        final CommunicationMonitor monitor = controller.createMonitor();
        model.actionRunLaterAsyncCallIndexProperty().onChanged(e -> {
            monitor.signal();
        });

        //when:
        controller.invoke(RUN_LATER_ASYNC_ACTION_NAME);
        monitor.await(10, TimeUnit.SECONDS);

        //then:
        Assert.assertNotNull(model.getActionPreRunLaterAsyncCallIndex());
        Assert.assertNotNull(model.getActionRunLaterAsyncCallIndex());
        Assert.assertNotNull(model.getActionPostRunLaterAsyncCallIndex());

        Assert.assertTrue(model.getActionPreRunLaterAsyncCallIndex() > 0);
        Assert.assertTrue(model.getActionRunLaterAsyncCallIndex() > 0);
        Assert.assertTrue(model.getActionPostRunLaterAsyncCallIndex() > 0);

        Assert.assertTrue(model.getActionPreRunLaterAsyncCallIndex() < model.getActionPostRunLaterAsyncCallIndex());
        Assert.assertTrue(model.getActionPostRunLaterAsyncCallIndex() < model.getActionRunLaterAsyncCallIndex());
    }
}
