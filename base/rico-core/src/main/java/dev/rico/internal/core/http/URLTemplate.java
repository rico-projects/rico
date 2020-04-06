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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

public class URLTemplate {

    private final String urlRepresentation;

    private URLTemplate(final String urlRepresentation) {
        Assert.requireNonBlank(urlRepresentation, "urlRepresentation");
        this.urlRepresentation = urlRepresentation;
    }

    public static URLTemplate of(final String urlRepresentation) {
        return new URLTemplate(urlRepresentation);
    }

    public String createString() {
        return create().toString();
    }

    public URL create() {
        try {
            return new URL(urlRepresentation);
        } catch (final MalformedURLException e) {
            throw new RuntimeException("Can not create url for '" + urlRepresentation + "'");
        }
    }

    public String createString(final String key, final String value) {
        return createString(Collections.singletonMap(key, value));
    }

    public String createString(final String key, final boolean value) {
        return createString(key, String.valueOf(value));
    }

    public String createString(final String key, final int value) {
        return createString(key, String.valueOf(value));
    }

    public String createString(final String key, final long value) {
        return createString(key, String.valueOf(value));
    }

    public String createString(final URLParams params) {
        return createString(params.asMap());
    }

    public String createString(final Map<String, String> variables) {
        return create(variables).toString();
    }

    public URL create(final String key, final boolean value) {
        return create(key, String.valueOf(value));
    }

    public URL create(final String key, final int value) {
        return create(key, String.valueOf(value));
    }

    public URL create(final String key, final long value) {
        return create(key, String.valueOf(value));
    }

    public URL create(final String key, final String value) {
        return create(Collections.singletonMap(key, value));
    }

    public URL create(final URLParams params) {
        Assert.requireNonNull(params, "params");
        return create(params.asMap());
    }

    public URL create(final Map<String, String> variables) {
        Assert.requireNonNull(variables, "variables");

        final String url = variables.keySet()
                .stream()
                .reduce(urlRepresentation, (base, key) -> replace(base, key, variables.get(key)));
        try {
            return new URL(url);
        } catch (final MalformedURLException e) {
            throw new RuntimeException("Can not create url for '" + url + "'");
        }
    }

    private String replace(final String base, final String key, final String value) {
        final String toReplace = "{" + key + "}";
        final String replaced = base.replace(toReplace, value);
        if(replaced == base) {
            return replaced;
        } else {
            return replace(replaced, key, value);
        }
    }
}
