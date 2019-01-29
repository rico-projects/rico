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
package dev.rico.core.http;

import dev.rico.internal.core.http.EmptyInputStream;
import org.apiguardian.api.API;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static dev.rico.internal.core.http.HttpHeaderConstants.CHARSET;
import static dev.rico.internal.core.http.HttpHeaderConstants.RAW_MIME_TYPE;
import static dev.rico.internal.core.http.HttpHeaderConstants.TEXT_MIME_TYPE;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(since = "0.x", status = EXPERIMENTAL)
public interface HttpCallRequestBuilder {

    HttpCallRequestBuilder withHeader(String name, String content);

    default HttpCallResponseBuilder withContent(final byte[] content) {
        return withContent(content, RAW_MIME_TYPE);
    }

    default HttpCallResponseBuilder withContent(final byte[] content, final String contentType) {
        if(content == null || content.length == 0) {
            return withContent(new EmptyInputStream(), contentType);
        } else {
            return withContent(new ByteArrayInputStream(content), contentType);
        }
    }


    default HttpCallResponseBuilder withContent(final String content) {
        return withContent(content, TEXT_MIME_TYPE);
    }

    default HttpCallResponseBuilder withContent(final String content, final String contentType) {
        if(content == null || content.isEmpty()) {
            return withContent(new EmptyInputStream(), contentType);
        } else {
            withHeader("charset", CHARSET);
            try {
                return withContent(content.getBytes(CHARSET), contentType);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Encoding error", e);
            }
        }

    }

    <I> HttpCallResponseBuilder withContent(I content);

    HttpCallResponseBuilder withContent(final InputStream stream, final String contentType);

    HttpCallResponseBuilder withoutContent();
}
