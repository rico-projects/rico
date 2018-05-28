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
package dev.rico.internal.server.security;

import dev.rico.internal.core.Assert;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.function.Supplier;

import static dev.rico.internal.security.SecurityConstants.APPLICATION_NAME_HEADER;
import static dev.rico.internal.security.SecurityConstants.AUTHORIZATION_HEADER;
import static dev.rico.internal.security.SecurityConstants.BEARER;
import static dev.rico.internal.security.SecurityConstants.REALM_NAME_HEADER;

public class SecurityClientRequestFactory extends HttpComponentsClientHttpRequestFactory implements ClientHttpRequestFactory {

    private final static Logger LOG = LoggerFactory.getLogger(SecurityClientRequestFactory.class);

    private final Supplier<String> securityTokenSupplier;

    private final Supplier<String> realmSupplier;

    private final Supplier<String> appNameSupplier;

    public SecurityClientRequestFactory(final Supplier<String> securityTokenSupplier, final Supplier<String> realmSupplier, final Supplier<String> appNameSupplier) {
        super(HttpClients.custom()
                .disableCookieManagement()
                .build()
        );
        this.securityTokenSupplier = Assert.requireNonNull(securityTokenSupplier, "securityTokenSupplier");
        this.realmSupplier = Assert.requireNonNull(realmSupplier, "realmSupplier");
        this.appNameSupplier = Assert.requireNonNull(appNameSupplier, "appNameSupplier");
    }

    @Override
    protected void postProcessHttpRequest(HttpUriRequest request) {
        final String token = securityTokenSupplier.get();
        if(token != null && !token.isEmpty() && !request.containsHeader(AUTHORIZATION_HEADER)) {
            LOG.debug("adding auth header");
            request.setHeader(AUTHORIZATION_HEADER, BEARER + token);
        }

        final String realm = realmSupplier.get();
        if(realm != null && !realm.isEmpty() && !request.containsHeader(REALM_NAME_HEADER)) {
            LOG.debug("adding realm header");
            request.setHeader(REALM_NAME_HEADER, realm);
        }

        final String appName = appNameSupplier.get();
        if(appName != null && !appName.isEmpty() && !request.containsHeader(APPLICATION_NAME_HEADER)) {
            LOG.debug("adding app name header");
            request.setHeader(APPLICATION_NAME_HEADER, appName);
        }

    }
}
