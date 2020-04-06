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
package dev.rico.internal.remoting.communication.converters;

import dev.rico.remoting.converter.Converter;
import dev.rico.remoting.converter.ConverterFactory;
import dev.rico.remoting.converter.BeanRepo;
import dev.rico.remoting.converter.ValueConverterException;

import java.util.Collections;
import java.util.List;

public class ClassConverterFactory implements ConverterFactory {

    private final static ClassConverter CONVERTER = new ClassConverter();

    @Override
    public void init(final BeanRepo beanRepository) {}

    @Override
    public List<Class> getSupportedTypes() {
        return Collections.singletonList(Class.class);
    }

    @Override
    public int getTypeIdentifier() {
        return 100;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }

    private static class ClassConverter extends AbstractStringConverter<Class> {

        @Override
        public Class convertFromRemoting(final String value) throws ValueConverterException {
            if(value == null) {
                return null;
            }
            try {
                return Class.forName(value);
            } catch (final ClassNotFoundException e) {
                throw new ValueConverterException("Can not find class " + value, e);
            }
        }

        @Override
        public String convertToRemoting(final Class value) throws ValueConverterException {
            if(value == null) {
                return null;
            }
            return value.getName();
        }
    }
}
