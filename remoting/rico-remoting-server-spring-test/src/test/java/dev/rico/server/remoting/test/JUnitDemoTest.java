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
package dev.rico.server.remoting.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class JUnitDemoTest extends SpringJUnitControllerTest {

    @Test
    public void testCreation() {
        ControllerUnderTest<TestModel> controller = createController("TestController");
        assertNotNull(controller);
        assertNotNull(controller.getModel());
        controller.destroy();
    }

    @Test
    public void testInteraction() {
        ControllerUnderTest<TestModel> controller = createController("TestController");
        assertEquals(null, controller.getModel().getValue());
        controller.invoke("action");
        assertEquals("Hello Rico Test", controller.getModel().getValue());
        controller.destroy();
    }

    @Test
    public void testDestroy() {
        ControllerUnderTest<TestModel> controller = createController("TestController");
        controller.destroy();
        try {
            controller.destroy();
            fail("Calling destroy() for a destroyed controller should throw an exception!");
        } catch (ControllerTestException e) {}
    }

    @Test
    public void testInvokeUnknownAction() {
        ControllerUnderTest<TestModel> controller = createController("TestController");
        try {
            controller.invoke("unknownActionName");
            fail("Calling an unknown action should throw an exception!");
        } catch (ControllerTestException e) {}
        controller.destroy();
    }

    @Test
    public void testInvokeActionAfterDestroy() {
        ControllerUnderTest<TestModel> controller = createController("TestController");
        controller.destroy();
        try {
            controller.invoke("add");
            fail("Calling an action after destroy should throw an exception!");
        } catch (ControllerTestException e) {}
    }

}
