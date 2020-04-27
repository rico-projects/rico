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

import dev.rico.internal.core.RicoConstants;
import dev.rico.remoting.converter.Converter;
import dev.rico.remoting.converter.ValueConverterException;
import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class CalendarConverterFactory extends AbstractConverterFactory {

    private static final Converter CONVERTER = new CalendarConverter();

    public static final int FIELD_TYPE_CALENDAR = 11;

    @Override
    public List<Class> getSupportedTypes() {
        return Arrays.asList(Calendar.class, GregorianCalendar.class);
    }

    @Override
    public int getTypeIdentifier() {
        return FIELD_TYPE_CALENDAR;
    }

    @Override
    public Converter getConverterForType(Class<?> cls) {
        return CONVERTER;
    }

    private static class CalendarConverter extends AbstractDateConverter<Calendar> {

        private static final Logger LOG = LoggerFactory.getLogger(CalendarConverter.class);

        @Override
        public Calendar convertFromRemoting(final String value) throws ValueConverterException {
            if (value == null) {
                return null;
            }
            try {
                final Calendar result = Calendar.getInstance(TimeZone.getTimeZone(RicoConstants.TIMEZONE_UTC));
                result.setTime(getDateFormat().parse(value));
                return result;
            } catch (final Exception e) {
                throw new ValueConverterException("Unable to parse the date: " + value, e);
            }
        }

        @Override
        public String convertToRemoting(final Calendar value) throws ValueConverterException {
            if (value == null) {
                return null;
            }
            try {
                return getDateFormat().format((value).getTime());
            } catch (final Exception e) {
                throw new ValueConverterException("Unable to format the date: " + value, e);
            }
        }
    }
}
