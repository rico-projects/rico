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
package dev.rico.core.http;

import dev.rico.core.functional.Promise;
import org.apiguardian.api.API;

import java.io.InputStream;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(since = "0.x", status = EXPERIMENTAL)
public interface HttpCallResponseBuilder {

    Promise<HttpResponse<InputStream>, HttpException> streamBytes();

    Promise<HttpResponse<ByteArrayProvider>, HttpException> readBytes();

    Promise<HttpResponse<ByteArrayProvider>, HttpException> readBytes(String contentType);

    Promise<HttpResponse<String>, HttpException> readString();

    Promise<HttpResponse<String>, HttpException> readString(String contentType);

    <R> Promise<HttpResponse<R>, HttpException> readObject(Class<R> responseType);

    Promise<HttpResponse<Void>, HttpException> withoutResult();
}
