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

import com.google.gson.Gson;
import dev.rico.client.Client;
import dev.rico.client.ClientConfiguration;
import dev.rico.client.concurrent.BackgroundExecutor;
import dev.rico.security.client.Security;
import dev.rico.core.Configuration;
import dev.rico.core.functional.Subscription;
import dev.rico.core.http.RequestMethod;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.context.ContextManagerImpl;
import dev.rico.internal.core.http.HttpClientConnection;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static dev.rico.internal.core.http.HttpHeaderConstants.CHARSET;
import static dev.rico.internal.core.http.HttpHeaderConstants.CONTENT_TYPE_HEADER;
import static dev.rico.internal.core.http.HttpHeaderConstants.FORM_MIME_TYPE;
import static dev.rico.internal.core.http.HttpHeaderConstants.TEXT_MIME_TYPE;
import static dev.rico.internal.core.http.HttpStatus.SC_HTTP_UNAUTHORIZED;
import static dev.rico.internal.security.SecurityConstants.APPLICATION_NAME_HEADER;
import static dev.rico.internal.security.SecurityConstants.APPLICATION_PROPERTY_NAME;
import static dev.rico.internal.security.SecurityConstants.AUTH_ENDPOINT_PROPERTY_DEFAULT_VALUE;
import static dev.rico.internal.security.SecurityConstants.AUTH_ENDPOINT_PROPERTY_NAME;
import static dev.rico.internal.security.SecurityConstants.DIRECT_CONNECTION_PROPERTY_DEFAULT_VALUE;
import static dev.rico.internal.security.SecurityConstants.DIRECT_CONNECTION_PROPERTY_NAME;
import static dev.rico.internal.security.SecurityConstants.REALM_NAME_HEADER;
import static dev.rico.internal.security.SecurityConstants.REALM_PROPERTY_NAME;
import static dev.rico.internal.security.SecurityConstants.USER_CONTEXT;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.19.0", status = INTERNAL)
public class KeycloakSecurity implements Security {

    private static final long TOKEN_EXPIRES_DELTA = 10_000;

    private static final long MIN_TOKEN_EXPIRES_RUN = 30_000;

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakSecurity.class);

    private final String authEndpoint;

    private final String defaultRealmName;

    private final String defaultAppName;

    private final BackgroundExecutor executor;

    private final boolean directConnect;

    private Future<Void> refreshTask;

    private final Lock refreshLock = new ReentrantLock();

    private final AtomicBoolean authorized;

    private final AtomicReference<String> accessToken;

    private final Lock loginLogoutLock = new ReentrantLock();

    private final AtomicReference<Subscription> userContextSubscription;

    public KeycloakSecurity(final ClientConfiguration configuration, final BackgroundExecutor backgroundExecutor) {
        Assert.requireNonNull(configuration, "configuration");
        this.defaultAppName = configuration.getProperty(APPLICATION_PROPERTY_NAME);
        this.authEndpoint = configuration.getProperty(AUTH_ENDPOINT_PROPERTY_NAME, AUTH_ENDPOINT_PROPERTY_DEFAULT_VALUE);
        Assert.requireNonBlank(authEndpoint, "authEndpoint");
        this.defaultRealmName = configuration.getProperty(REALM_PROPERTY_NAME);
        this.directConnect = configuration.getBooleanProperty(DIRECT_CONNECTION_PROPERTY_NAME, DIRECT_CONNECTION_PROPERTY_DEFAULT_VALUE);
        this.executor = Assert.requireNonNull(backgroundExecutor, "backgroundExecutor");
        this.authorized = new AtomicBoolean(false);
        this.accessToken = new AtomicReference<>(null);
        userContextSubscription = new AtomicReference<>();
    }

    @Override
    public Future<Void> login(final String user, final String password) {
        return login(user, password, Configuration.empty());
    }

    @Override
    public Future<Void> login(final String user, final String password, final Configuration securityConfig) {
        Assert.requireNonNull(securityConfig, "securityConfig");
        return executor.submit(() -> {
            loginLogoutLock.lock();
            try {
                if (authorized.get()) {
                    throw new RuntimeException("Already logged in!");
                }

                final String realmName = securityConfig.getProperty(REALM_PROPERTY_NAME, defaultRealmName);
                final String appName = securityConfig.getProperty(APPLICATION_PROPERTY_NAME, defaultAppName);

                try {
                    final String encodedUser = encode(user);
                    final String encodedPassword = encode(password);
                    final String encodedAppName = encode(appName);

                    final KeycloakOpenidConnectResult connectResult = receiveTokenByLogin(encodedUser, encodedPassword, realmName, appName);
                    accessToken.set(connectResult.getAccess_token());
                    final KeycloakAuthentification auth = new KeycloakAuthentification(accessToken.get(), appName, realmName);
                    KeycloakAuthentificationManager.getInstance().setAuth(auth);
                    authorized.set(true);
                    userContextSubscription.set(ContextManagerImpl.getInstance().addGlobalContext(USER_CONTEXT, user));
                    startTokenRefreshRunner(connectResult, realmName, encodedAppName);
                } catch (final IOException | URISyntaxException e) {
                    throw new RuntimeException("Can not receive security token!", e);
                }
            } finally {
                loginLogoutLock.unlock();
            }
        }, null);
    }

    @Override
    public Future<Void> logout() {
        return executor.submit(() -> {
            loginLogoutLock.lock();
            try {
                Subscription userSubscription = userContextSubscription.getAndSet(null);
                if(userSubscription != null) {
                    userSubscription.unsubscribe();
                }
                authorized.set(false);
                refreshLock.lock();
                try {
                    refreshTask.cancel(true);
                } finally {
                    refreshLock.unlock();
                }
                accessToken.set(null);
            } finally {
                loginLogoutLock.unlock();
            }
        }, null);
    }

    @Override
    public boolean isAuthorized() {
        //REST Endpoint at Keycloak site -> Call must be done manually
        //http://www.keycloak.org/docs-api/3.3/rest-api/index.html
        //See /admin/realms/{realm}/users/{id}/sessions
        return authorized.get();
    }

    private String encode(final String value) throws UnsupportedEncodingException {
        if (value != null) {
            return URLEncoder.encode(value, CHARSET);
        } else {
            return value;
        }
    }

    private void startTokenRefreshRunner(final KeycloakOpenidConnectResult connectResult, final String realmName, final String appName) {
        refreshLock.lock();
        try {
            Assert.requireNonNull(connectResult, "connectResult");
            refreshTask = executor.submit(() -> {
                try {
                    final AtomicReference<KeycloakOpenidConnectResult> connectResultReference = new AtomicReference<>(connectResult);
                    while (!Thread.interrupted()) {
                        final KeycloakOpenidConnectResult currentConnectResult = connectResultReference.get();
                        Assert.requireNonNull(currentConnectResult, "currentConnectResult");
                        final long sleepTime = Math.max(MIN_TOKEN_EXPIRES_RUN, currentConnectResult.getExpires_in() - TOKEN_EXPIRES_DELTA);
                        Thread.sleep(sleepTime);
                        LOG.debug("Token refresh started");
                        final KeycloakOpenidConnectResult newConnectResult = receiveTokenByRefresh(currentConnectResult.getRefresh_token(), realmName, appName);
                        Assert.requireNonNull(newConnectResult, "newConnectResult");
                        accessToken.set(newConnectResult.getAccess_token());
                        final KeycloakAuthentification auth = new KeycloakAuthentification(accessToken.get(), appName, realmName);
                        KeycloakAuthentificationManager.getInstance().setAuth(auth);
                        LOG.debug("Token refresh done");
                        connectResultReference.set(newConnectResult);
                    }
                } catch (final InterruptedException e) {
                    LOG.debug("Token refresh runner stopped");
                } catch (final IOException | URISyntaxException e) {
                    throw new RuntimeException("Can not receive security token!", e);
                }
            }, null);
        } finally {
            refreshLock.unlock();
        }
    }

    private HttpClientConnection createDirectConnection(final String realmName) throws URISyntaxException, IOException {
        final URI url = new URI(authEndpoint + "/auth/realms/" + realmName + "/protocol/openid-connect/token");
        final HttpClientConnection clientConnection = new HttpClientConnection(url, RequestMethod.POST);
        clientConnection.addRequestHeader(CONTENT_TYPE_HEADER, FORM_MIME_TYPE);
        return clientConnection;
    }

    private HttpClientConnection createServerProxyConnection(final String realmName, final String appName) throws URISyntaxException, IOException {
        final URI url = new URI(authEndpoint);
        final HttpClientConnection clientConnection = new HttpClientConnection(url, RequestMethod.POST);
        clientConnection.addRequestHeader(CONTENT_TYPE_HEADER, TEXT_MIME_TYPE);
        if (realmName != null && !realmName.isEmpty()) {
            clientConnection.addRequestHeader(REALM_NAME_HEADER, realmName);
        }
        if (appName != null && !appName.isEmpty()) {
            clientConnection.addRequestHeader(APPLICATION_NAME_HEADER, appName);
        }
        return clientConnection;
    }

    private KeycloakOpenidConnectResult receiveTokenByLogin(final String user, final String password, final String realmName, final String appName) throws IOException, URISyntaxException {
        if (directConnect) {
            final String content = "client_id=" + defaultAppName + "&username=" + user + "&password=" + password + "&grant_type=password";
            return receiveToken(createDirectConnection(realmName), content);
        } else {
            final String content = "username=" + user + "&password=" + password + "&grant_type=password";
            return receiveToken(createServerProxyConnection(realmName, appName), content);
        }
    }

    private KeycloakOpenidConnectResult receiveTokenByRefresh(final String refreshToken, final String realmName, final String appName) throws IOException, URISyntaxException {
        if (directConnect) {
            final String content = "grant_type=refresh_token&refresh_token=" + refreshToken + "&client_id=" + defaultAppName;
            return receiveToken(createDirectConnection(realmName), content);
        } else {
            final String content = "grant_type=refresh_token&refresh_token=" + refreshToken;
            return receiveToken(createServerProxyConnection(realmName, appName), content);
        }
    }

    private KeycloakOpenidConnectResult receiveToken(final HttpClientConnection connection, final String content) throws IOException {
        Assert.requireNonNull(content, "content");
        LOG.debug("receiving new token from keycloak server");
        connection.setDoOutput(true);
        connection.writeRequestContent(content);
        final int responseCode = connection.readResponseCode();
        if (responseCode == SC_HTTP_UNAUTHORIZED) {
            throw new RuntimeException("Invalid login!");
        }
        final String input = connection.readUTFResponseContent();
        final Gson gson = Client.getService(Gson.class);
        final KeycloakOpenidConnectResult result = gson.fromJson(input, KeycloakOpenidConnectResult.class);
        return result;
    }
}
