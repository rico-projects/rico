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
package dev.rico.server;

import dev.rico.internal.server.config.ConfigurationFileLoader;
import dev.rico.internal.server.config.ServerConfiguration;
import org.testng.annotations.Test;

import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.ROOT_PACKAGE_FOR_CLASSPATH_SCAN;
import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.USE_CROSS_SITE_ORIGIN_FILTER;
import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.USE_CROSS_SITE_ORIGIN_FILTER_DEFAULT_VALUE;
import static org.testng.Assert.*;


public class ConfigurationFileLoaderTest {

    @Test
    public void testConfigLoad() {
        try {
            //given:
            final ServerConfiguration configuration = ConfigurationFileLoader.loadConfiguration();

            //then:
            assertEquals(configuration.getBooleanProperty(USE_CROSS_SITE_ORIGIN_FILTER), false);
            assertEquals(configuration.getProperty(ROOT_PACKAGE_FOR_CLASSPATH_SCAN), null);
        } catch (Exception e) {
            fail("Error in test", e);
        }
    }

    @Test
    public void testConfigurationProvider() {
        try {
            //given:
            final ServerConfiguration configuration = ConfigurationFileLoader.loadConfiguration();

            //then:
            assertEquals(configuration.getProperty(TestConfigurationProvider.PROPERTY_1_NAME, null), TestConfigurationProvider.PROPERTY_1_VALUE);
            assertEquals(configuration.getProperty(TestConfigurationProvider.PROPERTY_2_NAME, null), TestConfigurationProvider.PROPERTY_2_VALUE);
            assertEquals(configuration.getProperty(TestConfigurationProvider.PROPERTY_3_NAME, null), TestConfigurationProvider.PROPERTY_3_VALUE);
            assertEquals(configuration.getProperty(AdditionalTestConfigurationProvider.PROPERTY_NAME, null), AdditionalTestConfigurationProvider.PROPERTY_VALUE);
        } catch (Exception e) {
            fail("Error in test", e);
        }
    }

    @Test
    public void testConfigurationProviderDoNotOverwrite() {
        try {
            //given:
            final ServerConfiguration configuration = ConfigurationFileLoader.loadConfiguration();

            //then:
            assertEquals(configuration.getBooleanProperty(USE_CROSS_SITE_ORIGIN_FILTER, USE_CROSS_SITE_ORIGIN_FILTER_DEFAULT_VALUE), false);
            assertEquals(configuration.getProperty(USE_CROSS_SITE_ORIGIN_FILTER, null), "false");
        } catch (Exception e) {
            fail("Error in test", e);
        }
    }

    @Test
    public void testNullPropertyWillNotBeAdded() {
        try {
            //given:
            final ServerConfiguration configuration = ConfigurationFileLoader.loadConfiguration();

            //when:
            configuration.setProperty("test-key", null);

            //then:
            assertEquals(configuration.getProperty("test-key", null), null);
            assertEquals(configuration.getProperty("test-key", "a"), "a");
            assertEquals(configuration.getProperty(TestConfigurationProvider.PROPERTY_3_NAME, null), null);
            assertFalse(configuration.getPropertyKeys().contains("test-key"));
            assertFalse(configuration.getPropertyKeys().contains(TestConfigurationProvider.PROPERTY_3_NAME));
        } catch (Exception e) {
            fail("Error in test", e);
        }
    }

    @Test
    public void testNullPropertyWillNotRemoveOldValue() {
        try {
            //given:
            final ServerConfiguration configuration = ConfigurationFileLoader.loadConfiguration();

            //when:
            configuration.setProperty("test-key", "a");
            configuration.setProperty("test-key", null);

            //then:
            assertEquals(configuration.getProperty("test-key", null), "a");
            assertEquals(configuration.getProperty("test-key", "b"), "a");
            assertTrue(configuration.getPropertyKeys().contains("test-key"));
        } catch (Exception e) {
            fail("Error in test", e);
        }
    }
}
