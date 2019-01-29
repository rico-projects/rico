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

import dev.rico.internal.remoting.converters.DateConverterFactory;
import dev.rico.remoting.converter.Converter;
import dev.rico.remoting.converter.ValueConverterException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

public class DateConverterFactoryTest {

    @Test
    public void testTypeIdentifier() {
        DateConverterFactory factory = new DateConverterFactory();
        Assert.assertEquals(factory.getTypeIdentifier(), DateConverterFactory.FIELD_TYPE_DATE);
    }

    @Test
    public void testTypeSupport() {
        DateConverterFactory factory = new DateConverterFactory();
        Assert.assertTrue(factory.supportsType(Date.class));
    }

    @Test
    public void testConverterCreation() {
        DateConverterFactory factory = new DateConverterFactory();
        Converter converter = factory.getConverterForType(Date.class);
        Assert.assertNotNull(converter);
    }

    @Test
    public void testConvertion() throws ValueConverterException {
        DateConverterFactory factory = new DateConverterFactory();
        Converter converter = factory.getConverterForType(Date.class);

        Date date = new Date();
        Object converted = converter.convertToRemoting(date);
        Assert.assertNotNull(converted);
        Assert.assertEquals(converter.convertFromRemoting(converted), date);
    }

}
