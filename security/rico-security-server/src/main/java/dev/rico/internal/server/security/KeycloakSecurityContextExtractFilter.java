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
import dev.rico.internal.core.context.ContextManagerImpl;
import dev.rico.core.functional.Subscription;
import org.apiguardian.api.API;
import org.keycloak.KeycloakSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static dev.rico.internal.security.SecurityConstants.USER_CONTEXT;
import static dev.rico.internal.security.SecurityConstants.APPLICATION_NAME_HEADER;
import static dev.rico.internal.security.SecurityConstants.REALM_NAME_HEADER;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.19.0", status = INTERNAL)
public class KeycloakSecurityContextExtractFilter implements Filter, AccessDeniedCallback {

    private final static Logger LOG = LoggerFactory.getLogger(KeycloakSecurityContextExtractFilter.class);

    private final ThreadLocal<KeycloakSecurityContext> contextHolder = new ThreadLocal<>();

    private final ThreadLocal<Boolean> accessDenied = new ThreadLocal<>();

    private final ThreadLocal<String> realmHolder = new ThreadLocal<>();

    private final ThreadLocal<String> appNameHolder = new ThreadLocal<>();

    private final KeyCloakSecurityExtractor keyCloakSecurityExtractor = new KeyCloakSecurityExtractor();

    public void init(final FilterConfig filterConfig) throws ServletException {}

    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        Assert.requireNonNull(chain, "chain");
        final KeycloakSecurityContext securityContext = keyCloakSecurityExtractor.extractContext(request);
        contextHolder.set(securityContext);
        realmHolder.set(req.getHeader(REALM_NAME_HEADER));
        appNameHolder.set(req.getHeader(APPLICATION_NAME_HEADER));
        accessDenied.set(false);

        final Subscription userContextSubscription = Optional.ofNullable(securityContext)
                .map(c -> c.getToken())
                .map(t -> t.getPreferredUsername())
                .map(u -> ContextManagerImpl.getInstance().addThreadContext(USER_CONTEXT, u))
                .orElse(null);
        try {
            chain.doFilter(request, response);
        }catch (Exception e) {
            if(!accessDenied.get()) {
                throw e;
            } else {
                LOG.error("SecurityContext error in request", e);
            }
        } finally {
            Optional.ofNullable(userContextSubscription).ifPresent(s -> s.unsubscribe());
            contextHolder.set(null);
            boolean sendAccessDenied = accessDenied.get();
            accessDenied.set(false);
            if(sendAccessDenied) {
                ((HttpServletResponse)response).sendError(403, "Access Denied");
            }
        }
    }

    public void destroy() {}

    public SecurityContextKeycloakImpl getSecurity() {
        return new SecurityContextKeycloakImpl(contextHolder.get(), this);
    }

    public Optional<String> token() {
        return Optional.ofNullable(contextHolder.get()).map(c -> c.getTokenString());
    }

    @Override
    public void onAccessDenied() {
        accessDenied.set(true);
    }

    public Optional<String> realm() {
        return Optional.ofNullable(realmHolder.get());
    }

    public Optional<String> appName() {
        return Optional.ofNullable(appNameHolder.get());
    }
}
