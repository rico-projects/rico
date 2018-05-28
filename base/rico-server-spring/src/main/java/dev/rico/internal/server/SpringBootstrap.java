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
package dev.rico.internal.server;

import dev.rico.internal.server.bootstrap.PlatformBootstrap;
import dev.rico.internal.server.remoting.config.ConfigurationFileLoader;
import dev.rico.internal.server.remoting.config.ServerConfiguration;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import java.util.Optional;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Basic Bootstrap for Spring based application. The bootstrap automatically starts Rico.
 *
 * @author Hendrik Ebbers
 */
@Configuration
@API(since = "0.x", status = INTERNAL)
public class SpringBootstrap implements ServletContextInitializer, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(SpringBootstrap.class);

    private static final String PREFIX = "rico.";

    @Autowired
    private Environment environment;

    @Autowired(required = false)
    private ServerConfiguration injectedConfig;

    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        final ServerConfiguration configuration = Optional.ofNullable(injectedConfig)
                .orElse(ConfigurationFileLoader.loadConfiguration());
        updateConfigurationBySpring(configuration);
        final PlatformBootstrap bootstrap = new PlatformBootstrap();
        bootstrap.init(servletContext, configuration);
    }

    private void updateConfigurationBySpring(final ServerConfiguration configuration) {
        for(final String key : configuration.getPropertyKeys()) {
            final String valInSpringConfig = environment.getProperty(PREFIX + key);
            if(valInSpringConfig != null) {
                LOG.debug("Rico property '{}' found in spring configuration", key);
                configuration.setProperty(key, valInSpringConfig);
            }
        }
    }

    private static ApplicationContext ctx = null;

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }

    public void setApplicationContext(final ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }
}
