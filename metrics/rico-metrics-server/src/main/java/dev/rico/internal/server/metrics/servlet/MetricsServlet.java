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
package dev.rico.internal.server.metrics.servlet;

import dev.rico.internal.core.Assert;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MetricsServlet extends HttpServlet {

    private final static Logger LOG = LoggerFactory.getLogger(MetricsServlet.class);

    private final PrometheusMeterRegistry prometheusRegistry;

    public MetricsServlet(final PrometheusMeterRegistry prometheusRegistry) {
        this.prometheusRegistry = Assert.requireNonNull(prometheusRegistry, "prometheusRegistry");
    }

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        LOG.debug("Metrics servlet called");
        final String response = prometheusRegistry.scrape();
        resp.getOutputStream().write(response.getBytes());
    }
}
