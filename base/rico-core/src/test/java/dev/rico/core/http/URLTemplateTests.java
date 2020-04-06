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
package dev.rico.core.http;

import dev.rico.internal.core.http.URLParams;
import dev.rico.internal.core.http.URLTemplate;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class URLTemplateTests {

    @Test
    public void testSimpleUrl() {
        //given:
        final String value = "http://www.example.org";
        final URLTemplate template = URLTemplate.of(value);

        //when:
        final String urlString = template.createString();
        final URL url = template.create();

        //then:
        Assert.assertNotNull(urlString);
        Assert.assertEquals(urlString, value);

        Assert.assertNotNull(url);
        Assert.assertEquals(url.toString(), value);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullUrl() {
        //when:
        final String value = null;

        //when:
        URLTemplate.of(value);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testBadUrl() {
        //given:
        final String value = "www.example.org";
        final URLTemplate template = URLTemplate.of(value);

        //when:
        template.create();
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testBadUrlForString() {
        //given:
        final String value = "www.example.org";
        final URLTemplate template = URLTemplate.of(value);

        //when:
        template.createString();
    }

    @Test
    public void testSimpleParam() {
        //given:
        final String value = "http://www.example.org/id={id}";
        final URLTemplate template = URLTemplate.of(value);

        //when:
        final String urlString = template.createString("id", "123");
        final URL url = template.create("id", "123");

        //then:
        Assert.assertNotNull(urlString);
        Assert.assertEquals(urlString, "http://www.example.org/id=123");

        Assert.assertNotNull(url);
        Assert.assertEquals(url.toString(), "http://www.example.org/id=123");
    }

    @Test
    public void testIntParam() {
        //given:
        final String value = "http://www.example.org/id={id}";
        final URLTemplate template = URLTemplate.of(value);

        //when:
        final String urlString = template.createString("id", 123);
        final URL url = template.create("id", 123);

        //then:
        Assert.assertNotNull(urlString);
        Assert.assertEquals(urlString, "http://www.example.org/id=123");

        Assert.assertNotNull(url);
        Assert.assertEquals(url.toString(), "http://www.example.org/id=123");
    }

    @Test
    public void testLongParam() {
        //given:
        final String value = "http://www.example.org/id={id}";
        final URLTemplate template = URLTemplate.of(value);

        //when:
        final String urlString = template.createString("id", 123L);
        final URL url = template.create("id", 123L);

        //then:
        Assert.assertNotNull(urlString);
        Assert.assertEquals(urlString, "http://www.example.org/id=123");

        Assert.assertNotNull(url);
        Assert.assertEquals(url.toString(), "http://www.example.org/id=123");
    }

    @Test
    public void testMultipleParam() {
        //given:
        final String value = "http://www.example.org/{type}/id={id}";
        final URLTemplate template = URLTemplate.of(value);
        final Map<String, String> values = new HashMap<>();
        values.put("type", "data");
        values.put("id", "123");

        //when:
        final String urlString = template.createString(values);
        final URL url = template.create(values);

        //then:
        Assert.assertNotNull(urlString);
        Assert.assertEquals(urlString, "http://www.example.org/data/id=123");

        Assert.assertNotNull(url);
        Assert.assertEquals(url.toString(), "http://www.example.org/data/id=123");
    }

    @Test
    public void testSameParamMultipleTimes() {
        //given:
        final String value = "http://www.example.org/{id}/id={id}";
        final URLTemplate template = URLTemplate.of(value);

        //when:
        final String urlString = template.createString("id", "123");
        final URL url = template.create("id", "123");

        //then:
        Assert.assertNotNull(urlString);
        Assert.assertEquals(urlString, "http://www.example.org/123/id=123");

        Assert.assertNotNull(url);
        Assert.assertEquals(url.toString(), "http://www.example.org/123/id=123");
    }

    @Test
    public void testWithParams() {
        //given:
        final String value = "http://www.example.org/{type}/id={id}";
        final URLTemplate template = URLTemplate.of(value);
        final URLParams params = URLParams.of("type", "data").and("id", 123);

        //when:
        final String urlString = template.createString(params);
        final URL url = template.create(params);

        //then:
        Assert.assertNotNull(urlString);
        Assert.assertEquals(urlString, "http://www.example.org/data/id=123");

        Assert.assertNotNull(url);
        Assert.assertEquals(url.toString(), "http://www.example.org/data/id=123");
    }
}
