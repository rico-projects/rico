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

import dev.rico.core.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class SimpleConfiguration implements Configuration {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleConfiguration.class);

    private final Properties internalProperties;

    public SimpleConfiguration() {
        this(new Properties());
    }

    public SimpleConfiguration(final Properties internalProperties) {
        this.internalProperties = Assert.requireNonNull(internalProperties, "internalProperties");
    }

    protected Properties getInternalProperties() {
        return internalProperties;
    }

    public boolean containsProperty(final String key) {
        return internalProperties.containsKey(key);
    }

    public boolean getBooleanProperty(final String key, final boolean defaultValue) {
        return Boolean.parseBoolean(getProperty(key, defaultValue + ""));
    }

    public boolean getBooleanProperty(final String key) {
        return getBooleanProperty(key, false);
    }

    public int getIntProperty(final String key, final int defaultValue) {
        return Integer.parseInt(getProperty(key, defaultValue + ""));
    }

    public long getLongProperty(final String key, final long defaultValue) {
        return Long.parseLong(getProperty(key, defaultValue + ""));
    }

    public List<String> getListProperty(final String key) {
        return getListProperty(key, Collections.<String>emptyList());
    }

    public List<String> getListProperty(final String key, final List<String> defaultValues) {
        final String value = getProperty(key);
        if (value != null) {
            return Arrays.asList(value.split(","));
        }
        return defaultValues;
    }

    public String getProperty(final String key, final String defaultValue) {
        return internalProperties.getProperty(key, defaultValue);
    }

    public String getProperty(final String key) {
        return internalProperties.getProperty(key);
    }

    public Set<String> getPropertyKeys() {
        final Set<String> ret = new HashSet<>();
        for (final Object key : internalProperties.keySet()) {
            if (key != null) {
                ret.add(key.toString());
            }
        }
        return ret;
    }

    public void setIntProperty(final String key, final int value) {
        setProperty(key, Integer.toString(value));
    }

    public void setLongProperty(final String key, final long value) {
        setProperty(key, Long.toString(value));
    }

    public void setBooleanProperty(final String key, final boolean value) {
        setProperty(key, Boolean.toString(value));
    }

    public void setListProperty(final String key, final List<String> values) {
        if (values == null) {
            setProperty(key, null);
        } else if (values.isEmpty()) {
            setProperty(key, "");
        } else {
            final StringBuilder builder = new StringBuilder();
            for (final String value : values) {
                builder.append(value + ", ");
            }
            builder.setLength(builder.length() - 2);
            setProperty(key, builder.toString());
        }
    }

    public void setProperty(final String key, final String value) {
        if (value == null) {
            LOG.warn("Setting property '{}' to null value will be ignored.");
        } else {
            internalProperties.setProperty(key, value);
        }
    }

    public void log() {
        if(LOG.isDebugEnabled()) {
            final Set<Map.Entry<Object, Object>> properties = internalProperties.entrySet();
            for (final Map.Entry property : properties) {
                LOG.debug("Configuration property: '" + property.getKey() + "' = '" + property.getValue() + "'");
            }
        }
    }
}
