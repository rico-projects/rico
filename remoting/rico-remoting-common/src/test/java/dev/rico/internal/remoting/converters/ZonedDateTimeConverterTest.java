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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.TimeZone;

public class ZonedDateTimeConverterTest {

    @Test
    public void testSupportedType() {
        //given
        final ZonedDateTimeConverterFactory factory = new ZonedDateTimeConverterFactory();

        //then
        Assert.assertTrue(factory.supportsType(ZonedDateTime.class));
        Assert.assertFalse(factory.supportsType(LocalDateTime.class));
    }

    @Test
    public void testNullValue() throws ValueConverterException {
        //given
        final ZonedDateTimeConverterFactory factory = new ZonedDateTimeConverterFactory();
        final Converter converter = factory.getConverterForType(ZonedDateTime.class);

        //when
        final Object rawObject = converter.convertToRemoting(null);
        final Object reConverted = converter.convertFromRemoting(rawObject);

        //then
        Assert.assertNull(reConverted);
    }

    @Test
    public void testSameTimeZone() throws ValueConverterException {
        //given
        final ZonedDateTimeConverterFactory factory = new ZonedDateTimeConverterFactory();
        final ZonedDateTime time = ZonedDateTime.now();
        final Converter converter = factory.getConverterForType(ZonedDateTime.class);

        //when
        final Object rawObject = converter.convertToRemoting(time);
        System.out.println("FORMAT: " + rawObject);
        System.out.println("BASE NANOS: " + time.getNano());
        final Object reConverted = converter.convertFromRemoting(rawObject);
        System.out.println("CONVERTED NANOS: " + ((ZonedDateTime)reConverted).getNano());
        //then
        Assert.assertNotNull(rawObject);
        Assert.assertNotNull(reConverted);
        Assert.assertTrue(ZonedDateTime.class.isAssignableFrom(reConverted.getClass()));
        final ZonedDateTime reconvertedTime = (ZonedDateTime) reConverted;
        Assert.assertEquals(reconvertedTime, time);
    }

    @Test
    public void testDifferentTimeZone() throws ValueConverterException {
        //given
        final ZonedDateTimeConverterFactory factory = new ZonedDateTimeConverterFactory();
        final ZoneId currentZoneId = ZoneId.systemDefault();
        final ZoneId differentZoneId = ZoneId.getAvailableZoneIds().stream()
                .map(i -> ZoneId.of(i))
                .filter(zoneId -> !Objects.equals(zoneId, currentZoneId))
                .findAny()
                .orElseThrow(() -> new RuntimeException("No Zone ID found!"));
        final ZonedDateTime time = ZonedDateTime.now(differentZoneId);
        final Converter converter = factory.getConverterForType(ZonedDateTime.class);

        //when
        final Object rawObject = converter.convertToRemoting(time);
        System.out.println(rawObject);
        final Object reConverted = converter.convertFromRemoting(rawObject);

        //then
        Assert.assertNotNull(rawObject);
        Assert.assertNotNull(reConverted);
        Assert.assertTrue(ZonedDateTime.class.isAssignableFrom(reConverted.getClass()));
        final ZonedDateTime reconvertedTime = (ZonedDateTime) reConverted;
        Assert.assertEquals(reconvertedTime, time);
    }

    @Test(enabled = false)
    public void testRawSameTimeZone() throws ValueConverterException, ParseException {
        final TimeZone defaultZone = TimeZone.getDefault();
        try {

            //given
            final ZonedDateTimeConverterFactory zonedDateTimeFactory = new ZonedDateTimeConverterFactory();
            final Converter zonedDateTimeConverter = zonedDateTimeFactory.getConverterForType(LocalDate.class);
            final String rawObject = "2019-01-30T11:25:07.341+08:00";

            //when
            final Object reconverted = zonedDateTimeConverter.convertFromRemoting(rawObject);

            //then
            Assert.assertNotNull(reconverted);
            Assert.assertTrue(ZonedDateTime.class.isAssignableFrom(reconverted.getClass()));
            final ZonedDateTime reconvertedTime = (ZonedDateTime) reconverted;
            Assert.assertEquals(ZoneId.of("Australia/Sydney"), reconvertedTime.getZone());

        } finally {
            TimeZone.setDefault(defaultZone);
        }
    }

}
