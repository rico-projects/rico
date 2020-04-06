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
package dev.rico.internal.remoting.repo;

import dev.rico.internal.core.Assert;
import dev.rico.internal.core.ReflectionHelper;
import dev.rico.remoting.converter.Converter;
import dev.rico.remoting.converter.ValueConverterException;
import org.apiguardian.api.API;

import java.lang.reflect.Field;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class PropertyInfo {

    private final Field field;

    private final Converter converter;

    public PropertyInfo(final Converter converter, final Field field) {
        this.field = Assert.requireNonNull(field, "field");
        this.converter = Assert.requireNonNull(converter, "converter");
    }

    public String getAttributeName() {
        return field.getName();
    }

    public Object getPrivileged(final Object bean) {
        return ReflectionHelper.getPrivileged(field, bean);
    }

    public void setPriviliged(final Object bean, final Object value) {
        ReflectionHelper.setPrivileged(field, bean, value);
    }

    public Object convertFromRemoting(final Object value) throws ValueConverterException {
        return converter.convertFromRemoting(value);
    }

    public Object convertToRemoting(final Object value) throws ValueConverterException {
        return converter.convertToRemoting(value);
    }
}
