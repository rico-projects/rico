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
package dev.rico.internal.metrics.server.module;

import dev.rico.core.Configuration;
import dev.rico.internal.metrics.MetricsImpl;
import dev.rico.internal.metrics.server.servlet.MetricsHttpSessionListener;
import dev.rico.internal.metrics.server.servlet.MetricsServlet;
import dev.rico.internal.metrics.server.servlet.RequestMetricsFilter;
import dev.rico.internal.server.bootstrap.AbstractBaseModule;
import dev.rico.metrics.spi.MetricsBinderProvider;
import dev.rico.server.spi.ModuleDefinition;
import dev.rico.server.spi.ServerCoreComponents;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.apiguardian.api.API;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import java.util.EnumSet;
import java.util.ServiceLoader;

import static dev.rico.internal.metrics.server.module.MetricsConfigConstants.METRICS_ACTIVE_PROPERTY;
import static dev.rico.internal.metrics.server.module.MetricsConfigConstants.METRICS_ENDPOINT_PROPERTY;
import static dev.rico.internal.metrics.server.module.MetricsConfigConstants.METRICS_SERVLET_FILTER_NAME;
import static dev.rico.internal.metrics.server.module.MetricsConfigConstants.METRICS_SERVLET_NAME;
import static dev.rico.internal.metrics.server.module.MetricsConfigConstants.MODULE_NAME;
import static dev.rico.internal.server.servlet.ServletConstants.ALL_URL_MAPPING;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "1.0.0", status = INTERNAL)
@ModuleDefinition(name = MODULE_NAME)
public class MetricsModule extends AbstractBaseModule {

    @Override
    protected String getActivePropertyName() {
        return METRICS_ACTIVE_PROPERTY;
    }

    @Override
    public void initialize(final ServerCoreComponents coreComponents) {
        final Configuration configuration = coreComponents.getConfiguration();
        final ServletContext servletContext = coreComponents.getServletContext();

        final PrometheusMeterRegistry prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        
        servletContext.addFilter(METRICS_SERVLET_FILTER_NAME, new RequestMetricsFilter())
                .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, ALL_URL_MAPPING);

        servletContext.addListener(new MetricsHttpSessionListener());

        servletContext.addServlet(METRICS_SERVLET_NAME, new MetricsServlet(prometheusRegistry))
                .addMapping(configuration.getProperty(METRICS_ENDPOINT_PROPERTY));

        MetricsImpl.getInstance().init(prometheusRegistry);

        ServiceLoader.load(MetricsBinderProvider.class)
                .stream()
                .map(provider -> provider.get())
                .filter(provider -> provider.isActive(configuration))
                .map(provider -> provider.createBinder(configuration))
                .forEach(binder -> MetricsImpl.getInstance().initializeBinder(binder));
    }
}
