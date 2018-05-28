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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.TimeZone;

import dev.rico.internal.remoting.converters.ValueFieldTypes;
import org.testng.annotations.Test;

import dev.rico.remoting.converter.Converter;
import dev.rico.remoting.converter.ValueConverterException;
import dev.rico.internal.remoting.Converters;
import dev.rico.internal.remoting.BeanRepository;

import mockit.Mocked;

public class LocalDateConverterFactoryTest {

    @Test
    public void testFactoryFieldType(@Mocked BeanRepository beanRepository) {
        // Given
        Converters converters = new Converters(beanRepository);

        // When
        int type = converters.getFieldType(LocalDate.class);

        // Then
        assertEquals(type, ValueFieldTypes.LOCAL_DATE_FIELD_TYPE);
    }

    @Test
    public void testConverterCreation(@Mocked BeanRepository beanRepository) {
        // Given
        Converters converters = new Converters(beanRepository);

        // When
        Converter converter = converters.getConverter(LocalDate.class);

        // Then
        assertNotNull(converter);
    }

    @Test
    public void testBasicConversions(@Mocked BeanRepository beanRepository) {
        // Given
        Converters converters = new Converters(beanRepository);

        // When
        Converter converter = converters.getConverter(LocalDate.class);

        // Then
        testReconversion(converter, LocalDate.now());
        testReconversion(converter, LocalDate.now(
                ZoneId.of(ZoneId.getAvailableZoneIds().iterator().next())));
        testReconversion(converter, LocalDate.now(ZoneId.of("GMT")));
        testReconversion(converter, LocalDate.now(ZoneId.of("Z")));
        testReconversion(converter, LocalDate.now(ZoneId.of("UTC+6")));

        testReconversion(converter, LocalDate.now().plusDays(1));
        testReconversion(converter, LocalDate.now().minusDays(2));

        testReconversion(converter, LocalDate.now().plusWeeks(1));
        testReconversion(converter, LocalDate.now().minusWeeks(2));

        testReconversion(converter, LocalDate.now().plusYears(1));
        testReconversion(converter, LocalDate.now().minusYears(2));

        testReconversion(converter,
                LocalDate.now().plusDays(3).plusWeeks(2).plusYears(1));
    }

    @Test
    public void testNullValues(@Mocked BeanRepository beanRepository) {
        // Given
        Converters converters = new Converters(beanRepository);

        // When
        Converter converter = converters.getConverter(LocalDate.class);

        // Then
        try {
            assertEquals(converter.convertFromRemoting(null), null);
            assertEquals(converter.convertToRemoting(null), null);
        } catch (ValueConverterException e) {
            fail("Error in conversion", e);
        }
    }

    @Test(expectedExceptions = ClassCastException.class)
    public void testWrongRemotingValues(@Mocked BeanRepository beanRepository)
            throws ValueConverterException {
        // Given
        Converters converters = new Converters(beanRepository);

        // When
        Converter converter = converters.getConverter(LocalDate.class);

        // Then
        converter.convertFromRemoting(7);
    }

    @Test(expectedExceptions = ClassCastException.class)
    public void testWrongBeanValues(@Mocked BeanRepository beanRepository)
            throws ValueConverterException {
        // Given
        Converters converters = new Converters(beanRepository);

        // When
        Converter converter = converters.getConverter(LocalDate.class);

        // Then
        converter.convertToRemoting(7);
    }

    private void testReconversion(Converter converter, LocalDate time) {
        try {
            Object remotingObject = converter.convertToRemoting(time);
            assertNotNull(remotingObject);
            TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC-3")));
            Object reconvertedObject = converter
                    .convertFromRemoting(remotingObject);
            assertNotNull(reconvertedObject);
            assertEquals(reconvertedObject.getClass(), LocalDate.class);
            LocalDate reverted = (LocalDate) reconvertedObject;
            assertEquals(reverted, time);
        } catch (ValueConverterException e) {
            fail("Error in conversion");
        }
    }
}
