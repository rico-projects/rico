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
package dev.rico.internal.remoting.info;

import dev.rico.internal.core.Assert;
import dev.rico.remoting.converter.Converter;
import dev.rico.remoting.converter.ValueConverterException;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public abstract class PropertyInfo {

    private final String attributeName;
    private final Converter converter;

    public PropertyInfo(final String attributeName, final Converter converter) {
        this.attributeName = attributeName;
        this.converter = Assert.requireNonNull(converter, "converter");
    }

    public String getAttributeName() {
        return attributeName;
    }

    public abstract Object getPrivileged(final Object bean);

    public abstract void setPriviliged(final Object bean, final Object value);

    public Object convertFromRemoting(final Object value) throws ValueConverterException {
        return converter.convertFromRemoting(value);
    }

    public Object convertToRemoting(final Object value) throws ValueConverterException {
        return converter.convertToRemoting(value);
    }
}
