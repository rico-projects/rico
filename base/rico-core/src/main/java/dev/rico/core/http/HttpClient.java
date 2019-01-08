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

import org.apiguardian.api.API;

import java.net.URI;

import static dev.rico.core.http.RequestMethod.DELETE;
import static dev.rico.core.http.RequestMethod.GET;
import static dev.rico.core.http.RequestMethod.POST;
import static dev.rico.core.http.RequestMethod.PUT;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(since = "0.x", status = EXPERIMENTAL)
public interface HttpClient {

    @Deprecated
    void addResponseHandler(HttpURLConnectionHandler handler);

    @Deprecated
    default HttpCallRequestBuilder request(final URI url) {
        return get(url);
    }

    @Deprecated
    default HttpCallRequestBuilder request(final String url) {
        return get(url);
    }

    HttpCallRequestBuilder request(URI url, RequestMethod method);

    HttpCallRequestBuilder request(String url, RequestMethod method);

    default HttpCallRequestBuilder get(final URI url) {
        return request(url, GET);
    }

    default HttpCallRequestBuilder get(final String url) {
        return request(url, GET);
    }

    default HttpCallRequestBuilder post(final URI url) {
        return request(url, POST);
    }

    default HttpCallRequestBuilder post(final String url) {
        return request(url, POST);
    }

    default HttpCallRequestBuilder put(final URI url) {
        return request(url, PUT);
    }

    default HttpCallRequestBuilder put(final String url) {
        return request(url, PUT);
    }

    default HttpCallRequestBuilder delete(final URI url) {
        return request(url, DELETE);
    }

    default HttpCallRequestBuilder delete(final String url) {
        return request(url, DELETE);
    }
}
