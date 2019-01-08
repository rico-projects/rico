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

import dev.rico.internal.metrics.MetricsImpl;
import dev.rico.internal.server.context.ContextServerUtil;
import dev.rico.core.context.Context;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RequestMetricsFilter implements Filter {

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final long startTime = System.currentTimeMillis();
        try {
            chain.doFilter(request, response);
        } finally {
            final long timeInMs = System.currentTimeMillis() - startTime;
            final Context methodTag = ContextServerUtil.createMethodTag((HttpServletRequest) request);
            final Context uriTag = ContextServerUtil.createUriTag((HttpServletRequest) request);
            final Context contextPathTag = ContextServerUtil.createContextPathTag((HttpServletRequest) request);
            final Context portTag = ContextServerUtil.createPortTag((HttpServletRequest) request);
            MetricsImpl.getInstance()
                    .getOrCreateTimer("request", contextPathTag, uriTag, methodTag, portTag)
                    .record(timeInMs, TimeUnit.MILLISECONDS);
            MetricsImpl.getInstance()
                    .getOrCreateCounter("requestCounter", contextPathTag, uriTag, methodTag, portTag)
                    .increment();
        }
    }

    @Override
    public void destroy() {}
}
