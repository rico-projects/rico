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
package dev.rico.internal.security.server;

import dev.rico.core.http.RequestMethod;
import dev.rico.core.logging.Logger;
import dev.rico.core.logging.LoggerFactory;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.http.HttpClientConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static dev.rico.internal.core.http.HttpStatus.HTTP_OK;
import static dev.rico.internal.core.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static dev.rico.internal.security.SecurityConstants.AUTHORIZATION_HEADER;
import static dev.rico.internal.security.SecurityConstants.REALM_NAME_HEADER;

public class KeycloakLogoutServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakTokenServlet.class);

    private final KeycloakConfiguration configuration;

    public KeycloakLogoutServlet(final KeycloakConfiguration configuration) {
        this.configuration = Assert.requireNonNull(configuration, "configuration");
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            LOG.debug("Logout endpoint called");
            final String realmName = Optional.ofNullable(req.getHeader(REALM_NAME_HEADER)).orElse(configuration.getRealmName());
            final String authEndPoint = configuration.getAuthEndpoint();
            final URI url = new URI(authEndPoint + "/realms/" + realmName + "/protocol/openid-connect/logout");
            LOG.debug("Calling Keycloak logout for realm '{}'", realmName);
            final HttpClientConnection clientConnection = new HttpClientConnection(url, RequestMethod.GET);
            clientConnection.addRequestHeader(AUTHORIZATION_HEADER, req.getHeader(AUTHORIZATION_HEADER));
            final int responseCode = clientConnection.readResponseCode();
            if (responseCode != HTTP_OK) {
                LOG.debug("Error in logout!");
                throw new RuntimeException("Error in logout!");
            }
            LOG.debug("Logout done");
        } catch (final Exception e) {
            resp.sendError(SC_INTERNAL_SERVER_ERROR, "Error in logout!");
        }
    }
}

