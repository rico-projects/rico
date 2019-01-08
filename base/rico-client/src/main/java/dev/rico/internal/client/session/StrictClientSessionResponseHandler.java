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
package dev.rico.internal.client.session;

import dev.rico.internal.core.Assert;
import dev.rico.internal.core.RicoConstants;
import dev.rico.core.http.HttpURLConnectionHandler;
import org.apiguardian.api.API;

import java.net.HttpURLConnection;
import java.net.URI;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class StrictClientSessionResponseHandler implements HttpURLConnectionHandler {

    private final URI url;

    public StrictClientSessionResponseHandler(final URI url) {
        this.url = Assert.requireNonNull(url, "url");
    }
    @Override
    public void handle(final HttpURLConnection response) {
        Assert.requireNonNull(response, "response");
        if(this.url.equals(response.getURL())) {
            String clientIdInHeader = response.getHeaderField(RicoConstants.CLIENT_ID_HTTP_HEADER_NAME);
            if (clientIdInHeader == null) {
                throw new RuntimeException("No client id found in response");
            }
        }
    }
}
