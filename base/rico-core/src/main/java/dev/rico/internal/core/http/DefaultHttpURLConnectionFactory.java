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

import dev.rico.internal.core.Assert;
import dev.rico.core.http.HttpURLConnectionFactory;
import org.apiguardian.api.API;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class DefaultHttpURLConnectionFactory implements HttpURLConnectionFactory {

    @Override
    public HttpURLConnection create(final URI url) throws IOException {
        Assert.requireNonNull(url, "url");
        final URLConnection connection = url.toURL().openConnection();
        if(connection instanceof HttpURLConnection) {
            return (HttpURLConnection) connection;
        }
        throw new IOException("URL do not provide a HttpURLConnection!");
    }
}
