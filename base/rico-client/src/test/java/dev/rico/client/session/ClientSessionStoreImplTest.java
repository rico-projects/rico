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

import dev.rico.internal.client.session.ClientSessionStoreImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URL;

public class ClientSessionStoreImplTest {
    private ClientSessionStoreImpl implement;
    private String clientID;

    @Test
    public void getClientIdentifierForUrlWhenGivenAURL() throws Exception {
        //GIVEN
        implement = new ClientSessionStoreImpl();
        URL url = new URL("http://example.com/80");
        clientID = "myId";
        implement.setClientIdentifierForUrl(url.toURI(), clientID);
        //when
        final String result = implement.getClientIdentifierForUrl(url.toURI());
        //then
        Assert.assertEquals(result, clientID);
    }

    @Test
    public void setClientIdentifierForUrl() throws Exception {
        //GIVEN
        implement = new ClientSessionStoreImpl();
        //when
        URL url = new URL("http://example.com/80");
        String clientID = "myId";
        implement.setClientIdentifierForUrl(url.toURI(), clientID);
        //then
        final String setUrl = implement.getClientIdentifierForUrl(url.toURI());
        Assert.assertEquals(setUrl, clientID);
    }

    @Test
    public void getClientIdentifierForUrlWhenGiven2URL() throws Exception {
        //GIVEN
        implement = new ClientSessionStoreImpl();
        URL url = new URL("http://amazon.com/80");
        URL url1 = new URL("http://apple.com/80");

        clientID = "amzn";
        String clientID1 = "appl";

        implement.setClientIdentifierForUrl(url.toURI(), clientID);
        implement.setClientIdentifierForUrl(url1.toURI(), clientID1);

        //when
        final String result = implement.getClientIdentifierForUrl(url.toURI());
        final String result1 = implement.getClientIdentifierForUrl(url1.toURI());
        //then
        Assert.assertEquals(result, clientID);
        Assert.assertEquals(result1, clientID1);
    }

    @Test
    public void setClientIdentifierFor2Url() throws Exception {
        //GIVEN
        implement = new ClientSessionStoreImpl();
        //when
        URL url = new URL("http://amazon.com/80");
        URL url1 = new URL("http://apple.com/80");

        String clientID = "amzn";
        String clientID1 = "appl";

        implement.setClientIdentifierForUrl(url.toURI(), clientID);
        implement.setClientIdentifierForUrl(url1.toURI(), clientID1);

        //then
        final String setUrl = implement.getClientIdentifierForUrl(url.toURI());
        final String setUrl1 = implement.getClientIdentifierForUrl(url1.toURI());

        Assert.assertEquals(setUrl, clientID);
        Assert.assertEquals(setUrl1, clientID1);
    }

    @Test
    public void getClientIdentifierForUrlWhenGiven10URL() throws Exception {
        //GIVEN
        implement = new ClientSessionStoreImpl();
        URL url = new URL("http://amazon.com/80");
        URL url1 = new URL("http://apple.com/80");
        URL url2 = new URL("http://microsoft.com/80");
        URL url3 = new URL("http://google.com/80");
        URL url4 = new URL("http://yahoo.com/80");
        URL url5 = new URL("http://netflix.com/80");
        URL url6 = new URL("http://youtube.com/80");
        URL url7 = new URL("http://9to5mac.com/80");
        URL url8 = new URL("http://github.com/80");
        URL url9 = new URL("http://facebook.com/80");

        clientID = "amzn";
        String clientID1 = "appl";
        String clientID2 = "mfst";
        String clientID3 = "googl";
        String clientID4 = "yhoo";
        String clientID5 = "ntflx";
        String clientID6 = "ytub";
        String clientID7 = "9to5";
        String clientID8 = "gthb";
        String clientID9 = "";

        implement.setClientIdentifierForUrl(url.toURI(), clientID);
        implement.setClientIdentifierForUrl(url1.toURI(), clientID1);
        implement.setClientIdentifierForUrl(url2.toURI(), clientID2);
        implement.setClientIdentifierForUrl(url3.toURI(), clientID3);
        implement.setClientIdentifierForUrl(url4.toURI(), clientID4);
        implement.setClientIdentifierForUrl(url5.toURI(), clientID5);
        implement.setClientIdentifierForUrl(url6.toURI(), clientID6);
        implement.setClientIdentifierForUrl(url7.toURI(), clientID7);
        implement.setClientIdentifierForUrl(url8.toURI(), clientID8);
        implement.setClientIdentifierForUrl(url9.toURI(), clientID9);


        //when
        final String result = implement.getClientIdentifierForUrl(url.toURI());
        final String result1 = implement.getClientIdentifierForUrl(url1.toURI());
        final String result2 = implement.getClientIdentifierForUrl(url2.toURI());
        final String result3 = implement.getClientIdentifierForUrl(url3.toURI());
        final String result4 = implement.getClientIdentifierForUrl(url4.toURI());
        final String result5 = implement.getClientIdentifierForUrl(url5.toURI());
        final String result6 = implement.getClientIdentifierForUrl(url6.toURI());
        final String result7 = implement.getClientIdentifierForUrl(url7.toURI());
        final String result8 = implement.getClientIdentifierForUrl(url8.toURI());
        final String result9 = implement.getClientIdentifierForUrl(url9.toURI());
        //then
        Assert.assertEquals(result, clientID);
        Assert.assertEquals(result1, clientID1);
        Assert.assertEquals(result2, clientID2);
        Assert.assertEquals(result3, clientID3);
        Assert.assertEquals(result4, clientID4);
        Assert.assertEquals(result5, clientID5);
        Assert.assertEquals(result6, clientID6);
        Assert.assertEquals(result7, clientID7);
        Assert.assertEquals(result8, clientID8);
        Assert.assertEquals(result9, clientID9);
    }

    @Test
    public void setClientIdentifierFor10Url() throws Exception {
        //GIVEN
        implement = new ClientSessionStoreImpl();
        //when
        URL url = new URL("http://amazon.com/80");
        URL url1 = new URL("http://apple.com/80");
        URL url2 = new URL("http://microsoft.com/80");
        URL url3 = new URL("http://google.com/80");
        URL url4 = new URL("http://yahoo.com/80");
        URL url5 = new URL("http://netflix.com/80");
        URL url6 = new URL("http://youtube.com/80");
        URL url7 = new URL("http://9to5mac.com/80");
        URL url8 = new URL("http://github.com/80");
        URL url9 = new URL("http://facebook.com/80");

        clientID = "amzn";
        String clientID1 = "appl";
        String clientID2 = "mfst";
        String clientID3 = "googl";
        String clientID4 = "yhoo";
        String clientID5 = "ntflx";
        String clientID6 = "ytub";
        String clientID7 = "9to5";
        String clientID8 = "gthb";
        String clientID9 = "";

        implement.setClientIdentifierForUrl(url.toURI(), clientID);
        implement.setClientIdentifierForUrl(url1.toURI(), clientID1);
        implement.setClientIdentifierForUrl(url2.toURI(), clientID2);
        implement.setClientIdentifierForUrl(url3.toURI(), clientID3);
        implement.setClientIdentifierForUrl(url4.toURI(), clientID4);
        implement.setClientIdentifierForUrl(url5.toURI(), clientID5);
        implement.setClientIdentifierForUrl(url6.toURI(), clientID6);
        implement.setClientIdentifierForUrl(url7.toURI(), clientID7);
        implement.setClientIdentifierForUrl(url8.toURI(), clientID8);
        implement.setClientIdentifierForUrl(url9.toURI(), clientID9);

        //then
        final String set = implement.getClientIdentifierForUrl(url.toURI());
        final String set1 = implement.getClientIdentifierForUrl(url1.toURI());
        final String set2 = implement.getClientIdentifierForUrl(url2.toURI());
        final String set3 = implement.getClientIdentifierForUrl(url3.toURI());
        final String set4 = implement.getClientIdentifierForUrl(url4.toURI());
        final String set5 = implement.getClientIdentifierForUrl(url5.toURI());
        final String set6 = implement.getClientIdentifierForUrl(url6.toURI());
        final String set7 = implement.getClientIdentifierForUrl(url7.toURI());
        final String set8 = implement.getClientIdentifierForUrl(url8.toURI());
        final String set9 = implement.getClientIdentifierForUrl(url9.toURI());

        Assert.assertEquals(set, clientID);
        Assert.assertEquals(set1, clientID1);
        Assert.assertEquals(set2, clientID2);
        Assert.assertEquals(set3, clientID3);
        Assert.assertEquals(set4, clientID4);
        Assert.assertEquals(set5, clientID5);
        Assert.assertEquals(set6, clientID6);
        Assert.assertEquals(set7, clientID7);
        Assert.assertEquals(set8, clientID8);
        Assert.assertEquals(set9, clientID9);
    }


    @Test
    public void TestSessionIsReset() throws Exception {
        //GIVEN
        implement = new ClientSessionStoreImpl();
        //when
        URL url = new URL("http://example.com/80");
        implement.resetSession(url.toURI());

        //then
        final String setUrl = implement.getClientIdentifierForUrl(url.toURI());
        Assert.assertEquals(setUrl, null);

    }

    @Test
    public  void TestSessionIsResetForMoreThanOn1() throws Exception{
        implement = new ClientSessionStoreImpl();

        URL url1 = new URL("http://apple.com/80");
        URL url2 = new URL("http://microsoft.com/80");
        URL url3 = new URL("http://google.com/80");
        URL url4 = new URL("http://yahoo.com/80");
        URL url5 = new URL("http://netflix.com/80");


        implement.resetSession(url1.toURI());
        implement.resetSession(url2.toURI());
        implement.resetSession(url3.toURI());
        implement.resetSession(url4.toURI());
        implement.resetSession(url5.toURI());

        final String reset1 = implement.getClientIdentifierForUrl(url1.toURI());
        final String reset2 = implement.getClientIdentifierForUrl(url2.toURI());
        final String reset3 = implement.getClientIdentifierForUrl(url3.toURI());
        final String reset4 = implement.getClientIdentifierForUrl(url4.toURI());
        final String reset5 = implement.getClientIdentifierForUrl(url5.toURI());

        Assert.assertEquals(reset1, null);
        Assert.assertEquals(reset2, null);
        Assert.assertEquals(reset3, null);
        Assert.assertEquals(reset4, null);
        Assert.assertEquals(reset5, null);


    }
}
