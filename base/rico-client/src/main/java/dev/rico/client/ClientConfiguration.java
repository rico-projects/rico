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
package dev.rico.client;

import dev.rico.core.Configuration;
import dev.rico.core.http.HttpURLConnectionFactory;
import org.apiguardian.api.API;

import java.net.CookieStore;
import java.util.List;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(since = "0.19.0", status = EXPERIMENTAL)
public interface ClientConfiguration extends Configuration {

    Thread.UncaughtExceptionHandler getUncaughtExceptionHandler();

    Thread.UncaughtExceptionHandler getUiUncaughtExceptionHandler();

    CookieStore getCookieStore();

    HttpURLConnectionFactory getHttpURLConnectionFactory();

    void setCookieStore(CookieStore cookieStore);

    void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler);

    void setUiUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler);

    void setHttpURLConnectionFactory(HttpURLConnectionFactory httpURLConnectionFactory);

    void setStringProperty(final String key, final String value);

    void setBooleanProperty(final String key, boolean value);

    void setIntProperty(final String key, int value);

    void setLongProperty(final String key, long value);

    void setListProperty(final String key, final List<String> value);

    <T> void setObjectProperty(final String key, T value);

    <T> T getObjectProperty(final String key);

    <T> T getObjectProperty(final String key, final T defaultValue);
}
