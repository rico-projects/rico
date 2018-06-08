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
import dev.rico.internal.core.http.AbstractHttpCallRequestBuilder;
import dev.rico.internal.core.http.HttpClientConnection;
import dev.rico.client.ClientConfiguration;
import com.google.gson.Gson;
import org.apiguardian.api.API;

import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class HttpCallRequestBuilderImpl extends AbstractHttpCallRequestBuilder {

    private final ClientConfiguration configuration;

    public HttpCallRequestBuilderImpl(final HttpClientConnection connection, final Gson gson, final List<HttpURLConnectionInterceptor> requestChainHandlers, ClientConfiguration configuration) {
        super(connection, gson, requestChainHandlers);
        this.configuration = configuration;
    }

    @Override
    protected HttpCallResponseBuilder createResponseBuilder(HttpClientConnection connection, ByteArrayProvider dataProvider, Gson gson, List<HttpURLConnectionInterceptor> requestChainHandlers) {
        return new HttpCallResponseBuilderImpl(connection, dataProvider, gson, requestChainHandlers, configuration);
    }
}
