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
package dev.rico.internal.server.bootstrap.modules;

import dev.rico.internal.core.Assert;
import dev.rico.internal.server.bootstrap.AbstractBaseModule;
import dev.rico.internal.server.servlet.HttpSessionMutexHolder;
import dev.rico.server.spi.ModuleDefinition;
import dev.rico.server.spi.ServerCoreComponents;
import org.apiguardian.api.API;

import javax.servlet.ServletContext;

import static dev.rico.internal.server.bootstrap.modules.HttpMutexModule.HTTP_MUTEX_MODULE;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
@ModuleDefinition(name = HTTP_MUTEX_MODULE)
public class HttpMutexModule extends AbstractBaseModule {

    public static final String HTTP_MUTEX_MODULE = "HttpMutexModule";

    public static final String HTTP_MUTEX_MODULE_ACTIVE = "httpMutexModuleActive";

    @Override
    protected String getActivePropertyName() {
        return HTTP_MUTEX_MODULE_ACTIVE;
    }

    @Override
    public void initialize(final ServerCoreComponents coreComponents) {
        Assert.requireNonNull(coreComponents, "coreComponents");
        final ServletContext servletContext = coreComponents.getServletContext();
        Assert.requireNonNull(servletContext, "servletContext");

        final HttpSessionMutexHolder mutexHolder = new HttpSessionMutexHolder();
        servletContext.addListener(mutexHolder);
    }
}
