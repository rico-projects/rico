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
package dev.rico.internal.remoting.communication.converters;

import dev.rico.remoting.converter.Converter;
import dev.rico.remoting.converter.ValueConverterException;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.*;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class DateConverterFactory extends AbstractConverterFactory {

    public final static int FIELD_TYPE_DATE = 9;

    private final static Converter CONVERTER = new DateConverter();

    @Override
    public boolean supportsType(final Class<?> cls) {
        return Date.class.isAssignableFrom(cls);
    }

    @Override
    public List<Class> getSupportedTypes() {
        return Collections.singletonList(Date.class);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_DATE;
    }

    @Override
    public Converter getConverterForType(final Class<?> cls) {
        return CONVERTER;
    }

    private static class DateConverter extends AbstractDateConverter<Date> {

        private static final Logger LOG = LoggerFactory.getLogger(DateConverter.class);

        @Override
        public Date convertFromRemoting(final String value) throws ValueConverterException{
            if (value == null) {
                return null;
            }
            try {
                return getDateFormat().parse(value);
            } catch (final Exception e) {
                throw new ValueConverterException("Unable to parse the date: " + value, e);
            }
        }

        @Override
        public String convertToRemoting(final Date value) throws ValueConverterException{
            if (value == null) {
                return null;
            }
            try {
                return getDateFormat().format(value);
            } catch (final Exception e) {
                throw new ValueConverterException("Unable to format the date: " + value, e);
            }
        }
    }
}
