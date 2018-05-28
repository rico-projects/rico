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
package dev.rico.internal.server.bootstrap.modules;

import dev.rico.internal.core.Assert;
import dev.rico.internal.server.remoting.servlet.ServerTimingFilter;
import dev.rico.core.Configuration;
import dev.rico.internal.server.bootstrap.AbstractBaseModule;
import dev.rico.server.spi.ModuleDefinition;
import dev.rico.server.spi.ModuleInitializationException;
import dev.rico.server.spi.ServerCoreComponents;
import org.apiguardian.api.API;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.EnumSet;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "1.0.0-RC5", status = INTERNAL)
@ModuleDefinition(order = 10)
public class ServerTimingModule extends AbstractBaseModule {

    public static final String MODULE_NAME = "serverTimingModule";

    public static final String FILTER_NAME = "ServerTimingFilter";

    public static final String SERVER_TIMING_MODULE_ACTIVE_PROPERTY_NAME = "timingActive";

    @Override
    protected String getActivePropertyName() {
        return SERVER_TIMING_MODULE_ACTIVE_PROPERTY_NAME;
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public void initialize(final ServerCoreComponents coreComponents) throws ModuleInitializationException {
        Assert.requireNonNull(coreComponents, "coreComponents");
        final ServletContext servletContext = coreComponents.getInstance(ServletContext.class);
        final Configuration configuration = coreComponents.getConfiguration();

        final Filter filter = new ServerTimingFilter(true);
        final FilterRegistration.Dynamic createdFilter = servletContext.addFilter(FILTER_NAME, filter);
        createdFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }
}
