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
package dev.rico.client.session;

import dev.rico.internal.client.session.SimpleUrlToAppDomainConverter;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URL;

public class SimpleUrlToAppDomainConverterTest {

    private SimpleUrlToAppDomainConverter simpleUrlToAppDomainConverter;

    private static final String PROTOCOL = "http";
    private static final String HOST = "example.com";
    private static final int PORT = 80;

    @BeforeMethod
    public void setUp() throws Exception {
        simpleUrlToAppDomainConverter = new SimpleUrlToAppDomainConverter();
    }

    @Test
    public void getApplicationDomain_whenGivenAURL_thenConvertAppDomainCorrectly() throws Exception {
        // GIVEN
        URL url = new URL(PROTOCOL, HOST, PORT, "");

        // WHEN
        String result = simpleUrlToAppDomainConverter.apply(url.toURI());

        // THEN
        Assert.assertEquals(result, HOST + ":" + PORT);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void getApplicationDomain_whenGivenANullURL_thenThrownANullPointerException() throws Exception {
        // WHEN
        simpleUrlToAppDomainConverter.apply(null);
    }
}