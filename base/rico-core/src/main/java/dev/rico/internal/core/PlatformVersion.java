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
package dev.rico.internal.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PlatformVersion {

    private final static Logger LOG = LoggerFactory.getLogger(PlatformVersion.class);

    private static final String DEFAULT_LOCATION = "build.properties";

    private static final String VERSION_PROPERTY_NAME = "version";

    private static final String BUILD_DATE_PROPERTY_NAME = "buildDate";

    private static final String BUILD_TIME_PROPERTY_NAME = "buildTime";

    private static final String UNKNOWN_VALUE = "unknown";

    private PlatformVersion() {
    }

    public static String getBuildTime() {
        try {
            return getBuildProperties().getProperty(BUILD_TIME_PROPERTY_NAME, UNKNOWN_VALUE);
        } catch (final Exception e) {
            LOG.error("Can not get build info", e);
            return UNKNOWN_VALUE;
        }
    }

    public static String getBuildDate() {
        try {
            return getBuildProperties().getProperty(BUILD_DATE_PROPERTY_NAME, UNKNOWN_VALUE);
        } catch (final Exception e) {
            LOG.error("Can not get build info", e);
            return UNKNOWN_VALUE;
        }
    }

    public static String getVersion() {
        try {
            return getBuildProperties().getProperty(VERSION_PROPERTY_NAME, UNKNOWN_VALUE);
        } catch (final Exception e) {
            LOG.error("Can not get build info", e);
            return UNKNOWN_VALUE;
        }
    }

    private static Properties getBuildProperties() throws IOException {
        return getBuildProperties(PlatformVersion.class.getClassLoader());
    }

    private static synchronized Properties getBuildProperties(final ClassLoader classLoader) throws IOException {
        Assert.requireNonNull(classLoader, "classLoader");
        try (final InputStream inputStream = classLoader.getResourceAsStream(DEFAULT_LOCATION)) {
            if (inputStream != null) {
                final Properties properties = new Properties();
                properties.load(inputStream);
                return properties;
            }
        }
        throw new RuntimeException("Can not load properties!");
    }

}
