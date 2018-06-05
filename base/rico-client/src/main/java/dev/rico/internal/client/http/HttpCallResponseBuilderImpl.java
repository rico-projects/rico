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
import dev.rico.internal.core.http.AbstractHttpCallResponseBuilder;
import dev.rico.internal.core.http.HttpClientConnection;
import dev.rico.client.ClientConfiguration;
import dev.rico.core.http.HttpExecutor;
import com.google.gson.Gson;
import dev.rico.internal.core.http.ResponseContentConverter;
import org.apiguardian.api.API;

import java.io.InputStream;
import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class HttpCallResponseBuilderImpl extends AbstractHttpCallResponseBuilder {

    private final ClientConfiguration configuration;

    public HttpCallResponseBuilderImpl(final HttpClientConnection connection, final ByteArrayProvider dataProvider, final Gson gson, final List<HttpURLConnectionHandler> requestHandlers, final List<HttpURLConnectionHandler> responseHandlers, final ClientConfiguration configuration) {
        super(connection, dataProvider, gson, requestHandlers, responseHandlers);
        this.configuration = Assert.requireNonNull(configuration, "configuration");
    }

    @Override
    protected  <R> HttpExecutor<R> createExecutor(final ResponseContentConverter<R> converter) {
        return new HttpCallExecutorImpl<> (configuration, () -> handleRequest(converter));
    }

    @Override
    protected HttpExecutor<InputStream> createExecutor() {
        return new HttpCallExecutorImpl<> (configuration, () -> handleRequest());
    }
}
