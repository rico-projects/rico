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
package dev.rico.internal.core;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public interface RicoConstants {

    String RICO = "rico";

    String HASH_ALGORITHM = "MD5";

    String THREAD_CONTEXT = "thread";

    String APPLICATION_NAME_PROPERTY = "application.name";

    String APPLICATION_NAME_DEFAULT = "app";

    String TIMEZONE_UTC = "UTC";

    String CLIENT_ID_HTTP_HEADER_NAME = "X-Client-Session-Id";

    String THREAD_NAME_PREFIX = "Rico-Background-Thread-";

    String THREAD_GROUP_NAME = "Rico executors";

    String OS_NAME = "os.name";

    String WIN = "win";
}
