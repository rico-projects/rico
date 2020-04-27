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
package dev.rico.internal.remoting.converters;

import dev.rico.remoting.converter.Converter;
import org.apiguardian.api.API;

import java.util.Arrays;
import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class IntegerConverterFactory extends AbstractConverterFactory {

    public static final int FIELD_TYPE_INT = 3;

    private static final Converter CONVERTER = new AbstractNumberConverter<Integer>() {

        @Override
        public Integer convertFromRemoting(final Number value) {
            return value == null ? null : value.intValue();
        }

        @Override
        public Number convertToRemoting(final Integer value) {
            return value;
        }
    };

    @Override
    public List<Class> getSupportedTypes() {
        return Arrays.asList(int.class, Integer.class);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_INT;
    }

    @Override
    public Converter getConverterForType(final Class<?> cls) {
        return CONVERTER;
    }
}
