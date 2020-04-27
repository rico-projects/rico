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
package dev.rico.internal.server.servlet;

import dev.rico.internal.core.Assert;
import dev.rico.internal.core.RicoConstants;
import dev.rico.core.Configuration;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.ACCESS_CONTROL_ALLOW_CREDENTIALS_DEFAULT_VALUE;
import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.ACCESS_CONTROL_ALLOW_HEADERS;
import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.ACCESS_CONTROL_ALLOW_HEADERS_DEFAULT_VALUE;
import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.ACCESS_CONTROL_ALLOW_METHODS;
import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.ACCESS_CONTROL_ALLOW_METHODS_DEFAULT_VALUE;
import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.ACCESS_CONTROL_MAXAGE;
import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.ACCESS_CONTROL_MAX_AGE_DEFAULT_VALUE;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class CrossSiteOriginFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(CrossSiteOriginFilter.class);

    private final Configuration configuration;

    public CrossSiteOriginFilter(final Configuration configuration){
        this.configuration = Assert.requireNonNull(configuration, "configuration");
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        //Nothing to do here
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse resp = (HttpServletResponse) response;

        if(LOG.isTraceEnabled()) {
            LOG.trace("Received Request for {} of type {} with headers {}", req.getRequestURL(), req.getMethod(), req.getHeaderNames());
        }

        //Access-Control-Allow-Headers
        String accessControlAllowHeaders = RicoConstants.CLIENT_ID_HTTP_HEADER_NAME;
        final String headerValues = getAsCommaSeparatedList(configuration.getListProperty(ACCESS_CONTROL_ALLOW_HEADERS, ACCESS_CONTROL_ALLOW_HEADERS_DEFAULT_VALUE));
        if(!headerValues.isEmpty()){
            accessControlAllowHeaders = accessControlAllowHeaders + ", " + headerValues;
        }

        //Access-Control-Allow-Methods
        final String allowedMethods = getAsCommaSeparatedList(configuration.getListProperty(ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_ALLOW_METHODS_DEFAULT_VALUE));


        final String clientOrigin = req.getHeader("origin");
        resp.setHeader("Access-Control-Allow-Origin", clientOrigin);
        if(!allowedMethods.isEmpty()){
            resp.setHeader("Access-Control-Allow-Methods", allowedMethods);
        }
        resp.setHeader("Access-Control-Allow-Headers", accessControlAllowHeaders);
        resp.setHeader("Access-Control-Expose-Headers", RicoConstants.CLIENT_ID_HTTP_HEADER_NAME);
        resp.setHeader("Access-Control-Allow-Credentials", "" + configuration.getBooleanProperty(ACCESS_CONTROL_ALLOW_CREDENTIALS, ACCESS_CONTROL_ALLOW_CREDENTIALS_DEFAULT_VALUE));
        resp.setHeader("Access-Control-Max-Age", "" + configuration.getLongProperty(ACCESS_CONTROL_MAXAGE, ACCESS_CONTROL_MAX_AGE_DEFAULT_VALUE));

        chain.doFilter(request, response);
    }

    public String getAsCommaSeparatedList(final List<String> headers) {
        Assert.requireNonNull(headers, "headers");
        final StringBuilder values = new StringBuilder("");
        if (headers.size() > 0) {
            for (int i = 0; i < headers.size(); i++) {
                values.append(headers.get(i));
                if (i < headers.size() - 1) {
                    values.append(",");
                }
            }
        }
        return values.toString();
    }

    @Override
    public void destroy() {
        //Nothing to do here
    }

}
