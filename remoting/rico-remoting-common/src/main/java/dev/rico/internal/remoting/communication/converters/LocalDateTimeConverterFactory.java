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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

import static dev.rico.internal.remoting.RemotingConstants.REMOTING_DATE_FORMAT_PATTERN;
import static dev.rico.internal.core.RicoConstants.TIMEZONE_UTC;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class LocalDateTimeConverterFactory extends AbstractConverterFactory {

    private final static Converter CONVERTER = new LocalDateTimeConverter();

    @Override
    public boolean supportsType(final Class<?> cls) {
        return LocalDateTime.class.isAssignableFrom(cls);
    }

    @Override
    public List<Class> getSupportedTypes() {
        return Collections.singletonList(LocalDateTime.class);
    }

    @Override
    public int getTypeIdentifier() {
        return ValueFieldTypes.LOCAL_DATE_TIME_FIELD_TYPE;
    }

    @Override
    public Converter getConverterForType(final Class<?> cls) {
        return CONVERTER;
    }

    private static class LocalDateTimeConverter extends AbstractDateConverter<LocalDateTime> {

        @Override
        public LocalDateTime convertFromRemoting(final String value) throws ValueConverterException {
            if (value == null) {
                return null;
            }
            try {
                final Calendar result = Calendar.getInstance(TimeZone.getTimeZone(TIMEZONE_UTC));
                result.setTime(getDateFormat().parse(value));
                return result.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            } catch (final Exception e) {
                throw new ValueConverterException("Can not convert to LocalDateTime", e);
            }
        }

        @Override
        public String convertToRemoting(final LocalDateTime value) throws ValueConverterException {
            if (value == null) {
                return null;
            }
            try {
                final Date date = Date.from(value.toInstant(OffsetDateTime.now().getOffset()));
                return getDateFormat().format(date);
            } catch (final Exception e) {
                throw new ValueConverterException("Can not convert from LocalDateTime", e);
            }
        }
    }

}
