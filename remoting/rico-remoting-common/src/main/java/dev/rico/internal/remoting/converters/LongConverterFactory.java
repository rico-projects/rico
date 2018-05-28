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
package dev.rico.internal.remoting.converters;

import dev.rico.remoting.converter.Converter;
import org.apiguardian.api.API;

import java.util.Arrays;
import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class LongConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_LONG = 4;

    private final static Converter CONVERTER = new AbstractNumberConverter<Long>() {

        @Override
        public Long convertFromRemoting(final Number value) {
            return value == null ? null : value.longValue();
        }

        @Override
        public Number convertToRemoting(final Long value) {
            return value;
        }
    };


    @Override
    public boolean supportsType(final Class<?> cls) {
        return long.class.equals(cls) || Long.class.equals(cls);
    }

    @Override
    public List<Class> getSupportedTypes() {
        return Arrays.asList(long.class, Long.class);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_LONG;
    }

    @Override
    public Converter getConverterForType(final Class<?> cls) {
        return CONVERTER;
    }
}
