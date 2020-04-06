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
package dev.rico.internal.client.http;

import com.google.gson.Gson;
import dev.rico.client.ClientConfiguration;
import dev.rico.core.http.HttpCallRequestBuilder;
import dev.rico.core.http.HttpCallResponseBuilder;
import dev.rico.core.http.HttpURLConnectionHandler;
import dev.rico.internal.core.Assert;
import dev.rico.internal.core.http.EmptyInputStream;
import dev.rico.internal.core.http.HttpClientConnection;
import dev.rico.internal.core.http.HttpHeaderImpl;
import org.apiguardian.api.API;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.rico.internal.core.http.HttpHeaderConstants.CONTENT_TYPE_HEADER;
import static dev.rico.internal.core.http.HttpHeaderConstants.JSON_MIME_TYPE;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class HttpCallRequestBuilderImpl implements HttpCallRequestBuilder {

    private final HttpClientConnection connection;

    private final Gson gson;

    private final List<HttpURLConnectionHandler> requestHandlers;

    private final List<HttpURLConnectionHandler> responseHandlers;

    private final ClientConfiguration configuration;

    private final AtomicBoolean done = new AtomicBoolean(false);

    public HttpCallRequestBuilderImpl(final HttpClientConnection connection, final Gson gson, final List<HttpURLConnectionHandler> requestHandlers, final List<HttpURLConnectionHandler> responseHandlers, ClientConfiguration configuration) {
        this.connection = Assert.requireNonNull(connection, "connection");
        this.gson = Assert.requireNonNull(gson, "gson");
        this.configuration = configuration;


        Assert.requireNonNull(requestHandlers, "requestHandlers");
        this.requestHandlers = Collections.unmodifiableList(requestHandlers);

        Assert.requireNonNull(responseHandlers, "responseHandlers");
        this.responseHandlers = Collections.unmodifiableList(responseHandlers);
    }

    @Override
    public HttpCallRequestBuilder withHeader(final String name, final String content) {
        connection.addRequestHeader(new HttpHeaderImpl(name, content));
        return this;
    }

    @Override
    public <I> HttpCallResponseBuilder withContent(final I content) {
        return withContent(gson.toJson(content), JSON_MIME_TYPE);
    }

    @Override
    public HttpCallResponseBuilder withoutContent() {
        return continueWithResponseBuilder(new EmptyInputStream());
    }

    public HttpCallResponseBuilder withContent(final InputStream stream, final String contentType) {
        if(contentType != null && !contentType.isEmpty()) {
            withHeader(CONTENT_TYPE_HEADER, contentType);
        }
        return continueWithResponseBuilder(stream);
    }


    private HttpCallResponseBuilder continueWithResponseBuilder(final InputStream dataProvider) {
        Assert.requireNonNull(dataProvider, "dataProvider");
        if (done.get()) {
            throw new RuntimeException("Request already defined!");
        }
        done.set(true);
        return new HttpCallResponseBuilderImpl(connection, dataProvider, gson, requestHandlers, responseHandlers, configuration);
    }

}
