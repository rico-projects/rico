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
package dev.rico.internal.metrics.server.servlet;

import dev.rico.core.logging.Logger;
import dev.rico.core.logging.LoggerFactory;
import dev.rico.internal.core.Assert;
import dev.rico.internal.metrics.MetricsImpl;
import dev.rico.metrics.types.Gauge;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class MetricsServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsServlet.class);

    private final PrometheusMeterRegistry prometheusRegistry;

    public MetricsServlet(final PrometheusMeterRegistry prometheusRegistry) {
        this.prometheusRegistry = Assert.requireNonNull(prometheusRegistry, "prometheusRegistry");
    }

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        LOG.debug("Metrics servlet called");

        final Gauge upTimeGauge = MetricsImpl.getInstance().getOrCreateGauge("process_uptime_seconds");
        upTimeGauge.setValue(ManagementFactory.getRuntimeMXBean().getUptime() / 1000);

        final String response = prometheusRegistry.scrape();
        resp.getOutputStream().write(response.getBytes());
    }
}
