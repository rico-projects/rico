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
package dev.rico.internal.core.context;

import dev.rico.core.context.ContextManager;
import dev.rico.core.functional.Subscription;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ContextManagerImplTests {

    @Test
    public void testGlobalContextDefaults() {
        // when
        final ContextManager manager = new ContextManagerImpl();

        // then
        assertEquals(manager.getGlobalAttributes().size(), 5);
        checkForGlobalContext(manager, "hostName");
        checkForGlobalContext(manager, "platform.version");
        checkForGlobalContext(manager, "canonicalHostName");
        checkForGlobalContext(manager, "hostAddress");
        checkForGlobalContext(manager, "application.name");
    }

    @Test
    public void testGlobalContext() {
        //given
        final ContextManager manager = new ContextManagerImpl();

        //when
        manager.setGlobalAttribute("KEY", "VALUE");

        //then
        assertEquals(manager.getGlobalAttributes().size(), 6);
        checkForGlobalContext(manager, "KEY", "VALUE");
    }

    @Test
    public void testOverrideGlobalContext() {
        //given:
        final ContextManager manager = new ContextManagerImpl();

        //when:
        manager.setGlobalAttribute("KEY", "VALUE");
        manager.setGlobalAttribute("KEY", "VALUE-2");

        //then:
        assertEquals(manager.getGlobalAttributes().size(), 6);
        checkForGlobalContext(manager, "KEY", "VALUE-2");
    }

    @Test
    public void testRemoveGlobalContext() {
        //given:
        final ContextManager manager = new ContextManagerImpl();

        //when:
        final Subscription subscription = manager.setGlobalAttribute("KEY", "VALUE");
        subscription.unsubscribe();

        //then:
        assertEquals(manager.getGlobalAttributes().size(), 5);
        checkForGlobalContextMissing(manager, "KEY");
    }

    private void checkForGlobalContextMissing(final ContextManager manager, final String name) {
        checkForGlobalContext(manager, name, null);
    }

    private void checkForGlobalContext(final ContextManager manager, final String name) {
        final String foundValue = manager.getGlobalAttributes().get(name);
        assertNotNull(foundValue, "Global attribute with name '" + name + "' not found");
    }

    private void checkForGlobalContext(final ContextManager manager, final String name, final String expectedValue) {
        final String foundValue = manager.getGlobalAttributes().get(name);
        assertEquals(foundValue, expectedValue, "Wrong global attribute with name '" + name + "' expected '" + expectedValue + "' but found'" + foundValue +"'");
    }

}
