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
package dev.rico.internal.server.security;

import dev.rico.internal.core.Assert;
import dev.rico.server.security.SecurityException;
import org.apiguardian.api.API;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static dev.rico.internal.security.SecurityConstants.APPLICATION_NAME_HEADER;
import static dev.rico.internal.security.SecurityConstants.BEARER_ONLY_HEADER;
import static dev.rico.internal.security.SecurityConstants.REALM_NAME_HEADER;
import static dev.rico.internal.server.security.SecurityServerConstants.REALMS_PROPERTY_NAME;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.19.0", status = INTERNAL)
public class KeycloakConfigResolverImpl implements KeycloakConfigResolver {

    private final static Logger LOG = LoggerFactory.getLogger(KeycloakConfigResolverImpl.class);

    private static KeycloakConfiguration configuration;

    public KeycloakDeployment resolve(final HttpFacade.Request request) {
        Assert.requireNonNull(request, "request");

        final String realmName = Optional.ofNullable(request.getHeader(REALM_NAME_HEADER)).
                orElse(configuration.getRealmName());
        final String applicationName = Optional.ofNullable(request.getHeader(APPLICATION_NAME_HEADER)).
                orElse(configuration.getApplicationName());
        final String authEndPoint = configuration.getAuthEndpoint();
        final boolean cors = configuration.isCors();

        Optional.ofNullable(realmName).orElseThrow(() -> new SecurityException("Realm name for security check is not configured!"));
        Optional.ofNullable(applicationName).orElseThrow(() -> new SecurityException("Application name for security check is not configured!"));
        Optional.ofNullable(authEndPoint).orElseThrow(() -> new SecurityException("Auth endpoint for security check is not configured!"));

        LOG.debug("Defined Keycloak AdapterConfig for request against realm '" +realmName + "' and app '" + applicationName + "'");

        final AdapterConfig adapterConfig = new AdapterConfig();
        LOG.debug("Checking if realm '" +realmName + "' is allowed");
        if(configuration.isRealmAllowed(realmName)){
            if(LOG.isTraceEnabled()) {
                LOG.trace("Realm '" + realmName + "' is allowed");
            }
            adapterConfig.setRealm(realmName);
        }else{
            if(LOG.isDebugEnabled()) {
                    final String allowedRealms = configuration.getRealmNames().stream().reduce("", (a, b) -> a + "," + b);
                    LOG.debug("Realm '" + realmName + "' is not allowed! Allowed realms are {}", allowedRealms);
            }
            throw new SecurityException("Access Denied! The given realm is not in the allowed realms.");
        }

        adapterConfig.setResource(applicationName);
        adapterConfig.setAuthServerUrl(authEndPoint);
        adapterConfig.setCors(cors);

        Optional.ofNullable(request.getHeader(BEARER_ONLY_HEADER)).
                ifPresent(v -> adapterConfig.setBearerOnly(true));

        return KeycloakDeploymentBuilder.build(adapterConfig);
    }

    public static void setConfiguration(final KeycloakConfiguration configuration) {
        Assert.requireNonNull(configuration, "configuration");
        KeycloakConfigResolverImpl.configuration = configuration;

        LOG.debug("Configuration for keycloak resolver defined");
        if(LOG.isTraceEnabled()) {
            if(configuration.isRealmCheckEnabled()) {
                final String allowedRealms = configuration.getRealmNames().stream().reduce("", (a, b) -> a + "," + b);
                LOG.trace("Allowed keycloak realms: {}", allowedRealms);
            } else {
                LOG.trace("Any keycloak realm is allowed. Property '" + REALMS_PROPERTY_NAME + "' won't be used!");
            }
        }
    }
}
