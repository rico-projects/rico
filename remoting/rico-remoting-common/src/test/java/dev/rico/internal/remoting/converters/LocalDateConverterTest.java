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
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.TimeZone;

public class LocalDateConverterTest {

    @Test
    public void testSupportedType() {
        //given
        final LocalDateConverterFactory factory = new LocalDateConverterFactory();

        //then
        Assert.assertTrue(factory.supportsType(LocalDate.class));
        Assert.assertFalse(factory.supportsType(ZonedDateTime.class));
        Assert.assertFalse(factory.supportsType(LocalDateTime.class));
        Assert.assertFalse(factory.supportsType(LocalTime.class));
    }

    @Test
    public void testNullValue() throws ValueConverterException {
        //given
        final LocalDateConverterFactory factory = new LocalDateConverterFactory();
        final Converter converter = factory.getConverterForType(LocalDate.class);

        //when
        final Object rawObject = converter.convertToRemoting(null);
        final Object reConverted = converter.convertFromRemoting(rawObject);

        //then
        Assert.assertNull(reConverted);
    }

    @Test
    public void testSameTimeZone() throws ValueConverterException {
        //given
        final LocalDateConverterFactory factory = new LocalDateConverterFactory();
        final LocalDate time = LocalDate.now();
        final Converter converter = factory.getConverterForType(LocalDate.class);

        //when
        final Object rawObject = converter.convertToRemoting(time);
        System.out.println(rawObject);
        final Object reConverted = converter.convertFromRemoting(rawObject);

        //then
        Assert.assertNotNull(rawObject);
        Assert.assertNotNull(reConverted);
        Assert.assertTrue(LocalDate.class.isAssignableFrom(reConverted.getClass()));
        final LocalDate reconvertedTime = (LocalDate) reConverted;
        Assert.assertEquals(reconvertedTime, time);
    }

    @Test(enabled = false)
    public void testDifferentTimeZone() throws ValueConverterException {
        final TimeZone defaultZone = TimeZone.getDefault();
        try {

            //given
            final LocalDateConverterFactory factory = new LocalDateConverterFactory();
            final LocalDate time = LocalDate.now();
            final Converter converter = factory.getConverterForType(LocalDate.class);
            final TimeZone differentZone = Arrays.asList(TimeZone.getAvailableIDs()).stream()
                    .map(id -> TimeZone.getTimeZone(id))
                    .filter(zone -> !Objects.equals(defaultZone, zone))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("No time zone found"));

            //when
            final Object rawObject = converter.convertToRemoting(time);
            TimeZone.setDefault(differentZone);
            final Object reConverted = converter.convertFromRemoting(rawObject);

            //then
            Assert.assertNotNull(rawObject);
            Assert.assertNotNull(reConverted);
            Assert.assertTrue(LocalDate.class.isAssignableFrom(reConverted.getClass()));
            final LocalDate reconvertedTime = (LocalDate) reConverted;
            Assert.assertEquals(reconvertedTime, time);
        } finally {
            TimeZone.setDefault(defaultZone);
        }
    }

    @Test(enabled = false)
    public void testRawSameTimeZone() throws ValueConverterException, ParseException {
        final TimeZone defaultZone = TimeZone.getDefault();
        try {

            //given
            final LocalDateConverterFactory localDateFactory = new LocalDateConverterFactory();
            final Converter localDateConverter = localDateFactory.getConverterForType(LocalDate.class);
            final ZonedDateTimeConverterFactory zonedDateTimeFactory = new ZonedDateTimeConverterFactory();
            final Converter zonedDateTimeConverter = zonedDateTimeFactory.getConverterForType(LocalDate.class);
            final String rawObject = "2019-01-30T11:25:07.341+03:00";
            final ZonedDateTime zonedDateTime = (ZonedDateTime) zonedDateTimeConverter.convertFromRemoting(rawObject);
            final LocalDate fromZonedTime = LocalDate.from(zonedDateTime);

            //when
            final Object reconvertedLocalDate = localDateConverter.convertFromRemoting(rawObject);

            //then
            Assert.assertNotNull(reconvertedLocalDate);
            Assert.assertTrue(LocalDate.class.isAssignableFrom(reconvertedLocalDate.getClass()));
            final LocalDate reconvertedTime = (LocalDate) reconvertedLocalDate;
            Assert.assertEquals(reconvertedTime, fromZonedTime);

        } finally {
            TimeZone.setDefault(defaultZone);
        }
    }

}
