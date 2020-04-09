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
package dev.rico.internal.remoting.server.config;

import dev.rico.core.Configuration;
import dev.rico.internal.core.Assert;
import dev.rico.internal.server.config.ConfigurationFileLoader;
import dev.rico.internal.server.config.ServerConfiguration;
import dev.rico.internal.remoting.server.event.DefaultEventBusProvider;
import org.apiguardian.api.API;

import java.io.Serializable;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * This class defines the configuration of the remoting. Normally the configuration is created based
 * on defaults and a property file (see {@link ConfigurationFileLoader}).
 */
@API(since = "0.x", status = INTERNAL)
public class RemotingConfiguration implements Serializable {

    public static final String ACTIVE = "remoting.active";

    public static final String SERVLET_MAPPING = "servletMapping";

    public static final String INTERRUPT_SERVLET_MAPPING = "interruptServletMapping";

    public static final String GARBAGE_COLLECTION_ACTIVE = "garbageCollectionActive";

    public static final String MAX_POLL_TIME = "maxPollTime";

    public static final String EVENTBUS_TYPE = "eventbusType";

    public static final boolean ACTIVE_DEFAULT_VALUE = true;

    public final static String SERVLET_MAPPING_DEFAULT_VALUE = "/remoting";

    public final static String INTERRUPT_SERVLET_MAPPING_DEFAULT_VALUE = "/remoting-interrupt";

    public final static String EVENTBUS_TYPE_DEFAULT_VALUE = DefaultEventBusProvider.DEFAULT_EVENTBUS_NAME;

    public final static long MAX_POLL_TIME_DEFAULT_VALUE = 5000;

    public final static boolean USE_GC_DEFAULT_VALUE = true;

    private final Configuration configuration;

    public RemotingConfiguration() {
        this(new ServerConfiguration());
    }

    public RemotingConfiguration(final Configuration configuration) {
        this.configuration = Assert.requireNonNull(configuration, "configuration");
    }

    public String getServletMapping() {
        return configuration.getProperty(SERVLET_MAPPING, SERVLET_MAPPING_DEFAULT_VALUE);
    }

    public String getInterruptServletMapping() {
        return configuration.getProperty(INTERRUPT_SERVLET_MAPPING, INTERRUPT_SERVLET_MAPPING_DEFAULT_VALUE);
    }

    public long getMaxPollTime() {
        return configuration.getLongProperty(MAX_POLL_TIME, MAX_POLL_TIME_DEFAULT_VALUE);
    }

    public boolean isUseGc() {
        return configuration.getBooleanProperty(GARBAGE_COLLECTION_ACTIVE, USE_GC_DEFAULT_VALUE);
    }

    public String getEventbusType() {
        return configuration.getProperty(EVENTBUS_TYPE, EVENTBUS_TYPE_DEFAULT_VALUE);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public boolean isRemotingActive() {
        return configuration.getBooleanProperty(ACTIVE, ACTIVE_DEFAULT_VALUE);
    }
}

