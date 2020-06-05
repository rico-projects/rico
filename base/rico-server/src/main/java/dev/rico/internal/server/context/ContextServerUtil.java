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
package dev.rico.internal.server.context;

import dev.rico.core.lang.StringPair;
import dev.rico.internal.core.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static dev.rico.internal.server.context.HttpRequestContextNameConstants.HTTP_REQUEST_CONTEXT_PATH_NAME;
import static dev.rico.internal.server.context.HttpRequestContextNameConstants.HTTP_REQUEST_METHOD_NAME;
import static dev.rico.internal.server.context.HttpRequestContextNameConstants.HTTP_REQUEST_PORT_NAME;
import static dev.rico.internal.server.context.HttpRequestContextNameConstants.HTTP_REQUEST_URI_NAME;

public final class ContextServerUtil {

    private ContextServerUtil() {
    }

    public static StringPair createPortTag(final HttpServletRequest request) {
        Assert.requireNonNull(request, "request");
        return StringPair.of(HTTP_REQUEST_PORT_NAME, String.valueOf(request.getServerPort()));
    }

    public static StringPair createMethodTag(final HttpServletRequest request) {
        Assert.requireNonNull(request, "request");
        final String method = Optional.ofNullable(request.getMethod()).orElse("UNKNOWN");
        return StringPair.of(HTTP_REQUEST_METHOD_NAME, method.isEmpty() ? "UNKNOWN" : method);
    }

    public static StringPair createUriTag(final HttpServletRequest request) {
        Assert.requireNonNull(request, "request");
        final String uri = Optional.ofNullable(request.getRequestURI()).orElse("/");
        return StringPair.of(HTTP_REQUEST_URI_NAME, uri.isEmpty() ? "root" : uri);
    }

    public static StringPair createContextPathTag(final HttpServletRequest request) {
        Assert.requireNonNull(request, "request");
        final String path = Optional.ofNullable(request.getContextPath()).orElse("/");
        return StringPair.of(HTTP_REQUEST_CONTEXT_PATH_NAME, path.isEmpty() ? "" : path);
    }

}
