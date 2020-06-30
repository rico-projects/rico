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
package dev.rico.internal.server.config;

import dev.rico.internal.core.Assert;
import dev.rico.server.spi.ConfigurationProvider;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.function.BiConsumer;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * This class loads a configuration (see {@link ServerConfiguration}) based on a property file.
 * The file must be placed under "META-INF/rico.properties" (normal for a JAR) or under
 * "WEB-INF/classes/META-INF/rico.properties" (normal for a WAR). If no file can be found a default
 * confihuration will be returned.
 * <p>
 * Currently the following properties will be supported in the "rico.properties" file
 * <p>
 * - servletMapping that defines the endpoint of the remoting servlet
 * - useCrossSiteOriginFilter (true / false) that defines if a cross site origin filter should be used
 * <p>
 * All properties that are not specified in the property file will be defined by default values.
 */
@API(since = "0.x", status = INTERNAL)
public class ConfigurationFileLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationFileLoader.class);

    private static final String DEFAULT_LOCATION = "rico.properties";

    private static final String JAR_LOCATION = "META-INF/rico.properties";

    private static final String WAR_LOCATION = "WEB-INF/classes/" + JAR_LOCATION;

    private ConfigurationFileLoader() {
    }

    /**
     * Tries to load a {@link ServerConfiguration} based on a file.
     * If no config file can be found a default config will be returned.
     *
     * @return a configuration
     */
    public static ServerConfiguration loadConfiguration() {
        final ServerConfiguration configuration = createConfiguration();
        Assert.requireNonNull(configuration, "configuration");

        ServiceLoader.load(ConfigurationProvider.class).stream()
                .map(ServiceLoader.Provider::get)
                .forEach(provider -> {
                    setAdditionalProperties(configuration, provider.getStringProperties(), configuration::setProperty);
                    setAdditionalProperties(configuration, provider.getListProperties(), configuration::setListProperty);
                    setAdditionalProperties(configuration, provider.getBooleanProperties(), configuration::setBooleanProperty);
                    setAdditionalProperties(configuration, provider.getIntegerProperties(), configuration::setIntProperty);
                    setAdditionalProperties(configuration, provider.getLongProperties(), configuration::setLongProperty);
                });

        LOG.debug("Configuration created with {} properties", configuration.getPropertyKeys().size());
        if (LOG.isTraceEnabled()) {
            for (final String key : configuration.getPropertyKeys()) {
                LOG.debug("Configured with '{}'='{}'", key, configuration.getProperty(key, null));
            }
        }

        return configuration;
    }

    private static <B> void setAdditionalProperties(ServerConfiguration configuration, Map<String, B> properties, BiConsumer<String, B> setProperty) {
        for (final Map.Entry<String, B> property : properties.entrySet()) {
            try {
                if (!configuration.containsProperty(property.getKey())) {
                    setProperty.accept(property.getKey(), property.getValue());
                }
            } catch (NullPointerException e) {
                LOG.warn("Value for {} is {}}", property.getKey(), property.getValue());
            }
        }
    }

    private static ServerConfiguration createConfiguration() {
        try {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            for (final String location : List.of(DEFAULT_LOCATION, JAR_LOCATION, WAR_LOCATION)) {
                try (final InputStream inputStream = classLoader.getResourceAsStream(location)) {
                    if (inputStream != null) {
                        return readConfig(inputStream);
                    }
                }
            }

            LOG.info("Can not read configuration. Maybe no {} file is defined. Will use a default configuration!", DEFAULT_LOCATION);
            return new ServerConfiguration();
        } catch (final IOException e) {
            throw new RuntimeException("Can not create configuration!", e);
        }
    }

    private static ServerConfiguration readConfig(final InputStream input) throws IOException {
        Assert.requireNonNull(input, "input");
        final Properties prop = new Properties();
        prop.load(input);

        return new ServerConfiguration(prop);
    }
}
