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

import dev.rico.core.Configuration;
import dev.rico.internal.core.Assert;
import dev.rico.internal.server.servlet.CrossSiteOriginFilter;
import dev.rico.internal.server.bootstrap.AbstractBaseModule;
import dev.rico.server.spi.ModuleDefinition;
import dev.rico.server.spi.ModuleInitializationException;
import dev.rico.server.spi.ServerCoreComponents;
import org.apiguardian.api.API;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.EnumSet;
import java.util.List;

import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.CORS_ENDPOINTS_URL_MAPPINGS;
import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.CORS_ENDPOINTS_URL_MAPPINGS_DEFAULT_VALUE;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
@ModuleDefinition
public class CorsModule extends AbstractBaseModule {

    public static final String CORS_MODULE = "CorsModule";

    public static final String CORS_FILTER = "CorsFilter";

    public static final String CORS_MODULE_ACTIVE = "corsActive";

    @Override
    protected String getActivePropertyName() {
        return CORS_MODULE_ACTIVE;
    }

    @Override
    public String getName() {
        return CORS_MODULE;
    }

    @Override
    public void initialize(final ServerCoreComponents coreComponents) throws ModuleInitializationException {
        Assert.requireNonNull(coreComponents, "coreComponents");
        final ServletContext servletContext = coreComponents.getInstance(ServletContext.class);
        final Configuration configuration = coreComponents.getConfiguration();
        final List<String> endpointList = configuration.getListProperty(CORS_ENDPOINTS_URL_MAPPINGS, CORS_ENDPOINTS_URL_MAPPINGS_DEFAULT_VALUE);

        final String[] endpoints = endpointList.toArray(new String[endpointList.size()]);
        final CrossSiteOriginFilter filter = new CrossSiteOriginFilter(configuration);
        final FilterRegistration.Dynamic createdFilter = servletContext.addFilter(CORS_FILTER, filter);
        createdFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, endpoints);
    }
}
