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
import dev.rico.internal.server.servlet.HttpSessionTimeoutListener;
import dev.rico.internal.server.bootstrap.AbstractBaseModule;
import dev.rico.server.spi.ModuleDefinition;
import dev.rico.server.spi.ModuleInitializationException;
import dev.rico.core.Configuration;
import dev.rico.server.spi.ServerCoreComponents;
import org.apiguardian.api.API;

import javax.servlet.ServletContext;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
@ModuleDefinition
public class HttpTimeoutModule extends AbstractBaseModule {

    public static final String HTTP_TIMEOUT_MODULE = "HttpTimeoutModule";

    public static final String HTTP_TIMEOUT_ACTIVE = "httpTimeoutActive";

    @Override
    protected String getActivePropertyName() {
        return HTTP_TIMEOUT_ACTIVE;
    }

    @Override
    public String getName() {
        return HTTP_TIMEOUT_MODULE;
    }

    @Override
    public void initialize(final ServerCoreComponents coreComponents) throws ModuleInitializationException {
        Assert.requireNonNull(coreComponents, "coreComponents");
        final ServletContext servletContext = coreComponents.getInstance(ServletContext.class);
        final Configuration configuration = coreComponents.getConfiguration();

        HttpSessionTimeoutListener sessionCleaner = new HttpSessionTimeoutListener(configuration);
        servletContext.addListener(sessionCleaner);
    }
}
