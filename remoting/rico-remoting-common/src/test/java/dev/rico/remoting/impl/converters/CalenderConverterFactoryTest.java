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
package dev.rico.remoting.impl.converters;

import dev.rico.internal.remoting.converters.CalendarConverterFactory;
import dev.rico.remoting.converter.Converter;
import dev.rico.remoting.converter.ValueConverterException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class CalenderConverterFactoryTest {

    @Test
    public void testTypeIdentifier() {
        CalendarConverterFactory factory = new CalendarConverterFactory();
        Assert.assertEquals(factory.getTypeIdentifier(), CalendarConverterFactory.FIELD_TYPE_CALENDAR);
    }

    @Test
    public void testTypeSupport() {
        CalendarConverterFactory factory = new CalendarConverterFactory();
        Assert.assertTrue(factory.supportsType(Calendar.class));
        Assert.assertTrue(factory.supportsType(GregorianCalendar.class));
    }

    @Test
    public void testConverterCreation() {
        CalendarConverterFactory factory = new CalendarConverterFactory();
        Converter converter = factory.getConverterForType(Calendar.class);
        Assert.assertNotNull(converter);

        converter = factory.getConverterForType(GregorianCalendar.class);
        Assert.assertNotNull(converter);
    }

    @Test
    public void testConversionCurrentDate() throws ValueConverterException {
        CalendarConverterFactory factory = new CalendarConverterFactory();
        Converter converter = factory.getConverterForType(Calendar.class);

        Calendar calendar = GregorianCalendar.getInstance();
        Object converted = converter.convertToRemoting(calendar);
        Assert.assertNotNull(converted);
        Assert.assertEquals(((Calendar)converter.convertFromRemoting(converted)).getTime(), calendar.getTime());
    }

    @Test
    public void testConversionFixDate() throws ValueConverterException {
        CalendarConverterFactory factory = new CalendarConverterFactory();
        Converter converter = factory.getConverterForType(Calendar.class);

        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.set(2017, 2, 3,4, 5, 6);
        Object converted = converter.convertToRemoting(calendar);
        Assert.assertNotNull(converted);
        Assert.assertEquals(((Calendar)converter.convertFromRemoting(converted)).getTime(), calendar.getTime());
    }

    @Test
    public void testToRemotingConversionFixDate() throws ValueConverterException {
        CalendarConverterFactory factory = new CalendarConverterFactory();
        Converter converter = factory.getConverterForType(Calendar.class);

        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.set(2017, 2, 3,4, 5, 6);
        calendar.set(Calendar.MILLISECOND, 0);
        Object converted = converter.convertToRemoting(calendar);
        Assert.assertNotNull(converted);
        Assert.assertTrue(converted instanceof String);
        Assert.assertEquals(converted, "2017-03-03T04:05:06.000Z");
    }

    @Test
    public void testToRemotingConversionFixDateInGmt() throws ValueConverterException {
        CalendarConverterFactory factory = new CalendarConverterFactory();
        Converter converter = factory.getConverterForType(Calendar.class);

        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+1"));
        calendar.set(2017, 2, 3,4, 5, 6);
        calendar.set(Calendar.MILLISECOND, 0);
        Object converted = converter.convertToRemoting(calendar);
        Assert.assertNotNull(converted);
        Assert.assertTrue(converted instanceof String);
        Assert.assertEquals(converted, "2017-03-03T03:05:06.000Z");
    }

    @Test
    public void testFromRemotingConversionFixDate() throws ValueConverterException {
        CalendarConverterFactory factory = new CalendarConverterFactory();
        Converter converter = factory.getConverterForType(Calendar.class);

        String input = "2017-03-03T03:05:06.000Z";

        Object converted = converter.convertFromRemoting(input);
        Assert.assertNotNull(converted);
        Assert.assertTrue(converted instanceof Calendar);

        Calendar calendar = (Calendar) converted;

        Assert.assertEquals(calendar.getTimeZone(), TimeZone.getTimeZone("UTC"));
        Assert.assertEquals(calendar.get(Calendar.YEAR), 2017);
        Assert.assertEquals(calendar.get(Calendar.MONTH), 2);
        Assert.assertEquals(calendar.get(Calendar.DAY_OF_MONTH), 3);
        Assert.assertEquals(calendar.get(Calendar.HOUR), 3);
        Assert.assertEquals(calendar.get(Calendar.MINUTE), 5);
        Assert.assertEquals(calendar.get(Calendar.SECOND), 6);
        Assert.assertEquals(calendar.get(Calendar.MILLISECOND), 0);
    }

    @Test
    public void testFromRemotingConversionFixDateWithTimeZoneConversion() throws ValueConverterException {
        CalendarConverterFactory factory = new CalendarConverterFactory();
        Converter converter = factory.getConverterForType(Calendar.class);

        String input = "2017-03-03T03:05:06.000Z";

        Object converted = converter.convertFromRemoting(input);
        Assert.assertNotNull(converted);
        Assert.assertTrue(converted instanceof Calendar);

        Calendar calendar = (Calendar) converted;
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        Assert.assertEquals(calendar.getTimeZone(), TimeZone.getTimeZone("GMT+1"));
        Assert.assertEquals(calendar.get(Calendar.YEAR), 2017);
        Assert.assertEquals(calendar.get(Calendar.MONTH), 2);
        Assert.assertEquals(calendar.get(Calendar.DAY_OF_MONTH), 3);
        Assert.assertEquals(calendar.get(Calendar.HOUR), 4);
        Assert.assertEquals(calendar.get(Calendar.MINUTE), 5);
        Assert.assertEquals(calendar.get(Calendar.SECOND), 6);
        Assert.assertEquals(calendar.get(Calendar.MILLISECOND), 0);
    }
}
