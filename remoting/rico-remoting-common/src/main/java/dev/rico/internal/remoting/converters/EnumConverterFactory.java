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
import dev.rico.remoting.converter.ValueConverterException;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class EnumConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_ENUM = 10;

    private final Map<Class<?>, EnumConverter> enumConverters = new HashMap<>();

    @Override
    public boolean supportsType(final Class<?> cls) {
        return Enum.class.isAssignableFrom(cls);
    }

    @Override
    public List<Class> getSupportedTypes() {
        return Collections.singletonList(Enum.class);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_ENUM;
    }

    @Override
    public Converter getConverterForType(final Class<?> cls) {
        EnumConverter enumConverter = enumConverters.get(cls);
        if (enumConverter == null) {
            enumConverter = new EnumConverter(cls);
            enumConverters.put(cls, enumConverter);
        }
        return enumConverter;
    }

    private static class EnumConverter extends AbstractStringConverter<Enum> {

        private static final Logger LOG = LoggerFactory.getLogger(EnumConverter.class);

        private final Class<? extends Enum> clazz;

        @SuppressWarnings("unchecked")
        public EnumConverter(final Class<?> clazz) {
            this.clazz = (Class<? extends Enum>) clazz;
        }

        @Override
        public Enum convertFromRemoting(final String value) throws ValueConverterException{
            if (value == null) {
                return null;
            }
            try {
                return Enum.valueOf(clazz, value);
            } catch (IllegalArgumentException ex) {
                throw new ValueConverterException("Unable to convert to an enum: " + clazz + ", value: " + value, ex);
            }
        }

        @Override
        public String convertToRemoting(final Enum value) throws ValueConverterException{
            if (value == null) {
                return null;
            }
            try {
                return value.name();
            } catch (ClassCastException ex) {
                throw new ValueConverterException("Unable to evaluatethe enum: " + value, ex);
            }
        }
    }

}
