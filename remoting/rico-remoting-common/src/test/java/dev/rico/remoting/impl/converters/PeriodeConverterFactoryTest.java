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
package dev.rico.remoting.impl.converters;

import dev.rico.internal.remoting.repo.Repository;
import dev.rico.remoting.converter.Converter;
import dev.rico.remoting.converter.ValueConverterException;
import dev.rico.internal.remoting.communication.converters.Converters;
import dev.rico.internal.remoting.communication.converters.ValueFieldTypes;
import mockit.Mocked;
import org.testng.annotations.Test;

import java.time.Period;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

public class PeriodeConverterFactoryTest {

    @Test
    public void testFactoryFieldType(@Mocked Repository beanRepository) {
        //Given
        Converters converters = new Converters(beanRepository);

        //When
        int type = converters.getFieldType(Period.class);

        //Then
        assertEquals(type, ValueFieldTypes.PERIODE_FIELD_TYPE);
    }

    @Test
    public void testConverterCreation(@Mocked Repository beanRepository) {
        //Given
        Converters converters = new Converters(beanRepository);

        //When
        Converter converter = converters.getConverter(Period.class);

        //Then
        assertNotNull(converter);
    }

    @Test
    public void testBasicConversions(@Mocked Repository beanRepository) {
        //Given
        Converters converters = new Converters(beanRepository);

        //When
        Converter converter = converters.getConverter(Period.class);

        //Then
        testReconversion(converter, Period.ofDays(7));
        testReconversion(converter, Period.ofYears(700_000_000));
        testReconversion(converter, Period.ofWeeks(70_000_000));
        testReconversion(converter, Period.ZERO);
        testReconversion(converter, Period.ofDays(10_000_000));
    }

    @Test
    public void testNullValues(@Mocked Repository beanRepository) {
        //Given
        Converters converters = new Converters(beanRepository);

        //When
        Converter converter = converters.getConverter(Period.class);

        //Then
        try {
            assertEquals(converter.convertFromRemoting(null), null);
            assertEquals(converter.convertToRemoting(null), null);
        } catch (ValueConverterException e) {
            fail("Error in conversion", e);
        }
    }

    @Test(expectedExceptions = ClassCastException.class)
    public void testWrongValues(@Mocked Repository beanRepository) throws ValueConverterException{
        //Given
        Converters converters = new Converters(beanRepository);

        //When
        Converter converter = converters.getConverter(Period.class);

        //Then
        converter.convertFromRemoting(7);
    }

    @Test(expectedExceptions = ClassCastException.class)
    public void testWrongBeanValues(@Mocked Repository beanRepository) throws ValueConverterException{
        //Given
        Converters converters = new Converters(beanRepository);

        //When
        Converter converter = converters.getConverter(Period.class);

        //Then
        converter.convertToRemoting(7);
    }

    private void testReconversion(Converter converter, Period duration) {
        try {
            Object remotingObject = converter.convertToRemoting(duration);
            assertNotNull(remotingObject);
            Object reconvertedObject = converter.convertFromRemoting(remotingObject);
            assertNotNull(reconvertedObject);
            assertEquals(reconvertedObject.getClass(), Period.class);
            Period reconvertedDuration = (Period) reconvertedObject;
            assertEquals(reconvertedDuration, duration);
        } catch (ValueConverterException e) {
            fail("Error in conversion", e);
        }
    }
}
