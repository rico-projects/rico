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

import dev.rico.internal.core.Assert;
import dev.rico.internal.core.http.ConnectionUtils;
import dev.rico.internal.core.http.HttpClientConnection;
import dev.rico.core.http.RequestMethod;
import dev.rico.security.server.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static dev.rico.internal.core.http.HttpHeaderConstants.CHARSET;
import static dev.rico.internal.core.http.HttpHeaderConstants.CHARSET_HEADER;
import static dev.rico.internal.core.http.HttpHeaderConstants.CONTENT_TYPE_HEADER;
import static dev.rico.internal.core.http.HttpHeaderConstants.FORM_MIME_TYPE;
import static dev.rico.internal.core.http.HttpStatus.SC_HTTP_UNAUTHORIZED;
import static dev.rico.internal.security.SecurityConstants.APPLICATION_NAME_HEADER;
import static dev.rico.internal.security.SecurityConstants.REALM_NAME_HEADER;

public class KeycloakTokenServlet extends HttpServlet {

    private final static Logger LOG = LoggerFactory.getLogger(KeycloakTokenServlet.class);

    private final KeycloakConfiguration configuration;

    public KeycloakTokenServlet(final KeycloakConfiguration configuration) {
        this.configuration = Assert.requireNonNull(configuration, "configuration");
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            LOG.debug("open-id endpoint called");
            final String realmName = Optional.ofNullable(req.getHeader(REALM_NAME_HEADER)).orElse(configuration.getRealmName());
            final String appName = Optional.ofNullable(req.getHeader(APPLICATION_NAME_HEADER)).orElse(configuration.getApplicationName());
            final String authEndPoint = configuration.getAuthEndpoint();
            final String content = ConnectionUtils.readUTF8Content(req.getInputStream()) + "&client_id=" + appName;


            if(configuration.isRealmAllowed(realmName)){
                LOG.debug("Calling Keycloak");
                final URI url = new URI(authEndPoint + "/realms/" + realmName + "/protocol/openid-connect/token");
                final HttpClientConnection clientConnection = new HttpClientConnection(url, RequestMethod.POST);
                clientConnection.addRequestHeader(CONTENT_TYPE_HEADER, FORM_MIME_TYPE);
                clientConnection.addRequestHeader(CHARSET_HEADER, CHARSET);
                clientConnection.writeRequestContent(content);
                final int responseCode = clientConnection.readResponseCode();
                if(responseCode == SC_HTTP_UNAUTHORIZED) {
                    LOG.debug("Invalid login!");
                    throw new RuntimeException("Invalid login!");
                }
                LOG.debug("sending auth token to client");
                final byte[] responseContent = clientConnection.readResponseContent();
                ConnectionUtils.writeContent(resp.getOutputStream(), responseContent);
            }else{
                if(LOG.isDebugEnabled()) {
                    final String allowedRealms = configuration.getRealmNames().stream().reduce("", (a, b) -> a + "," + b);
                    LOG.debug("Realm '" + realmName + "' is not allowed! Allowed realms are {}", allowedRealms);
                }
                throw new SecurityException("Access Denied! The given realm is not in the allowed realms.");
            }


        } catch (final Exception e) {
            LOG.error("Error in security token handling", e);
            resp.sendError(SC_HTTP_UNAUTHORIZED, "Can not authorize");
        }
    }
}
