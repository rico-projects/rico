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
import dev.rico.internal.core.Assert;
import dev.rico.core.http.ByteArrayProvider;
import dev.rico.core.http.HttpResponse;
import dev.rico.core.http.RequestMethod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import static dev.rico.internal.core.RicoConstants.HASH_ALGORITHM;
import static dev.rico.internal.core.http.HttpHeaderConstants.CHARSET;
import static dev.rico.internal.core.http.HttpHeaderConstants.CONTENT_DISPOSITION_HEADER_NAME;

public class ConnectionUtils {

    private ConnectionUtils() {
    }

    public static String toBase64(final byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static DigestInputStream createMD5HashStream(final InputStream inputStream) throws NoSuchAlgorithmException {
        Assert.requireNonNull(inputStream, "inputStream");
        final MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        return new DigestInputStream(inputStream, digest);
    }

    public static byte[] readContent(final InputStream inputStream) throws IOException {
        Assert.requireNonNull(inputStream, "inputStream");
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        copy(inputStream, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] readContent(final HttpURLConnection connection) throws IOException {
        Assert.requireNonNull(connection, "connection");
        try (final InputStream inputStream = getContentStream(connection)) {
            return readContent(inputStream);
        }
    }

    public static InputStream getContentStream(final HttpURLConnection connection) throws IOException {
        Assert.requireNonNull(connection, "connection");
        final InputStream errorstream = connection.getErrorStream();
        if (errorstream == null) {
            return connection.getInputStream();
        } else {
            return errorstream;
        }
    }

    public static String readUTF8Content(final HttpURLConnection connection) throws IOException {
        return new String(readContent(connection), CHARSET);
    }

    public static String readUTF8Content(final InputStream inputStream) throws IOException {
        return new String(readContent(inputStream), CHARSET);
    }

    public static void writeContent(final OutputStream outputStream, final byte[] rawData) throws IOException {
        Assert.requireNonNull(outputStream, "outputStream");
        Assert.requireNonNull(rawData, "rawData");
        outputStream.write(rawData);
        outputStream.flush();
    }

    public static void writeContent(final HttpURLConnection connection, final byte[] rawData) throws IOException {
        Assert.requireNonNull(connection, "connection");
        Assert.requireNonNull(rawData, "rawData");
        try (final OutputStream outputStream = connection.getOutputStream()) {
            writeContent(outputStream, rawData);
        }
    }

    public static void writeContent(final HttpURLConnection connection, final ByteArrayProvider provider) throws IOException {
        Assert.requireNonNull(provider, "provider");
        writeContent(connection, provider.get());
    }

    public static void writeContent(final OutputStream outputStream, final ByteArrayProvider provider) throws IOException {
        Assert.requireNonNull(provider, "provider");
        writeContent(outputStream, provider.get());
    }

    public static void writeUTF8Content(final HttpURLConnection connection, final String content) throws IOException {
        Assert.requireNonNull(content, "content");
        writeContent(connection, content.getBytes(CHARSET));
    }

    public static void writeUTF8Content(final OutputStream outputStream, final String content) throws IOException {
        Assert.requireNonNull(content, "content");
        writeContent(outputStream, content.getBytes(CHARSET));
    }

    public static String getContentName(final HttpResponse<InputStream> response) {
        Assert.requireNonNull(response, "response");
        return response.getHeaders().stream()
                .filter(h -> Objects.equals(CONTENT_DISPOSITION_HEADER_NAME, h.getName()))
                .map(HttpHeader::getContent)
                .flatMap(v -> Arrays.stream(v.split(";")))
                .map(String::trim)
                .filter(v -> v.startsWith("filename=\""))
                .map(v -> v.substring("filename=\"" .length(), v.length() - 1))
                .findAny()
                .orElse(null);
    }

    public static boolean doesURLExist(final URI uri) {
        Assert.requireNonNull(uri, "uri");
        try {
            final HttpClientConnection clientConnection = new HttpClientConnection(uri, RequestMethod.HEAD);
            final int code = clientConnection.getConnection().getResponseCode();
            if (code < 300) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static long copy(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        return copy(inputStream, outputStream, 1024);
    }

    public static long copy(final InputStream inputStream, final OutputStream outputStream, final int bufferSize) throws IOException {
        Assert.requireNonNull(inputStream, "inputStream");
        Assert.requireNonNull(outputStream, "outputStream");

        final byte[] buffer = new byte[bufferSize];
        long finalLength = 0;
        int len;
        while ((len = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
            finalLength = finalLength  + len;
        }
        return finalLength;
    }
}
