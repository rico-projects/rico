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
package dev.rico.internal.server;

import dev.rico.internal.server.bootstrap.PlatformBootstrap;
import dev.rico.internal.server.remoting.config.ConfigurationFileLoader;
import dev.rico.internal.server.remoting.config.ServerConfiguration;
import org.apiguardian.api.API;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * The Boostrap for a JavaEE based application.
 *
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = INTERNAL)
public class JavaeeBootstrap implements ServletContainerInitializer {

    @Override
    public void onStartup(final Set<Class<?>> c, final ServletContext servletContext) throws ServletException {
        final ServerConfiguration configuration = ConfigurationFileLoader.loadConfiguration();
        final PlatformBootstrap bootstrap = new PlatformBootstrap();
        bootstrap.init(servletContext, configuration);
    }
}
