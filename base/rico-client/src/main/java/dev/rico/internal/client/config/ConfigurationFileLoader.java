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
package dev.rico.internal.client.config;

import dev.rico.internal.client.DefaultClientConfiguration;
import dev.rico.internal.core.Assert;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class ConfigurationFileLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationFileLoader.class);

    private static final String PLATFORM_LOCATION = "platform.properties";

    private static final String DEFAULT_LOCATION = "rico.properties";

    private static final String JAR_LOCATION = "META-INF/rico.properties";

    private ConfigurationFileLoader() {
    }

    public static DefaultClientConfiguration loadConfiguration(final String... additionalLocations) {
        final DefaultClientConfiguration configuration = createConfiguration(additionalLocations);
        Assert.requireNonNull(configuration, "configuration");

        LOG.debug("Configuration created with {} properties", configuration.getPropertyKeys().size());
        if(LOG.isTraceEnabled()) {
            for(String key : configuration.getPropertyKeys()) {
                LOG.debug("Configured with '{}'='{}'", key, configuration.getProperty(key, null));
            }
        }
        return configuration;
    }

    private static DefaultClientConfiguration createConfiguration(final String... additionalLocations) {
        try {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            if(additionalLocations != null) {
                for(final String location : additionalLocations) {
                    LOG.trace("Trying to load configuration at '" + location + "'");
                    try (final InputStream inputStream = classLoader.getResourceAsStream(location)) {
                        if (inputStream != null) {
                            return readConfig(inputStream);
                        }
                    } catch (final Exception e) {
                        LOG.trace("No config found at '" + location + "'");
                    }
                }
            }

            try (final InputStream inputStream = classLoader.getResourceAsStream(PLATFORM_LOCATION)) {
                LOG.trace("Trying to load configuration at '" + PLATFORM_LOCATION + "'");
                if (inputStream != null) {
                    return readConfig(inputStream);
                }
            } catch (final Exception e) {
                LOG.trace("No config found at '" + PLATFORM_LOCATION + "'");
            }

            try (final InputStream inputStream = classLoader.getResourceAsStream(DEFAULT_LOCATION)) {
                LOG.trace("Trying to load configuration at '" + DEFAULT_LOCATION + "'");
                if (inputStream != null) {
                    return readConfig(inputStream);
                }
            } catch (final Exception e) {
                LOG.trace("No config found at '" + DEFAULT_LOCATION + "'");
            }

            try (final InputStream inputStream = classLoader.getResourceAsStream(JAR_LOCATION)) {
                LOG.trace("Trying to load configuration at '" + JAR_LOCATION + "'");
                if (inputStream != null) {
                    return readConfig(inputStream);
                }
            } catch (final Exception e) {
                LOG.trace("No config found at '" + JAR_LOCATION + "'");
            }

            return new DefaultClientConfiguration(new Properties());
        } catch (final Exception e) {
            throw new RuntimeException("Can not create configuration!", e);
        }
    }

    private static DefaultClientConfiguration readConfig(final InputStream input) throws IOException {
        Assert.requireNonNull(input, "input");
        final Properties prop = new Properties();
        prop.load(input);

        return new DefaultClientConfiguration(prop);
    }
}
