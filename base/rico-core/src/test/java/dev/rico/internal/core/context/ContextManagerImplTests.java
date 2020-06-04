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

import dev.rico.core.context.Context;
import dev.rico.core.context.ContextManager;
import dev.rico.core.functional.Subscription;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Optional;

public class ContextManagerImplTests {

    @Test
    public void testGlobalContextDefaults() {
        // when:
        final ContextManager manager = new ContextManagerImpl();


        // then:
        Assert.assertEquals(manager.getGlobalContexts().size(), 5);
        checkForGlobalContext(manager, "hostName");
        checkForGlobalContext(manager, "platform.version");
        checkForGlobalContext(manager, "canonicalHostName");
        checkForGlobalContext(manager, "hostAddress");
        checkForGlobalContext(manager, "application.name");
    }

    @Test
    public void testGlobalContext() {
        //given:
        final ContextManager manager = new ContextManagerImpl();

        //when:
        manager.addGlobalContext("KEY", "VALUE");

        //then:
        Assert.assertEquals(manager.getGlobalContexts().size(), 6);
        checkForGlobalContext(manager, "KEY", "VALUE");
    }

    @Test
    public void testOverrideGlobalContext() {
        //given:
        final ContextManager manager = new ContextManagerImpl();

        //when:
        manager.addGlobalContext("KEY", "VALUE");
        manager.addGlobalContext("KEY", "VALUE-2");

        //then:
        Assert.assertEquals(manager.getGlobalContexts().size(), 6);
        checkForGlobalContext(manager, "KEY", "VALUE-2");
    }

    @Test
    public void testRemoveGlobalContext() {
        //given:
        final ContextManager manager = new ContextManagerImpl();

        //when:
        final Subscription subscription = manager.addGlobalContext("KEY", "VALUE");
        subscription.unsubscribe();

        //then:
        Assert.assertEquals(manager.getGlobalContexts().size(), 5);
        checkForGlobalContextMissing(manager, "KEY");
    }

    private void checkForGlobalContextMissing(final ContextManager manager, final String type) {
        final Optional<Context> context = manager.getGlobalContexts()
                .stream()
                .filter(c -> c.getType().equals(type))
                .findAny();
        Assert.assertFalse(context.isPresent(), "Context of type " + type + " was found.");
    }

    private void checkForGlobalContext(final ContextManager manager, final String type) {
        final Optional<Context> context = manager.getGlobalContexts()
                .stream()
                .filter(c -> c.getType().equals(type))
                .findAny();
        Assert.assertTrue(context.isPresent(), "Context of type " + type + " not found.");
    }

    private void checkForGlobalContext(final ContextManager manager, final String type, final String value) {
        final Optional<Context> context = manager.getGlobalContexts()
                .stream()
                .filter(c -> c.getType().equals(type))
                .filter(c -> c.getValue().equals(value))
                .findAny();
        Assert.assertTrue(context.isPresent(), "Context of type " + type + " and value " + value + " not found.");
    }

}
