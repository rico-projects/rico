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

import dev.rico.core.context.RicoApplicationContext;
import dev.rico.core.functional.Assignment;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static dev.rico.internal.core.context.ContextConstants.APPLICATION_NAME_CONTEXT;
import static dev.rico.internal.core.context.ContextConstants.APPLICATION_START_LOCALE_CONTEXT;
import static dev.rico.internal.core.context.ContextConstants.APPLICATION_START_TIME_CONTEXT;
import static dev.rico.internal.core.context.ContextConstants.APPLICATION_SYSTEM_TIMEZONE_CONTEXT;
import static dev.rico.internal.core.context.ContextConstants.CANONICAL_HOST_NAME_CONTEXT;
import static dev.rico.internal.core.context.ContextConstants.HOST_ADDRESS_CONTEXT;
import static dev.rico.internal.core.context.ContextConstants.HOST_NAME_CONTEXT;
import static dev.rico.internal.core.context.ContextConstants.JAVA_VENDOR_CONTEXT;
import static dev.rico.internal.core.context.ContextConstants.JAVA_VERSION_CONTEXT;
import static dev.rico.internal.core.context.ContextConstants.OS_CONTEXT;
import static dev.rico.internal.core.context.ContextConstants.PLATFORM_VERSION_CONTEXT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class RicoApplicationContextImplTests {

    private static final String[] INITIAL_GLOBAL_ATTRIBUTES = {HOST_NAME_CONTEXT, PLATFORM_VERSION_CONTEXT, CANONICAL_HOST_NAME_CONTEXT,
            HOST_ADDRESS_CONTEXT, APPLICATION_NAME_CONTEXT, JAVA_VERSION_CONTEXT, JAVA_VENDOR_CONTEXT,
            OS_CONTEXT, APPLICATION_START_TIME_CONTEXT, APPLICATION_START_LOCALE_CONTEXT, APPLICATION_SYSTEM_TIMEZONE_CONTEXT};

    private static final String key_1 = "KEY-1";
    private static final String key_2 = "KEY-2";
    private static final String value_1 = "VALUE-1";
    private static final String value_2 = "VALUE-2";

    private RicoApplicationContext manager;

    @BeforeMethod
    public void setup() {
        manager = new RicoApplicationContextImpl();
    }

    @Test
    public void testInitialState() {
        assertEquals(manager.getThreadLocalAttributes().size(), 0);

        assertEquals(manager.getGlobalAttributes().size(), INITIAL_GLOBAL_ATTRIBUTES.length);
        assertEquals(manager.getAttributes().size(), INITIAL_GLOBAL_ATTRIBUTES.length);

        Stream.of(INITIAL_GLOBAL_ATTRIBUTES).forEach(n -> assertNameExists(manager.getGlobalAttributes(), n));
        Stream.of(INITIAL_GLOBAL_ATTRIBUTES).forEach(n -> assertNameExists(manager.getAttributes(), n));
        Stream.of(INITIAL_GLOBAL_ATTRIBUTES).forEach(n -> assertTrue(manager.getAttribute(n).isPresent()));
    }

    @Test
    public void testSettingGlobalContext() {
        //given
        final int initialGlobalSize = manager.getGlobalAttributes().size();
        final int initialThreadSize = manager.getThreadLocalAttributes().size();
        final int initialMergedSize = manager.getAttributes().size();

        // when
        manager.setGlobalAttribute(key_1, value_1);

        // then
        assertValue(manager.getAttribute(key_1), value_1);

        assertEquals(manager.getThreadLocalAttributes().size(), initialThreadSize);
        assertNameDoesNotExist(manager.getThreadLocalAttributes(), key_1);

        assertEquals(manager.getGlobalAttributes().size(), initialGlobalSize + 1);
        assertContainsKeyValue(manager.getGlobalAttributes(), key_1, value_1);

        assertEquals(manager.getAttributes().size(), initialMergedSize + 1);
        assertContainsKeyValue(manager.getAttributes(), key_1, value_1);
    }

    @Test
    public void testOverrideGlobalContext() {
        //given
        final int initialGlobalSize = manager.getGlobalAttributes().size();
        final int initialThreadSize = manager.getThreadLocalAttributes().size();
        final int initialMergedSize = manager.getAttributes().size();

        // when
        manager.setGlobalAttribute(key_1, value_1);
        manager.setGlobalAttribute(key_1, value_2);

        // then
        assertValue(manager.getAttribute(key_1), value_2);

        assertEquals(manager.getThreadLocalAttributes().size(), initialThreadSize);
        assertNameDoesNotExist(manager.getThreadLocalAttributes(), key_1);

        assertEquals(manager.getGlobalAttributes().size(), initialGlobalSize + 1);
        assertContainsKeyValue(manager.getGlobalAttributes(), key_1, value_2);

        assertEquals(manager.getAttributes().size(), initialMergedSize + 1);
        assertContainsKeyValue(manager.getAttributes(), key_1, value_2);
    }

    @Test
    public void testTwoGlobalContext() {
        //given
        final int initialGlobalSize = manager.getGlobalAttributes().size();
        final int initialThreadSize = manager.getThreadLocalAttributes().size();
        final int initialMergedSize = manager.getAttributes().size();

        // when
        manager.setGlobalAttribute(key_1, value_1);
        manager.setGlobalAttribute(key_2, value_2);

        // then
        assertValue(manager.getAttribute(key_1), value_1);
        assertValue(manager.getAttribute(key_2), value_2);

        assertEquals(manager.getThreadLocalAttributes().size(), initialThreadSize);
        assertNameDoesNotExist(manager.getThreadLocalAttributes(), key_1);
        assertNameDoesNotExist(manager.getThreadLocalAttributes(), key_2);

        assertEquals(manager.getGlobalAttributes().size(), initialGlobalSize + 2);
        assertContainsKeyValue(manager.getGlobalAttributes(), key_1, value_1);
        assertContainsKeyValue(manager.getGlobalAttributes(), key_2, value_2);

        assertEquals(manager.getAttributes().size(), initialMergedSize + 2);
        assertContainsKeyValue(manager.getAttributes(), key_1, value_1);
        assertContainsKeyValue(manager.getAttributes(), key_2, value_2);
    }

    @Test
    public void testRemoveGlobalContext() {
        //given
        final int initialGlobalSize = manager.getGlobalAttributes().size();
        final int initialThreadSize = manager.getThreadLocalAttributes().size();
        final int initialMergedSize = manager.getAttributes().size();

        // when
        final Assignment assignment = manager.setGlobalAttribute(key_1, value_1);
        assignment.unset();

        // then
        assertFalse(manager.getAttribute(key_1).isPresent());

        assertEquals(manager.getThreadLocalAttributes().size(), initialThreadSize);
        assertNameDoesNotExist(manager.getThreadLocalAttributes(), key_1);

        assertEquals(manager.getGlobalAttributes().size(), initialGlobalSize);
        assertNameDoesNotExist(manager.getGlobalAttributes(), key_1);

        assertEquals(manager.getAttributes().size(), initialMergedSize);
        assertNameDoesNotExist(manager.getAttributes(), key_1);
    }

    @Test
    public void testSettingThreadLocalContext() {
        //given
        final int initialGlobalSize = manager.getGlobalAttributes().size();
        final int initialThreadSize = manager.getThreadLocalAttributes().size();
        final int initialMergedSize = manager.getAttributes().size();

        // when
        manager.setThreadLocalAttribute(key_1, value_1);

        // then
        assertValue(manager.getAttribute(key_1), value_1);

        assertEquals(manager.getThreadLocalAttributes().size(), initialThreadSize + 1);
        assertContainsKeyValue(manager.getThreadLocalAttributes(), key_1, value_1);

        assertEquals(manager.getGlobalAttributes().size(), initialGlobalSize);
        assertNameDoesNotExist(manager.getGlobalAttributes(), key_1);

        assertEquals(manager.getAttributes().size(), initialMergedSize + 1);
        assertContainsKeyValue(manager.getAttributes(), key_1, value_1);
    }

    @Test
    public void testOverrideThreadLocalContext() {
        //given
        final int initialGlobalSize = manager.getGlobalAttributes().size();
        final int initialThreadSize = manager.getThreadLocalAttributes().size();
        final int initialMergedSize = manager.getAttributes().size();

        // when
        manager.setThreadLocalAttribute(key_1, value_1);
        manager.setThreadLocalAttribute(key_1, value_2);

        // then
        assertValue(manager.getAttribute(key_1), value_2);

        assertEquals(manager.getThreadLocalAttributes().size(), initialThreadSize + 1);
        assertContainsKeyValue(manager.getThreadLocalAttributes(), key_1, value_2);

        assertEquals(manager.getGlobalAttributes().size(), initialGlobalSize);
        assertNameDoesNotExist(manager.getGlobalAttributes(), key_1);

        assertEquals(manager.getAttributes().size(), initialMergedSize + 1);
        assertContainsKeyValue(manager.getAttributes(), key_1, value_2);
    }

    @Test
    public void testTwoThreadLocalContext() {
        //given
        final int initialGlobalSize = manager.getGlobalAttributes().size();
        final int initialThreadSize = manager.getThreadLocalAttributes().size();
        final int initialMergedSize = manager.getAttributes().size();

        // when
        manager.setThreadLocalAttribute(key_1, value_1);
        manager.setThreadLocalAttribute(key_2, value_2);

        // then
        assertValue(manager.getAttribute(key_1), value_1);
        assertValue(manager.getAttribute(key_2), value_2);

        assertEquals(manager.getThreadLocalAttributes().size(), initialThreadSize + 2);
        assertContainsKeyValue(manager.getThreadLocalAttributes(), key_1, value_1);
        assertContainsKeyValue(manager.getThreadLocalAttributes(), key_2, value_2);

        assertEquals(manager.getGlobalAttributes().size(), initialGlobalSize);
        assertNameDoesNotExist(manager.getGlobalAttributes(), key_1);
        assertNameDoesNotExist(manager.getGlobalAttributes(), key_2);

        assertEquals(manager.getAttributes().size(), initialMergedSize + 2);
        assertContainsKeyValue(manager.getAttributes(), key_1, value_1);
        assertContainsKeyValue(manager.getAttributes(), key_2, value_2);
    }

    @Test
    public void testRemoveThreadLocalContext() {
        //given
        final int initialGlobalSize = manager.getGlobalAttributes().size();
        final int initialThreadSize = manager.getThreadLocalAttributes().size();
        final int initialMergedSize = manager.getAttributes().size();

        // when
        final Assignment assignment = manager.setThreadLocalAttribute(key_1, value_1);
        assignment.unset();

        // then
        assertFalse(manager.getAttribute(key_1).isPresent());

        assertEquals(manager.getThreadLocalAttributes().size(), initialThreadSize);
        assertNameDoesNotExist(manager.getThreadLocalAttributes(), key_1);

        assertEquals(manager.getGlobalAttributes().size(), initialGlobalSize);
        assertNameDoesNotExist(manager.getGlobalAttributes(), key_1);

        assertEquals(manager.getAttributes().size(), initialMergedSize);
        assertNameDoesNotExist(manager.getAttributes(), key_1);
    }


    @Test
    public void testThreadLocalContextOverwritesGlobalContext() {
        //given
        final int initialGlobalSize = manager.getGlobalAttributes().size();
        final int initialThreadSize = manager.getThreadLocalAttributes().size();
        final int initialMergedSize = manager.getAttributes().size();

        // when
        manager.setGlobalAttribute(key_1, value_1);
        manager.setThreadLocalAttribute(key_1, value_2);
        manager.setGlobalAttribute(key_1, value_1);

        // then
        assertValue(manager.getAttribute(key_1), value_2);

        assertEquals(manager.getGlobalAttributes().size(), initialGlobalSize + 1);
        assertContainsKeyValue(manager.getGlobalAttributes(), key_1, value_1);

        assertEquals(manager.getThreadLocalAttributes().size(), initialThreadSize + 1);
        assertContainsKeyValue(manager.getThreadLocalAttributes(), key_1, value_2);

        assertEquals(manager.getAttributes().size(), initialMergedSize + 1);
        assertContainsKeyValue(manager.getAttributes(), key_1, value_2);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void assertValue(Optional<String> actual, String expected) {
        assertEquals(actual.orElseThrow(), expected);
    }

    private void assertNameDoesNotExist(Map<String, String> attributes, final String name) {
        assertFalse(attributes.containsKey(name), "Global attribute with name '" + name + "' found");
    }

    private void assertNameExists(Map<String, String> attributes, final String name) {
        assertTrue(attributes.containsKey(name), "Global attribute with name '" + name + "' not found");
    }

    private void assertContainsKeyValue(Map<String, String> attributes, final String name, final String expectedValue) {
        final String foundValue = attributes.get(name);
        assertEquals(foundValue, expectedValue, "Wrong global attribute with name '" + name + "' expected '" + expectedValue + "' but found'" + foundValue + "'");
    }

}
