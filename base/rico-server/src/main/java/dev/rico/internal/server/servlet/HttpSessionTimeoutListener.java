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
package dev.rico.internal.server.servlet;

import dev.rico.core.Configuration;
import dev.rico.internal.core.Assert;

import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.SESSION_TIMEOUT;
import static dev.rico.internal.server.bootstrap.BasicConfigurationProvider.SESSION_TIMEOUT_DEFAULT_VALUE;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class HttpSessionTimeoutListener implements HttpSessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(HttpSessionTimeoutListener.class);

    private final int sessionTimeoutInSeconds;

    public HttpSessionTimeoutListener(final Configuration configuration) {
        this.sessionTimeoutInSeconds = configuration.getIntProperty(SESSION_TIMEOUT, SESSION_TIMEOUT_DEFAULT_VALUE);
    }

    @Override
    public void sessionCreated(final HttpSessionEvent sessionEvent) {
        Assert.requireNonNull(sessionEvent, "sessionEvent");
        try {
            sessionEvent.getSession().setMaxInactiveInterval(sessionTimeoutInSeconds);
        } catch (Exception e) {
            LOG.warn("Can not set the defined session timeout!", e);
        }
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent se) {}
}
