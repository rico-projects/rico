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
package dev.rico.internal.client.http.cookie;

import dev.rico.core.logging.Logger;
import dev.rico.core.logging.LoggerFactory;
import dev.rico.internal.core.Assert;
import org.apiguardian.api.API;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dev.rico.internal.core.http.HttpHeaderConstants.COOKIE_HEADER;
import static dev.rico.internal.core.http.HttpHeaderConstants.SET_COOKIE_HEADER;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class HttpClientCookieHandler {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientCookieHandler.class);

    private final CookieStore cookieStore;

    public HttpClientCookieHandler(final CookieStore cookieStore) {
        this.cookieStore = Assert.requireNonNull(cookieStore, "cookieStore");
    }

    public void updateCookiesFromResponse(final HttpURLConnection connection) throws URISyntaxException {
        Assert.requireNonNull(connection, "connection");
        LOG.debug("adding cookies from response to cookie store");

        final Map<String, List<String>> headerFields = connection.getHeaderFields();
        final List<String> cookiesHeader = headerFields.get(SET_COOKIE_HEADER);
        if (cookiesHeader != null) {
            LOG.debug("found '{}' header field", SET_COOKIE_HEADER);
            for (String cookie : cookiesHeader) {
                if (cookie == null || cookie.isEmpty()) {
                    continue;
                }
                LOG.debug("will parse '{}' header content '{}'", SET_COOKIE_HEADER, cookie);
                final List<HttpCookie> cookies = new ArrayList<>();
                try {
                    cookies.addAll(HttpCookie.parse(cookie));
                } catch (final Exception e) {
                    throw new RuntimeException("Can not convert '" + SET_COOKIE_HEADER + "' response header field to http cookies. Bad content: " + cookie, e);
                }
                LOG.debug("Found {} http cookies in header", cookies.size());
                for (final HttpCookie httpCookie : cookies) {
                    LOG.trace("Found Cookie '{}' for Domain '{}' at Ports '{}' with Path '{}", httpCookie.getValue(), httpCookie.getDomain(), httpCookie.getPortlist(), httpCookie.getPath());
                    cookieStore.add(connection.getURL().toURI(), httpCookie);
                }

            }
        }
    }

    public void setRequestCookies(final HttpURLConnection connection) throws URISyntaxException {
        Assert.requireNonNull(connection, "connection");
        LOG.debug("Adding cookies from cookie store to request");
        if (cookieStore.getCookies().size() > 0) {
            final String cookieValue = cookieStore.get(connection.getURL().toURI()).stream().
                    map(cookie -> {
                        LOG.trace("Cookie '{}' is for Domain '{}' at Ports '{}' with Path '{}", cookie.getValue(), cookie.getDomain(), cookie.getPortlist(), cookie.getPath());
                        return cookie.toString();
                    }).
                    collect(Collectors.joining("; "));

            if (!cookieValue.isEmpty()) {
                LOG.debug("Adding '{}' header to request. Content: {}", SET_COOKIE_HEADER, cookieValue);
                connection.setRequestProperty(COOKIE_HEADER, cookieValue);
                return;
            }
        }
        LOG.debug("No cookies to add");
    }
}
