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
package dev.rico.internal.core.http;

import dev.rico.core.http.HttpHeader;
import dev.rico.core.http.HttpURLConnectionFactory;
import dev.rico.core.http.RequestMethod;
import dev.rico.core.logging.Logger;
import dev.rico.core.logging.LoggerFactory;
import dev.rico.internal.core.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.rico.core.http.RequestMethod.GET;
import static dev.rico.internal.core.http.HttpHeaderConstants.CHARSET;
import static dev.rico.internal.core.http.HttpHeaderConstants.CHARSET_HEADER;
import static dev.rico.internal.core.http.HttpHeaderConstants.CONTENT_LENGHT_HEADER;

public class HttpClientConnection {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientConnection.class);

    private final HttpURLConnection connection;

    private final RequestMethod method;

    private final URI url;

    public HttpClientConnection(final HttpURLConnectionFactory httpURLConnectionFactory, final URI url, final RequestMethod method) throws IOException {
        Assert.requireNonNull(httpURLConnectionFactory, "httpURLConnectionFactory");
        this.url = Assert.requireNonNull(url, "url");
        this.method = Assert.requireNonNull(method, "method");

        this.connection = httpURLConnectionFactory.create(url);
        if (connection == null) {
            throw new IllegalStateException("Connection was not created. Check connection factory!");
        }
        this.connection.setRequestMethod(method.getRawName());
        connection.setUseCaches(false);
    }

    public HttpClientConnection(final URI url, final RequestMethod method) throws IOException {
        this(new DefaultHttpURLConnectionFactory(), url, method);
    }

    public void addRequestHeader(final String name, final String content) {
        addRequestHeader(new HttpHeaderImpl(name, content));
    }

    public void addRequestHeader(final HttpHeader headers) {
        Assert.requireNonNull(headers, "headers");
        connection.setRequestProperty(headers.getName(), headers.getContent());
    }

    public void writeRequestContent(final String content) throws IOException {
        Assert.requireNonNull(content, "content");
        addRequestHeader(CHARSET_HEADER, CHARSET);
        writeRequestContent(content.getBytes(CHARSET));
    }

    public void writeRequestContent(final byte[] content) throws IOException {
        Assert.requireNonNull(content, "content");
        if (content.length > 0) {
            if (method == GET) {
                LOG.warn("You are currently defining a request content for a HTTP GET call for endpoint '{}'", url);
            }
            addRequestHeader(CONTENT_LENGHT_HEADER, content.length + "");
            setDoOutput(true);
            ConnectionUtils.writeContent(connection, content);
        }
    }

    public void writeRequestContent(final InputStream inputStream) throws IOException {
        Assert.requireNonNull(inputStream, "inputStream");
        if (inputStream.available() > 0) {
            setDoOutput(true);
            final OutputStream outputStream = connection.getOutputStream();
            final long length = ConnectionUtils.copy(inputStream, outputStream);
            if (length > 0) {
                if (method == GET) {
                    LOG.warn("You are currently defining a request content for a HTTP GET call for endpoint '{}'", url);
                }
            }
        }
    }


    public int readResponseCode() throws IOException {
        return connection.getResponseCode();
    }

    public byte[] readResponseContent() throws IOException {
        return ConnectionUtils.readContent(connection);
    }

    public InputStream getContentStream() throws IOException {
        return ConnectionUtils.getContentStream(connection);
    }

    public String readUTFResponseContent() throws IOException {
        final String charSet = Optional.ofNullable(getResponseHeader(CHARSET_HEADER)).map(h -> h.getContent()).orElse(CHARSET);
        return new String(readResponseContent(), charSet);
    }

    public String getResponseMessage() throws IOException {
        return connection.getResponseMessage();
    }

    public HttpHeader getResponseHeader(final String name) {
        return getResponseHeaders().stream()
                .filter(h -> h.getName() != null)
                .filter(h -> h.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<HttpHeader> getResponseHeaders() {
        return connection.getHeaderFields().
                entrySet().
                stream().
                flatMap(e -> e.getValue().stream().map(v -> new HttpHeaderImpl(e.getKey(), v))).
                collect(Collectors.toList());
    }

    public void setDoOutput(final boolean doOutput) {
        connection.setDoOutput(doOutput);
    }

    public HttpURLConnection getConnection() {
        return connection;
    }

    public long getContentSize() {
        return connection.getContentLengthLong();
    }

}
