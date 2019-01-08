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
package dev.rico.internal.server.remoting.controller.repository;

import dev.rico.internal.server.remoting.controller.ControllerRepository;
import dev.rico.internal.server.scanner.DefaultClasspathScanner;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ControllerRepositoryTest {

    @Test
    public void testExistingControllers() throws Exception{
        ControllerRepository controllerRepository = new ControllerRepository(new DefaultClasspathScanner(ControllerRepositoryTest.class.getPackage().getName()));
        Class<?> controllerClass = controllerRepository.getControllerClassForName(TestController.class.getName());
        assertNotNull(controllerClass);
        assertEquals(controllerClass, TestController.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testWrongControllersName() throws Exception{
        ControllerRepository controllerRepository = new ControllerRepository(new DefaultClasspathScanner(ControllerRepositoryTest.class.getPackage().getName()));
        controllerRepository.getControllerClassForName("WrongControllerName");
    }

}
