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
package dev.rico.internal.client.http;

import dev.rico.core.http.*;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.http.AbstractHttpClient;
import dev.rico.internal.core.http.DefaultHttpURLConnectionFactory;
import dev.rico.internal.core.http.HttpClientConnection;
import dev.rico.client.ClientConfiguration;
import com.google.gson.Gson;
import org.apiguardian.api.API;

import java.io.IOException;
import java.net.*;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class HttpClientImpl extends AbstractHttpClient {

    private final ClientConfiguration configuration;

    public HttpClientImpl(final Gson gson, final ClientConfiguration configuration) {
        this(gson, new DefaultHttpURLConnectionFactory(), configuration);
    }

    public HttpClientImpl(final Gson gson, final HttpURLConnectionFactory httpURLConnectionFactory, final ClientConfiguration configuration) {
        super(gson, httpURLConnectionFactory);
        this.configuration = configuration;
    }

    @Override
    public HttpCallRequestBuilder request(final URI url, final RequestMethod method) {
        try {
            Assert.requireNonNull(url, "url");
            Assert.requireNonNull(method, "method");
            final HttpClientConnection clientConnection = new HttpClientConnection(getHttpURLConnectionFactory(), url, method);
            return new HttpCallRequestBuilderImpl(clientConnection, getGson(), getRequestChainHandlers(), configuration);
        } catch (final IOException e) {
            throw new RuntimeException("HTTP error", e);
        }
    }
}
