/*
 * Copyright 2015-2016 Canoo Engineering AG.
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

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * Created by hendrikebbers on 25.10.16.
 */
public class InstantConverterFactory extends AbstractConverterFactory {

    @SuppressWarnings("rawtypes")
    private final static Converter CONVERTER = new InstantConverter();

    @Override
    public boolean supportsType(Class<?> cls) {
        return Instant.class.isAssignableFrom(cls);
    }

    @Override
    public List<Class> getSupportedTypes() {
        return Collections.singletonList(Instant.class);
    }

    @Override
    public int getTypeIdentifier() {
        return 1000;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }

    private static class InstantConverter extends AbstractStringConverter<Instant> {

        @Override
        public Instant convertFromRemoting(String value) throws ValueConverterException {
            if (value == null) {
                return null;
            }
            try {
                return Instant.from(DateTimeFormatter.ISO_INSTANT.parse(value));
            } catch (Exception e) {
                throw new ValueConverterException("Can not convert to Instant", e);
            }
        }

        @Override
        public String convertToRemoting(Instant value) throws ValueConverterException {
            if (value == null) {
                return null;
            }
            try {
                return value.toString();
            } catch (Exception e) {
                throw new ValueConverterException("Can not convert from Instant", e);
            }
        }
    }

}
