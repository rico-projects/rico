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

import java.util.Arrays;
import java.util.List;

public class CharacterConverterFactory extends AbstractConverterFactory {

    public static final int FIELD_CHARACTER_BYTE = 107;

    private static final Converter<Character, Number> CONVERTER = new AbstractNumberConverter<>() {

        @Override
        public Character convertFromRemoting(final Number value) {
            return value == null ? null : (char) value.intValue();
        }

        @Override
        public Number convertToRemoting(final Character value) {
            return value == null ? null : Integer.valueOf(value.charValue());
        }
    };

    @Override
    public List<Class> getSupportedTypes() {
        return Arrays.asList(char.class, Character.class);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_CHARACTER_BYTE;
    }

    @Override
    public Converter getConverterForType(final Class<?> cls) {
        return CONVERTER;
    }
}
