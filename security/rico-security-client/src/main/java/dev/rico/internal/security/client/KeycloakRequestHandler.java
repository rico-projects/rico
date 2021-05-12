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
package dev.rico.internal.security.client;

import dev.rico.core.http.HttpURLConnectionHandler;
import dev.rico.core.logging.Logger;
import dev.rico.core.logging.LoggerFactory;
import dev.rico.internal.core.Assert;
import org.apiguardian.api.API;

import java.net.HttpURLConnection;

import static dev.rico.internal.security.SecurityConstants.APPLICATION_NAME_HEADER;
import static dev.rico.internal.security.SecurityConstants.AUTHORIZATION_HEADER;
import static dev.rico.internal.security.SecurityConstants.BEARER;
import static dev.rico.internal.security.SecurityConstants.BEARER_ONLY_HEADER;
import static dev.rico.internal.security.SecurityConstants.REALM_NAME_HEADER;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.19.0", status = INTERNAL)
public class KeycloakRequestHandler implements HttpURLConnectionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakRequestHandler.class);

    @Override
    public void handle(final HttpURLConnection connection) {
        Assert.requireNonNull(connection, "connection");
        KeycloakAuthentificationManager.getInstance().getAuthFor(connection.getURL()).ifPresent(auth -> {

            //No redirect, can not be handled in Java
            connection.setRequestProperty(BEARER_ONLY_HEADER, "true");

            final String accessToken = auth.getAccessToken();
            if (accessToken != null && !accessToken.isEmpty()) {
                LOG.debug("Adding security access token to request");
                connection.setRequestProperty(AUTHORIZATION_HEADER, BEARER + accessToken);
            }

            final String realm = auth.getRealm();
            if (realm != null && !realm.isEmpty()) {
                LOG.debug("Adding realm to request");
                connection.setRequestProperty(REALM_NAME_HEADER, realm);
            }

            final String appName = auth.getAppName();
            if (appName != null && !appName.isEmpty()) {
                LOG.debug("Adding appName to request");
                connection.setRequestProperty(APPLICATION_NAME_HEADER, appName);
            }
        });


    }
}
