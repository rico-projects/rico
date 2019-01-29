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
package dev.rico.server;

import dev.rico.internal.server.client.HttpClientSessionImpl;
import dev.rico.server.util.HttpSessionMock;
import dev.rico.server.client.ClientSession;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ClientSessionImplTest {

    @Test
    public void testAddAttribute() {
        //given:
        ClientSession clientSession = new HttpClientSessionImpl(new HttpSessionMock());

        //when:
        clientSession.setAttribute("test-attribute", "Hello Client Session");

        //then:
        Assert.assertEquals(1, clientSession.getAttributeNames().size());
        Assert.assertTrue(clientSession.getAttributeNames().contains("test-attribute"));
        Assert.assertEquals("Hello Client Session", clientSession.getAttribute("test-attribute"));
    }

    @Test
    public void testNullAttribute() {
        //given:
        ClientSession clientSession = new HttpClientSessionImpl(new HttpSessionMock());

        //then:
        Assert.assertEquals(0, clientSession.getAttributeNames().size());
        Assert.assertNull(clientSession.getAttribute("test-attribute"));
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testImmutableAttributeSet() {
        //given:
        ClientSession clientSession = new HttpClientSessionImpl(new HttpSessionMock());

        //then:
        clientSession.getAttributeNames().add("att");
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testImmutableAttributeSet2() {
        //given:
        ClientSession clientSession = new HttpClientSessionImpl(new HttpSessionMock());

        //when:
        clientSession.setAttribute("test-attribute", "Hello Client Session");

        //then:
        clientSession.getAttributeNames().remove("test-attribute");
    }

    @Test
    public void testRemoveAttribute() {
        //given:
        ClientSession clientSession = new HttpClientSessionImpl(new HttpSessionMock());

        //when:
        clientSession.setAttribute("test-attribute", "Hello Client Session");
        clientSession.removeAttribute("test-attribute");

        //then:
        Assert.assertEquals(0, clientSession.getAttributeNames().size());
        Assert.assertNull(clientSession.getAttribute("test-attribute"));
    }

    @Test
    public void testMultipleAttributes() {
        //given:
        ClientSession clientSession = new HttpClientSessionImpl(new HttpSessionMock());

        //when:
        clientSession.setAttribute("test-attribute1", "Hello Client Session");
        clientSession.setAttribute("test-attribute2", "Yeah!");
        clientSession.setAttribute("test-attribute3", "Rico");

        //then:
        Assert.assertEquals(3, clientSession.getAttributeNames().size());
        Assert.assertTrue(clientSession.getAttributeNames().contains("test-attribute1"));
        Assert.assertTrue(clientSession.getAttributeNames().contains("test-attribute2"));
        Assert.assertTrue(clientSession.getAttributeNames().contains("test-attribute3"));
        Assert.assertEquals("Hello Client Session", clientSession.getAttribute("test-attribute1"));
        Assert.assertEquals("Yeah!", clientSession.getAttribute("test-attribute2"));
        Assert.assertEquals("Rico", clientSession.getAttribute("test-attribute3"));
    }

    @Test
    public void testInvalidate() {
        //given:
        ClientSession clientSession = new HttpClientSessionImpl(new HttpSessionMock());

        //when:
        clientSession.setAttribute("test-attribute1", "Hello Client Session");
        clientSession.setAttribute("test-attribute2", "Yeah!");
        clientSession.setAttribute("test-attribute3", "Rico");
        clientSession.invalidate();

        //then:
        Assert.assertEquals(0, clientSession.getAttributeNames().size());
        Assert.assertFalse(clientSession.getAttributeNames().contains("test-attribute1"));
        Assert.assertFalse(clientSession.getAttributeNames().contains("test-attribute2"));
        Assert.assertFalse(clientSession.getAttributeNames().contains("test-attribute3"));
        Assert.assertNull(clientSession.getAttribute("test-attribute1"));
        Assert.assertNull(clientSession.getAttribute("test-attribute2"));
        Assert.assertNull(clientSession.getAttribute("test-attribute3"));
    }
}
