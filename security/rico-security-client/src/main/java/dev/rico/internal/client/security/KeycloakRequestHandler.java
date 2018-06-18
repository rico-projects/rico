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
package dev.rico.internal.client.security;

import dev.rico.core.http.RequestChain;
import dev.rico.internal.core.Assert;
import dev.rico.core.http.HttpURLConnectionInterceptor;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;

import static dev.rico.internal.security.SecurityConstants.APPLICATION_NAME_HEADER;
import static dev.rico.internal.security.SecurityConstants.AUTHORIZATION_HEADER;
import static dev.rico.internal.security.SecurityConstants.BEARER;
import static dev.rico.internal.security.SecurityConstants.BEARER_ONLY_HEADER;
import static dev.rico.internal.security.SecurityConstants.REALM_NAME_HEADER;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.19.0", status = INTERNAL)
public class KeycloakRequestHandler implements HttpURLConnectionInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakRequestHandler.class);

    @Override
    public void handle(final HttpURLConnection connection, RequestChain chain) throws IOException {
        Assert.requireNonNull(connection, "connection");
        Assert.requireNonNull(chain, "chain");

        KeycloakAuthentificationManager.getInstance().getAuthFor(connection.getURL()).ifPresent(auth -> {

            //No redirect, can not be handled in Java
            connection.setRequestProperty(BEARER_ONLY_HEADER, "true");

            final String accessToken = auth.getAccessToken();
            if(accessToken != null && !accessToken.isEmpty()) {
                LOG.debug("Adding security access token to request");
                connection.setRequestProperty(AUTHORIZATION_HEADER, BEARER + accessToken);
            }

            final String realm = auth.getRealm();
            if(realm != null && !realm.isEmpty()) {
                LOG.debug("Adding realm to request");
                connection.setRequestProperty(REALM_NAME_HEADER, realm);
            }

            final String appName = auth.getAppName();
            if(appName != null && !appName.isEmpty()) {
                LOG.debug("Adding appName to request");
                connection.setRequestProperty(APPLICATION_NAME_HEADER, appName);
            }
        });
        chain.call();

    }
}
