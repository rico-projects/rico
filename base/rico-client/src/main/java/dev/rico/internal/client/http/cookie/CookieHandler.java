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
package dev.rico.internal.client.http.cookie;

import dev.rico.core.http.RequestChain;
import dev.rico.internal.core.Assert;
import dev.rico.core.http.HttpURLConnectionInterceptor;
import org.apiguardian.api.API;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class CookieHandler implements HttpURLConnectionInterceptor {

    private final HttpClientCookieHandler clientCookieHandler;

    public CookieHandler(final HttpClientCookieHandler clientCookieHandler) {
        this.clientCookieHandler = Assert.requireNonNull(clientCookieHandler, "clientCookieHandler");
    }

    @Override
    public void handle(final HttpURLConnection connection, final RequestChain chain) throws IOException {
        Assert.requireNonNull(connection, "connection");
        try {
            clientCookieHandler.setRequestCookies(connection);
            HttpURLConnection response = chain.call();
            clientCookieHandler.updateCookiesFromResponse(response);
        } catch (final URISyntaxException e) {
            throw new RuntimeException("Can not set cookies", e);
        }
    }
}
